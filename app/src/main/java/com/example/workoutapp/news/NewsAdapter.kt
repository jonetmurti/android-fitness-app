package com.example.workoutapp.news

import android.net.Network
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutapp.databinding.NewsItemBinding
import com.example.workoutapp.network.NetworkNews

class NewsAdapter(private val listener: NewsClickListener): ListAdapter<NetworkNews, NewsAdapter.ViewHolder>(NewsDiffCallback()) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, listener)
    }

    class ViewHolder(private val binding: NewsItemBinding, private val listener: NewsClickListener): RecyclerView.ViewHolder(binding.root){
        fun bind(item: NetworkNews){
            binding.tvAuthor.text = item.author
            binding.tvDate.text = item.publishedAt
            binding.tvDescription.text = item.description
            binding.tvTitle.text = item.title

            binding.root.setOnClickListener {
                listener.onClick(item)
            }
        }

        companion object{
            fun from(parent: ViewGroup, listener: NewsClickListener): ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = NewsItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, listener)
            }
        }
    }

    interface NewsClickListener{
        fun onClick(item: NetworkNews)
    }
}

class NewsDiffCallback: DiffUtil.ItemCallback<NetworkNews>(){
    override fun areContentsTheSame(oldItem: NetworkNews, newItem: NetworkNews): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: NetworkNews, newItem: NetworkNews): Boolean {
        return oldItem.title == newItem.title
    }
}