package edu.rosehulman.moviequotes

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.dialog_add.view.*

class MovieQuoteAdapter(private val context: Context) :
    RecyclerView.Adapter<MovieQuoteViewHolder>(),
    FirebaseQuotesManager.Observer
{
    init {
        FirebaseQuotesManager.setObserver(this)
        FirebaseQuotesManager.beginListening()
    }

    fun stopListening() {
        FirebaseQuotesManager.stopListening()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieQuoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view, null, false)
        return MovieQuoteViewHolder(view, this, context)
    }

    override fun getItemCount() = FirebaseQuotesManager.getNumQuotes()

    override fun onBindViewHolder(holder: MovieQuoteViewHolder, position: Int) {
        val movieQuote = FirebaseQuotesManager.getQuoteAt(position)
        holder.bind(movieQuote)
    }

    fun showAddEditDialog(position: Int) {
        // pos of -1 means add
        val builder = AlertDialog.Builder(context)
        builder.setTitle(if (position < 0) R.string.add_dialog_title else R.string.edit_dialog_title)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null, false)
        builder.setView(view)
        if (position >= 0) {
            val mq = FirebaseQuotesManager.getQuoteAt(position)
            view.quote_edit_text.setText(mq.quote)
            view.movie_edit_text.setText(mq.movie)
        }
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
            val quote = view.quote_edit_text.text.toString()
            val movie = view.movie_edit_text.text.toString()
            val movieQuote = MovieQuote(quote, movie)
            if (position < 0) {
                add(movieQuote)
            } else {
                edit(quote, movie, position)
            }
        }
        if (position >= 0) {
            builder.setNeutralButton("Delete") { _, _ ->
                delete(position)

            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    fun add(movieQuote: MovieQuote) {
        FirebaseQuotesManager.add(movieQuote)
    }

    private fun edit(quote: String, movie: String, position: Int) {
        FirebaseQuotesManager.edit(quote, movie, position)
    }

    private fun delete(position: Int) {
        FirebaseQuotesManager.delete(position)
    }

    fun select(position: Int) {
        FirebaseQuotesManager.select(position)
    }
}