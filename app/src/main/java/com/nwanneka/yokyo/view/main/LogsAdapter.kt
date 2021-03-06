package com.nwanneka.yokyo.view.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.databinding.ItemLogBinding

class LogsAdapter : ListAdapter<Logg, LogsAdapter.LogsViewHolder>(DiffCallback) {

    interface OnItemClickListener {
        fun onLogItemClicked(logg: Logg)
    }

    private lateinit var listener: OnItemClickListener

    inner class LogsViewHolder(private val binding: ItemLogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(logg: Logg) {
            binding.logLocation.text = logg.location
            binding.logDateTime.text = "${logg.date} ${logg.time}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogsViewHolder {
        return from(parent)
    }

    private fun from(parent: ViewGroup): LogsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLogBinding.inflate(layoutInflater, parent, false)
        return LogsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogsViewHolder, position: Int) {
        val logItem = getItem(position)
        holder.bind(logItem)
        holder.itemView.setOnClickListener {
            listener.onLogItemClicked(logItem)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Logg>() {
        override fun areContentsTheSame(oldItem: Logg, newItem: Logg): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areItemsTheSame(oldItem: Logg, newItem: Logg): Boolean {
            return oldItem == newItem
        }
    }

    internal fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
