package edu.rosehulman.moviequotes

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_view.view.*

class MovieQuoteViewHolder(
    itemView: View,
    adapter: MovieQuoteAdapter,
    val context: Context
): RecyclerView.ViewHolder(itemView) {
    val quoteTextView = itemView.quote_text_view
    val movieTextView = itemView.movie_text_view

    init {
        itemView.setOnClickListener {
            adapter.showAddEditDialog(adapterPosition)
        }

        itemView.setOnLongClickListener {
            adapter.select(adapterPosition)
            true
        }
    }

    fun bind(movieQuote: MovieQuote) {
        quoteTextView.text = movieQuote.quote
        movieTextView.text = movieQuote.movie
        val color = if (movieQuote.isSelected) {
            ContextCompat.getColor(context,R.color.colorAccent)
        } else {
            Color.WHITE
        }
        itemView.setBackgroundColor(color)
    }
}