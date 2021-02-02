package com.abdull.imagesearchapp.ui.details

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.map
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.abdull.imagesearchapp.R
import com.abdull.imagesearchapp.data.local.Collection
import com.abdull.imagesearchapp.data.local.Photo
import com.abdull.imagesearchapp.data.remote.UnsplashPhoto
import com.abdull.imagesearchapp.databinding.FragmentDetailsBinding
import com.abdull.imagesearchapp.extension.addAnewCollectionDialog
import com.abdull.imagesearchapp.extension.showAvailableCollectionsDialog
import com.abdull.imagesearchapp.extension.toastOnMainThread
import com.abdull.imagesearchapp.ui.collection.CollectionViewModel
import com.abdull.imagesearchapp.ui.saved.SavedGalleryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.collection_dialog_item.view.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class DetailsFragment : Fragment() {

    var photo: UnsplashPhoto? = null

    var savedPhotoId: String? = null

    private val savedGalleryViewModel by viewModels<SavedGalleryViewModel>()
    private val collectionViewModel by viewModels<CollectionViewModel>()
    private lateinit var availableCollections: List<Collection>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photo = requireArguments().getParcelable("photo")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)


        collectionViewModel.allLiveDataCollections.observe(viewLifecycleOwner) {
            availableCollections = it
        }

        binding.apply {

            Glide.with(this@DetailsFragment)
                .load(photo?.urls?.full)
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
                        textViewDescription.isVisible = !photo?.description.isNullOrBlank()
                        return false
                    }
                })
                .into(imageView)

            textViewDescription.text = photo?.description

            val uri = Uri.parse(photo?.user?.attributionUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            textViewCreator.apply {
                text = "Photo by ${photo?.user?.name} on Unsplash"
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true
            }


        }




        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_details_save, menu)

        savedGalleryViewModel.findPhotoById(photo!!.id)

        savedGalleryViewModel.findPhotoById.observe(viewLifecycleOwner) {
            savedPhotoId = it
            menu.findItem(R.id.action_save).isVisible = it.isNullOrBlank()
            menu.findItem(R.id.action_delete).isVisible = !it.isNullOrBlank()

            Log.d("TAG", "onViewCreated: it is $it")
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                if (!availableCollections.isNullOrEmpty()) {
                    chooseCollectionDialog()
                } else {
                    insertCollectionAndPhoto()
                }
            }
            R.id.action_delete -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Photo")
                    .setMessage("Are you sure you want to delete this photo from your collection?")
                    .setPositiveButton(
                        android.R.string.yes,
                        object : DialogInterface.OnClickListener {

                            override fun onClick(p0: DialogInterface?, p1: Int) {

                                savedGalleryViewModel.deleteByPhotoId(photo!!.id)
                                Toast.makeText(
                                    requireActivity(),
                                    "Deleted id ${photo!!.id}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                requireActivity().onBackPressed()
                            }
                        })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_baseline_warning_24)
                    .show()

            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseCollectionDialog() {

        /*var arrayList = ArrayList<String>()
        val listView = ListView(requireContext())
        val arrayAdapter = ArrayAdapter(requireContext(),
            R.layout.collection_dialog_item,
            R.id.collection_item_text_view,
            arrayList
            )
        listView.adapter = arrayAdapter*/

        var listOfCollection = ArrayList<String>(availableCollections.map { it.name })

        listOfCollection.add("+ Save to new collection")

        requireActivity().showAvailableCollectionsDialog(listOfCollection.toTypedArray()) { item ->
            val chosenCollection = availableCollections.find { it.name == item }
            if (chosenCollection == null) {
                insertCollectionAndPhoto()
            } else {
                val insetPhoto = Photo(
                    collectionId = chosenCollection.id,
                    photoId = photo!!.id,
                    photoDescription = photo!!.description,
                    photoRaw = photo!!.urls.raw,
                    photoFull = photo!!.urls.full,
                    photoRegular = photo!!.urls.regular,
                    photoSmall = photo!!.urls.small,
                    photoThumb = photo!!.urls.thumb,
                    user = photo!!.user.name,
                    userName = photo!!.user.username
                )
                savedGalleryViewModel.insert(insetPhoto)
                Toast.makeText(
                    requireActivity(),
                    "Saved ${photo!!.id} to ${chosenCollection.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    fun insertCollectionAndPhoto() {

        requireActivity().addAnewCollectionDialog(availableCollections = availableCollections) { cName, cDescription ->
            val newCollection = Collection(cName, cDescription)

            val handler = CoroutineExceptionHandler { _, exception ->
                Log.d(TAG, "Exception thrown in one of the children: $exception")
            }
            val parent = viewLifecycleOwner.lifecycleScope.launch(handler) {
                val insertionResult = async {
                    collectionViewModel.insert(newCollection)
                }.await()


                val insertPhotoInCollection = async {
                    val newPhoto = Photo(
                        collectionId = insertionResult,
                        photoId = photo!!.id,
                        photoDescription = photo!!.description,
                        photoRaw = photo!!.urls!!.raw,
                        photoFull = photo!!.urls!!.full,
                        photoRegular = photo!!.urls.regular,
                        photoSmall = photo!!.urls.small,
                        photoThumb = photo!!.urls.thumb,
                        user = photo!!.user!!.name,
                        userName = photo!!.user.username
                    )
                    savedGalleryViewModel.insert(newPhoto)
                }

            }
            parent.invokeOnCompletion {
                if (it != null) {
                    requireContext().toastOnMainThread("Something went wrong")
                } else {
                    requireContext().toastOnMainThread("${photo?.id} saved in $cName")
                }
            }
        }
    }

    companion object {
        private const val TAG = "DetailsFragment"
    }
}