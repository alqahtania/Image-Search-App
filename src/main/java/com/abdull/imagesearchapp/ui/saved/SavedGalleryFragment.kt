package com.abdull.imagesearchapp.ui.saved

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.abdull.imagesearchapp.MainActivity
import com.abdull.imagesearchapp.R
import com.abdull.imagesearchapp.data.local.Photo
import com.abdull.imagesearchapp.data.remote.UnsplashPhoto
import com.abdull.imagesearchapp.databinding.FragmentSavedGalleryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collectLatest

import kotlinx.coroutines.launch


@AndroidEntryPoint
class SavedGalleryFragment : Fragment(R.layout.fragment_saved_gallery),
    SavedPhotoAdapter.ItemClickListener {

    companion object {
        private const val TAG = "SavedGalleryFragment"
    }

    private val args by navArgs<SavedGalleryFragmentArgs>()
    private val viewModel by viewModels<SavedGalleryViewModel>()
    private var _binding: FragmentSavedGalleryBinding? = null
    lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val collection = args.collection

        (requireActivity() as MainActivity).supportActionBar?.title = collection.name


        navController = Navigation.findNavController(view)

        _binding = FragmentSavedGalleryBinding.bind(view)

        val adapter = SavedPhotoAdapter(this)
        _binding?.apply {
            recyclerViewSavedPhotos.setHasFixedSize(true)

            recyclerViewSavedPhotos.adapter = adapter


            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getAllPagedPhotosByCollection(collection.id).collectLatest {
                    adapter.submitData(it)
                }
            }

            adapter.addLoadStateListener { loadState ->

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerViewSavedPhotos.isVisible = false

                    textViewEmptyCollection.isVisible = true
                    buttonGoToGallery.isVisible = true
                } else {
                    textViewEmptyCollection.isVisible = false
                    buttonGoToGallery.isVisible = false
                }
            }
            buttonGoToGallery.setOnClickListener {

                navController.popBackStack(R.id.galleryFragment, false)
            }


            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    val photo = adapter.getPhotoAt(position)

                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setTitle("Delete Photo")
                        .setMessage("Are you sure you want to delete this photo from your collection?")
                        .setPositiveButton(
                            android.R.string.yes,
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {

                                    viewModel.deleteByPhotoId(photo!!.photoId)
                                    Toast.makeText(
                                        requireActivity(),
                                        "Deleted id ${photo!!.photoId}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        .setNegativeButton(android.R.string.no, null)
                        .setOnDismissListener {
                            adapter.notifyItemChanged(position)

                        }
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .show()

                }
            }).attachToRecyclerView(recyclerViewSavedPhotos)

        }

        //we changed it from LiveData to  Flow
//        viewModel.photos.observe(viewLifecycleOwner){
//            adapter.submitData(viewLifecycleOwner.lifecycle, it)
//            Log.d(TAG, "onViewCreated: called photo : ${it.toString()} ")
//        }


    }

    override fun onItemClicked(photo: Photo) {
        val urls = UnsplashPhoto.UnsplashPhotoUrls(
            photo.photoRaw ?: "",
            photo.photoFull ?: "",
            photo.photoRegular ?: "",
            photo.photoSmall ?: "",
            photo.photoThumb ?: ""
        )
        val user = UnsplashPhoto.UnsplashUser(photo.user, photo.userName)
        val unsplashPhoto =
            UnsplashPhoto(photo.photoId, photo.photoDescription, urls = urls, user = user)
        val bundle = bundleOf("photo" to unsplashPhoto)
        navController.navigate(R.id.action_savedGalleryFragment_to_detailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}