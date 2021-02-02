package com.abdull.imagesearchapp.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.abdull.imagesearchapp.databinding.UnsplashPhotoLoadStateFooterBinding

/**
 * Created by Abdullah Alqahtani on 10/3/2020.
 */
class UnsplashPhotoLoadStateAdapter(private val retry : () -> Unit) :
    LoadStateAdapter<UnsplashPhotoLoadStateAdapter.LoadStateViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding =
            UnsplashPhotoLoadStateFooterBinding
                .inflate(LayoutInflater.from(parent.context),
                    parent, false)

        return LoadStateViewHolder(binding)

    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {

        holder.bind(loadState)
    }



    inner class LoadStateViewHolder(private val binding : UnsplashPhotoLoadStateFooterBinding) :
            RecyclerView.ViewHolder(binding.root){

        init {
            binding.buttonRetry.setOnClickListener{
                retry.invoke()
            }
        }
        fun bind(loadState: LoadState){

            binding.apply {
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading
                textViewError.isVisible = loadState !is LoadState.Loading
            }
        }
    }
}