package com.example.andersenhometaskfragments5

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso

class FragmentDetail : Fragment(R.layout.detail_fragment), BackPressedListener {

    private var index = 0
    private lateinit var list: MutableList<Contact>
    private lateinit var saveButtonClickListener: SaveButtonClickListener

    private lateinit var etFirstName: TextView
    private lateinit var etLastName: TextView
    private lateinit var etNumber: TextView
    private lateinit var ivDetail: ImageView

    private var newFirstName = ""
    private var newLastName = ""
    private var newNumber = ""

    private val picasso = Picasso.get()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SaveButtonClickListener) saveButtonClickListener = context
    }

    private fun init() {
        etFirstName = requireView().findViewById(R.id.etFirstName)
        etLastName = requireView().findViewById(R.id.etLastName)
        etNumber = requireView().findViewById(R.id.etNumber)
        ivDetail = requireView().findViewById(R.id.imageViewDetail)
        newFirstName = list[index].firstName.toString()
        newLastName = list[index].lastName.toString()
        newNumber = list[index].phoneNumber.toString()
        etFirstName.text = newFirstName
        etLastName.text = newLastName
        etNumber.text = newNumber
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        list = requireArguments().getParcelableArrayList(KEY_LIST)!!
        index = requireArguments().getInt(KEY_INDEX)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        picasso.load(list[index].pathImage).into(ivDetail)
        view.findViewById<Button>(R.id.buttonSave).setOnClickListener {
            if (etFirstName.text.toString() != "" && etLastName.text.toString() != "" && etNumber.text.toString() != "") {
                list[index].firstName = etFirstName.text.toString()
                list[index].lastName = etLastName.text.toString()
                list[index].phoneNumber = etNumber.text.toString()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_empty_field),
                    Toast.LENGTH_LONG
                ).show()
            }
            saveButtonClickListener.onSaveButtonClicked(list, index)
        }

    }

    interface SaveButtonClickListener {
        fun onSaveButtonClicked(list: MutableList<Contact>, index: Int)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(KEY_LIST, ArrayList<Parcelable>(list))
        outState.putInt(KEY_INDEX, index)
    }

    override fun onBackPressedClicked(): Boolean = true

    companion object {

        private const val KEY_LIST = "KEY_LIST"
        private const val KEY_INDEX = "KEY_INDEX"
        const val FRAGMENT_DETAIL_TAG = "FRAGMENT_DETAIL_TAG"

        fun newInstance(list: MutableList<Contact>, index: Int)
            = FragmentDetail().also {
                it.arguments = Bundle().apply {
                putParcelableArrayList(KEY_LIST, ArrayList<Parcelable>(list))
                putInt(KEY_INDEX, index)
            }
        }
    }

}