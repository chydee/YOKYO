package com.nwanneka.yokyo.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.nwanneka.yokyo.data.Highlight
import com.nwanneka.yokyo.databinding.ItemHighlightBinding

class HighlightAdapter constructor(private val requestManager: RequestManager) : ListAdapter<Highlight, HighlightAdapter.HighlightViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onHighlightClick(highlight: Highlight)
        fun onShareClicked(content: String)
    }

    private lateinit var listener: OnItemClickListener

    inner class HighlightViewHolder(private val binding: ItemHighlightBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(highlight: Highlight) {
            binding.highlightTitle.text = highlight.title
            requestManager.load(highlight.thumbnail)
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
        val color = getItem(position)
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
}
