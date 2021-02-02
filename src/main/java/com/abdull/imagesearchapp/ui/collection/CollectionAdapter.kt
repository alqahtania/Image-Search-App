package com.abdull.imagesearchapp.ui.collection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.abdull.imagesearchapp.data.local.Collection
import com.abdull.imagesearchapp.databinding.CollectionItemBinding

/**
 * Created by Abdullah Alqahtani on 10/6/2020.
 */
class CollectionAdapter(private val listener : OnItemClickEvents) : PagingDataAdapter<Collection, CollectionAdapter.CollectionViewHolder>(
    collectionComparator) {


    interface OnItemClickEvents{
        fun itemClicked(collection: Collection)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CollectionViewHolder {
       val binding =
           CollectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CollectionViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CollectionViewHolder, position: Int) {

        val currentItem = getItem(position)
        if(currentItem != null){
            holder.bind(currentItem)
        }
    }

    fun getCollectionAt(position : Int) : Collection?{
        return getItem(position)
    }


    inner class CollectionViewHolder(private val binding : CollectionItemBinding) :
            RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener{
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val collection = getItem(position)
                    if(collection != null){
                        listener.itemClicked(collection)
                    }
                }
            }
        }

        fun bind(collection : Collection){
            binding.apply {
                textViewCollectionName.text = collection.name
                textViewCollectionDescription.text = collection.description
            }
        }
    }

    companion object {
        private val collectionComparator = object : DiffUtil.ItemCallback<Collection>() {
            override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Collection,
                newItem: Collection
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}