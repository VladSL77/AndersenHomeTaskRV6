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
        saveButtonClickListener = context as SaveButtonClickListener
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
        changeInfo()
        view.findViewById<Button>(R.id.buttonSave).setOnClickListener {
            list[index].firstName = newFirstName
            list[index].lastName = newLastName
            list[index].phoneNumber = newNumber
            saveButtonClickListener.onSaveButtonClicked(list)
        }

    }

    interface SaveButtonClickListener {
        fun onSaveButtonClicked(list: MutableList<Contact>)
    }

    private fun changeInfo() {

        etFirstName.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (etFirstName.text.toString() != "") {
                    newFirstName = etFirstName.text.toString()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_empty_field),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@OnKeyListener true
            }
            false
        })

        etLastName.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (etLastName.text.toString() != "") {
                    newLastName = etLastName.text.toString()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_empty_field),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@OnKeyListener true
            }
            false
        })

        etNumber.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                if (etNumber.text.toString() != "") {
                    newNumber = etNumber.text.toString()
                } else {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_empty_field),
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@OnKeyListener true
            }
            false
        })

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