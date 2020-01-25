package edu.rosehulman.moviequotes

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class MovieQuote(
    var quote: String = "Some quote",
    var movie: String = "Some movie",
    var isSelected: Boolean = false
    ) {
    @get:Exclude var id = ""
    @ServerTimestamp var created: Timestamp? = null

    companion object {
        const val CREATED_KEY = "created"
        fun from(snapshot: DocumentSnapshot): MovieQuote {
            val mq = snapshot.toObject(MovieQuote::class.java)!!
            mq.id = snapshot.id
            return mq
        }
    }
}
