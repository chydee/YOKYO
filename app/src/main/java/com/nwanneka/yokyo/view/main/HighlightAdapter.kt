package com.nwanneka.yokyo.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.core.GlideApp
import com.nwanneka.yokyo.data.Highlight
import com.nwanneka.yokyo.databinding.ItemHighlightBinding
import java.util.*
import kotlin.properties.Delegates

class HighlightAdapter : RecyclerView.Adapter<HighlightAdapter.HighlightViewHolder>(), AutoUpdatableAdapter, Filterable {

    interface OnItemClickListener {
        fun onHighlightClick(highlight: Highlight)
        fun onShareClicked(content: String)
    }


    private var items: ArrayList<Highlight> by Delegates.observable(ArrayList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    private lateinit var listener: OnItemClickListener

    private lateinit var itemsFilter: MutableList<Highlight>

    inner class HighlightViewHolder(private val binding: ItemHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(highlight: Highlight) {
            binding.highlightTitle.text = highlight.title
            GlideApp.with(binding.root.context)
                .load(highlight.thumbnail)
                .centerCrop()
                .placeholder(R.drawable.waha)
                .into(binding.highlightThumbNail)
            binding.btnShareHighlight.setOnClickListener {
                listener.onShareClicked("${highlight.title}: http://www.youtube.com/watch?v=${highlight.videoID}")
            }
            binding.btnPlayVideo.setOnClickListener {
                listener.onHighlightClick(highlight)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighlightViewHolder {
        return from(parent)
    }

    private fun from(parent: ViewGroup): HighlightViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemHighlightBinding.inflate(layoutInflater, parent, false)
        return HighlightViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HighlightViewHolder, position: Int) {
        val color = items[position]
        holder.bind(color)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Highlight>() {
        override fun areContentsTheSame(oldItem: Highlight, newItem: Highlight): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: Highlight, newItem: Highlight): Boolean {
            return oldItem == newItem
        }
    }

    internal fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    internal fun submitList(highlights: ArrayList<Highlight>) {
        this.items = highlights
        itemsFilter = ArrayList(highlights)
    }

    override fun getFilter(): Filter {
        return itemFilter
    }

    private val itemFilter: Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val filteredList: MutableList<Highlight> = ArrayList()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(itemsFilter)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.ENGLISH).trim { it <= ' ' }
                for (item in itemsFilter) {
                    if (item.title.lowercase(Locale.ENGLISH).contains(filterPattern) or item.title.lowercase(Locale.ENGLISH)
                            .contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            items = (results.values as ArrayList<Highlight>?) as ArrayList<Highlight>
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

interface AutoUpdatableAdapter {
    fun <T> RecyclerView.Adapter<*>.autoNotify(
        oldList: List<T>,
        newList: List<T>,
        compare: (T, T) -> Boolean
    ) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return compare(oldList[oldItemPosition], newList[newItemPosition])
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize() = oldList.size
            override fun getNewListSize() = newList.size
        })
        diff.dispatchUpdatesTo(this)
    }
}
