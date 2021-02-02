package com.abdull.imagesearchapp.ui.saved


import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.abdull.imagesearchapp.R
import com.abdull.imagesearchapp.data.local.Photo
import com.abdull.imagesearchapp.databinding.ItemSavedUnsplashPhotoBinding

/**
 * Created by Abdullah Alqahtani on 10/5/2020.
 */
class SavedPhotoAdapter(private val listeners : ItemClickListener) :
    PagingDataAdapter<Photo, SavedPhotoAdapter.PhotoViewHolder>(photoComparator) {

    interface ItemClickListener{
        fun onItemClicked(photo : Photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemSavedUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if(currentItem != null){
            holder.bind(currentItem)
        }
    }

    fun getPhotoAt(position: Int) : Photo?{
        return getItem(position)
    }


    inner class PhotoViewHolder(private val binding: ItemSavedUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.imageView.setOnClickListener{
                val position = bindingAdapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val photo = getItem(position)
                    if(photo != null){
                        listeners.onItemClicked(photo)
                    }
                }
            }
        }


        fun bind(photo: Photo) {
            binding.apply {

                Glide.with(itemView)
                    .load(photo.photoSmall)
                    .error(R.drawable.ic_error)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progressBar.isVisible = false
                            textViewCreator.isVisible = true
                            textViewDescription.isVisible = !photo.photoDescription.isNullOrEmpty()
                            return false
                        }
                    })
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageView)

                textViewDescription.text = photo.photoDescription

                val uri = Uri.parse(photo.getAttributionUrl())
                val intent = Intent(Intent.ACTION_VIEW, uri)

                textViewCreator.apply {
                    text = "Photo by ${photo.user} on Unspalsh"
                    setOnClickListener {
                        context.startActivity(intent)
                    }
                    paint.isUnderlineText = true
                }
            }
        }
    }

    companion object {
        private val photoComparator = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem.photoId == newItem.photoId
            }

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
                return oldItem == newItem
            }
        }
    }
}