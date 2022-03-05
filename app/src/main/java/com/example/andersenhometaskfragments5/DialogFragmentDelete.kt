package com.example.andersenhometaskfragments5

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment

class DialogFragmentDelete : DialogFragment() {

    private lateinit var deleteButtonClickedListener: DeleteButtonClickedListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        deleteButtonClickedListener = parentFragment as DeleteButtonClickedListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage("Delete contact: ${requireArguments().getString(FIRST_NAME)} ${requireArguments().getString(LAST_NAME)}?")
            .setPositiveButton(getString(R.string.delete)) { dialog, _ ->
                deleteButtonClickedListener.onDeleteButtonClicked(requireArguments().getInt(INDEX))
                dialog.dismiss()
            }
            .show()
    }

    interface DeleteButtonClickedListener {
        fun onDeleteButtonClicked(index: Int)
    }

    companion object {

        private const val INDEX = "INDEX"
        private const val FIRST_NAME = "FIRST_NAME"
        private const val LAST_NAME = "LAST_NAME"
        const val DIALOG_FRAGMENT_DELETE_TAG = "DIALOG_FRAGMENT_DELETE_TAG"

        fun newInstance(firstName: String, lastName: String, index: Int)
            = DialogFragmentDelete().also {
                it.arguments = Bundle().apply {
                    putInt(INDEX, index)
                    putString(FIRST_NAME, firstName)
                    putString(LAST_NAME, lastName)
                }
            }
    }

}