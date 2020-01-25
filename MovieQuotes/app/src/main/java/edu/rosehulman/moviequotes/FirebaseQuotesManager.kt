package edu.rosehulman.moviequotes

import android.util.Log
import com.google.firebase.firestore.*

object FirebaseQuotesManager {
    private val movieQuotes = ArrayList<MovieQuote>()
    private lateinit var quotesRef: CollectionReference
    private lateinit var quotesRegistration: ListenerRegistration
    private var observer: Observer? = null

    fun setObserver(observer: Observer) {
        this.observer = observer
    }

    fun beginListening() {
        quotesRef = FirebaseFirestore
            .getInstance()
            .collection(Constants.QUOTES_COLLECTION)

        quotesRegistration = quotesRef
            .orderBy(MovieQuote.CREATED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e(Constants.TAG, "Error: $exception")
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges) {
                    val mq = MovieQuote.from(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            movieQuotes.add(0, mq)
                            observer?.notifyItemInserted(0)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val pos = movieQuotes.indexOfFirst { it.id == mq.id }
                            movieQuotes.removeAt(pos)
                            observer?.notifyItemRemoved(pos)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val pos = movieQuotes.indexOfFirst { it.id == mq.id }
                            movieQuotes[pos] = mq
                            observer?.notifyItemChanged(pos)
                        }
                    }
                }
            }
    }

    fun stopListening() {
        Log.d(Constants.TAG, "Removing listener")
        quotesRegistration.remove()
    }


    fun getNumQuotes() = movieQuotes.size
    fun getQuoteAt(position: Int) = movieQuotes[position]

    fun add(movieQuote: MovieQuote) {
        quotesRef.add(movieQuote)
    }

    fun edit(quote: String, movie: String, position: Int) {
        movieQuotes[position].quote = quote
        movieQuotes[position].movie = movie
        quotesRef.document(movieQuotes[position].id).set(movieQuotes[position])
    }

    fun delete(position: Int) {
        quotesRef.document(movieQuotes[position].id).delete()
    }

    fun select(position: Int) {
        movieQuotes[position].isSelected = !movieQuotes[position].isSelected
        quotesRef.document(movieQuotes[position].id).set(movieQuotes[position])
    }

    interface Observer {
        // Interestingly, if we use the same names as the
        // RecyclerView.Adapter methods, then our custom MovieQuote adapter
        // already inherits them from RecyclerView.Adapter.
        fun notifyItemInserted(position: Int)
        fun notifyItemRemoved(position: Int)
        fun notifyItemChanged(position: Int)
    }
}