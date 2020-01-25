package edu.rosehulman.moviequotes

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MovieQuoteAdapter
    val settingsRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.SETTINGS_COLLECTION)
        .document(Constants.SETTINGS_DOCUMENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            Log.d(Constants.TAG, "Pressed the FAB")
            adapter.showAddEditDialog(-1)
        }

        adapter = MovieQuoteAdapter(this)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        settingsRef.addSnapshotListener {snapshot: DocumentSnapshot?, exception: FirebaseFirestoreException? ->
            if (exception != null) {
                return@addSnapshotListener
            }
            toolbar.title = (snapshot!!["author"] ?: "") as String
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter.stopListening()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                // startActivity(Intent(Settings.ACTION_SETTINGS))
                getWhichSettings()
                true
            }
            R.id.action_show_favorite_movie -> {
                showFavoriteMovie()
                true
            }
            R.id.action_show_favorite_quote -> {
                showFavoriteQuote()
                true
            }
            R.id.action_show_all_quotes -> {
                showAllQuotes()
                true
            }
            R.id.action_set_author -> {
                setAuthor()
                true
            }
//            R.id.action_increase_font -> {
////                changeFontSize(4)
//                true
//            }
//            R.id.action_decrease_font -> {
////                changeFontSize(-4)
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFavoriteMovie() {
        val favoriteMovieQuoteReference = FirebaseFirestore
            .getInstance()
            .collection("favorites")
            .document("moviequote")
        // get() returns a Task
        favoriteMovieQuoteReference.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            val movie = (snapshot["movie"] ?: "") as String
            Toast.makeText(this, "Movie: $movie", Toast.LENGTH_LONG).show()
        }
    }

    private fun showFavoriteQuote() {
        val favoriteMovieQuoteReference = FirebaseFirestore
            .getInstance()
            .collection("favorites")
            .document("moviequote")
        favoriteMovieQuoteReference.get().addOnSuccessListener { snapshot: DocumentSnapshot ->
            val mq = snapshot.toObject(MovieQuote::class.java)!!
            // Toast.makeText(this, "Quote: $quote", Toast.LENGTH_LONG).show()
            adapter.add(mq)
        }
    }

    private fun showAllQuotes() {
        val quotesRef = FirebaseFirestore
            .getInstance()
            .collection("quotes")
        quotesRef.get().addOnSuccessListener { snapshot: QuerySnapshot ->
            for (doc in snapshot) {
                val mq = doc.toObject(MovieQuote::class.java)
                Log.d(Constants.TAG, "Quote: $mq")
            }
        }
    }

    private fun setAuthor() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.layout_edit_app_title)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null, false) as EditText
        builder.setView(view)
        view.setText(toolbar.title)
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val author: String = view.text.toString()
            val map = mapOf("author" to author)
            settingsRef.set(map)
        }
        builder.setNegativeButton(android.R.string.cancel, null)

        builder.create().show()

    }

    private fun getWhichSettings() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.settings_dialog_title)
        builder.setItems(R.array.settings_choices) { _: DialogInterface?, which: Int ->
            val settingsConstant = when (which) {
                0 -> Settings.ACTION_SOUND_SETTINGS
                1 -> Settings.ACTION_SEARCH_SETTINGS
                else -> Settings.ACTION_SETTINGS
            }
            startActivity(Intent(settingsConstant))
        }
        builder.create().show()
    }


//    private fun changeFontSize(delta: Int) {
//        var currentSize = quote_text_view.textSize / resources.displayMetrics.scaledDensity
//        currentSize += delta
//        quote_text_view.textSize = currentSize
//        movie_text_view.textSize = currentSize
//    }

}
