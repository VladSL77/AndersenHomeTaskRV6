package com.example.andersenhometaskfragments5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.andersenhometaskfragments5.FragmentDetail.Companion.FRAGMENT_DETAIL_TAG
import com.example.andersenhometaskfragments5.FragmentList.Companion.FRAGMENT_LIST_TAG
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(), FragmentList.InfoClickListener,
    FragmentDetail.SaveButtonClickListener {

    private lateinit var list: MutableList<Contact>
    private var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list = generateContacts()

        if (supportFragmentManager.findFragmentByTag(FRAGMENT_LIST_TAG) == null) {
            supportFragmentManager.beginTransaction().run {
                replace(R.id.fragmentContainer1, FragmentList.newInstance(list, index), FRAGMENT_LIST_TAG)
                commit()
            }
        }
    }

    private fun generateContacts(): MutableList<Contact> {
        val values = mutableListOf<Contact>()
        var contact: Contact
        for (position in 0..110) {
            contact = Contact()
            contact.id = position
            contact.firstName = "FirstName Contact$position"
            contact.lastName = "LastName Contact$position"
            contact.phoneNumber = "PhoneNumber Contact$position"
            contact.pathImage = "https://picsum.photos/id/1$position/200/200"
            values.add(contact)
        }
        return values
    }

    override fun onInfoClicked(list: MutableList<Contact>, index: Int) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragmentContainer1, FragmentDetail.newInstance(list, index), FRAGMENT_DETAIL_TAG)
            addToBackStack(FRAGMENT_DETAIL_TAG)
            commit()
        }
    }

    override fun onSaveButtonClicked(list: MutableList<Contact>) {
        supportFragmentManager.beginTransaction().run {
            replace(R.id.fragmentContainer1, supportFragmentManager.findFragmentByTag(
                FRAGMENT_LIST_TAG)!!, FRAGMENT_LIST_TAG)
            commit()
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer1) as? BackPressedListener
        if (currentFragment?.onBackPressedClicked() != false) {
            super.onBackPressed()
        } else {
            moveTaskToBack(true)
            exitProcess(-1)
        }
    }

}