package com.abdull.imagesearchapp.ui.collection

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.abdull.imagesearchapp.R
import com.abdull.imagesearchapp.data.local.Collection
import com.abdull.imagesearchapp.databinding.FragmentCollectionBinding
import com.abdull.imagesearchapp.extension.addAnewCollectionDialog
import com.abdull.imagesearchapp.extension.toastOnMainThread
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CollectionFragment : Fragment(), CollectionAdapter.OnItemClickEvents {

    companion object{
        private const val TAG = "CollectionFragment"
    }

    var _binding : FragmentCollectionBinding? = null
    val collectionViewModel by viewModels<CollectionViewModel>()
    lateinit var navController : NavController
    private lateinit var availableCollections: List<Collection>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        _binding = FragmentCollectionBinding.bind(view)

        val adapter = CollectionAdapter(this)

        _binding?.apply {

            recyclerViewCollection.setHasFixedSize(true)
            recyclerViewCollection.adapter = adapter

            collectionViewModel.allLiveDataCollections.observe(viewLifecycleOwner){
                availableCollections = it
            }
            viewLifecycleOwner.lifecycleScope.launch{
                collectionViewModel.collections.collectLatest {
                    adapter.submitData(it)
                }
            }

            recyclerViewCollection.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

            adapter.addLoadStateListener { loadState ->

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerViewCollection.isVisible = false
                    tvEmptyCollection.isVisible = true
                } else {
                    tvEmptyCollection.isVisible = false
                    recyclerViewCollection.isVisible = true
                }
            }
            fabCollection.setOnClickListener{
                insertCollection()
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
                    val collection = adapter.getCollectionAt(position)

                    val dialog = AlertDialog.Builder(requireContext())
                    dialog.setTitle("Delete Collection")
                        .setMessage("Are you sure you want to delete this collection? This will also delete all photos in this collection")
                        .setPositiveButton(
                            android.R.string.yes,
                            object : DialogInterface.OnClickListener {
                                override fun onClick(p0: DialogInterface?, p1: Int) {
                                    if(collection != null){
                                        collectionViewModel.delete(collection)

                                        Toast.makeText(
                                            requireActivity(),
                                            "Deleted id ${collection.name}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            })
                        .setNegativeButton(android.R.string.no, null)
                        .setOnDismissListener {
                            adapter.notifyItemChanged(position)

                        }
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .show()

                }
            }).attachToRecyclerView(recyclerViewCollection)
        }

    }



    fun insertCollection() {

        requireActivity().addAnewCollectionDialog(availableCollections) { cName, cDescription ->
            val newCollection = Collection(cName, cDescription)

            val handler = CoroutineExceptionHandler { _, exception ->
                Log.d(TAG, "Exception thrown in one of the children: $exception")
            }
            val parent = viewLifecycleOwner.lifecycleScope.launch(handler) {
                val insertionResult = async {
                    collectionViewModel.insert(newCollection)
                }
            }
            parent.invokeOnCompletion {
                if (it != null) {
                    requireContext().toastOnMainThread("Something went wrong")
                } else {
                    requireContext().toastOnMainThread("$$cName saved")
                }
            }
        }

    }

    override fun itemClicked(collection: Collection) {
        val action = CollectionFragmentDirections.actionCollectionFragmentToSavedGalleryFragment(collection)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}