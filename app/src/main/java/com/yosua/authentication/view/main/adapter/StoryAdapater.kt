package com.yosua.authentication.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yosua.authentication.R
import com.yosua.authentication.databinding.ItemStoryBinding
import com.yosua.authentication.model.remote.response.ListStoryItem

class StoryAdapter(private val stories : List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {
    private lateinit var binding : ItemStoryBinding
    override fun onCreateViewHolder(
        parent : ViewGroup,
        viewType : Int,
    ) : StoryViewHolder {
        binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder : StoryViewHolder, position : Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount() : Int = stories.size

    inner class StoryViewHolder(private val binding : ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story : ListStoryItem) {
            binding.apply {
                tvPerson.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.image_24)
                    .into(imageView)
            }
        }
    }
}