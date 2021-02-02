package com.abdull.imagesearchapp.extension

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.*
import android.view.ViewGroup
import android.widget.*
import com.abdull.imagesearchapp.R
import com.abdull.imagesearchapp.data.local.Collection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Abdullah Alqahtani on 10/6/2020.
 */


fun Context.toastOnMainThread(message: String) {
    val context = this
    CoroutineScope(Main).launch {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }
}

fun Activity.addAnewCollectionDialog(
    availableCollections: List<Collection>, name: String? = "",
    description: String? = "",
    lmbd: (String, String) -> Unit
) {
    val tSet = TreeSet<String>(String.CASE_INSENSITIVE_ORDER)
    if (!availableCollections.isNullOrEmpty()) {
        val cSet = availableCollections.map { it.name }.toHashSet()
        cSet.forEach { tSet.add(it) }
    }
    val context = this
    val linearLayout = LinearLayout(this)
    linearLayout.orientation = LinearLayout.VERTICAL
    val editTextName = EditText(this)
    val editTextDescription = EditText(this)
    editTextName.filters = arrayOf(InputFilter.LengthFilter(20))
    editTextName.maxLines = 1
    editTextName.inputType = InputType.TYPE_CLASS_TEXT
    editTextDescription.filters = arrayOf(InputFilter.LengthFilter(100))
    editTextDescription.maxLines = 3
    editTextDescription.inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE
    editTextDescription.isSingleLine = false
    if (!description.isNullOrBlank()) {
        editTextDescription.text = SpannableStringBuilder(description)
    }
    if (!name.isNullOrBlank()) {
        editTextName.text = SpannableStringBuilder(name)
        if (tSet.contains(name.trim())) {
            editTextName.error = "${name.trim()} already exists"
        }
    }
    editTextName.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (!tSet.isNullOrEmpty()) {
                if (tSet.contains(p0.toString().trim())) {
                    editTextName.error = "${p0?.trim()} already exists"
                }
            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
    editTextDescription.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            val lineCount = editTextDescription.lineCount
            if (lineCount > 3) {
                editTextDescription.text.delete(
                    editTextDescription.selectionEnd - 1,
                    editTextDescription.selectionStart
                )

            }
        }

        override fun afterTextChanged(p0: Editable?) {

        }
    })
    editTextName.hint = "Collection Name"
    editTextDescription.hint = "Collection Description"
    editTextName.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    editTextDescription.layoutParams = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )

    linearLayout.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    linearLayout.addView(editTextName)
    linearLayout.addView(editTextDescription)


    val dialog = AlertDialog.Builder(this)

    dialog.setTitle("Add a new collection")
        .setView(linearLayout)
        .setPositiveButton("Add", object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                if (!editTextName.text.isNullOrBlank()) {
                    if (!tSet.contains(editTextName.text.toString().trim())) {
                        val cName = editTextName.text.toString()
                        val cDescription = editTextDescription.text.toString()
                        lmbd(cName, cDescription)
                    } else {

                        addAnewCollectionDialog(
                            availableCollections,
                            name = editTextName.text.toString(),
                            description = editTextDescription.text.toString(),
                            lmbd
                        )
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Collection name cannot be empty",
                        Toast.LENGTH_LONG
                    ).show()

                    addAnewCollectionDialog(
                        availableCollections,
                        name = editTextName.text.toString(),
                        description = editTextDescription.text.toString(),
                        lmbd
                    )
                }
            }
        })
        .setNegativeButton("Cancel", null)
        .setIcon(R.drawable.ic_baseline_collections_black)
        .show()
}

fun Activity.showAvailableCollectionsDialog(list: Array<String>, lmbd: (String) -> Unit) {

    val alertDialog = AlertDialog.Builder(this)
        .setCancelable(true)
        .setTitle("Save to collection")
        .setView(ScrollView(this))
        .setIcon(R.drawable.ic_baseline_collections_black)
        .setItems(list, object : DialogInterface.OnClickListener {
            override fun onClick(p0: DialogInterface?, p1: Int) {
                lmbd(list[p1])
            }
        })
        .show()

    /*val lp = WindowManager.LayoutParams()

    lp.copyFrom(alertDialog.getWindow()?.attributes)

    lp.height = 1000

    alertDialog.window?.setAttributes(lp)*/


}



