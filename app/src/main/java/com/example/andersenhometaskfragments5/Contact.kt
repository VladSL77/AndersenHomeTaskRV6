package com.example.andersenhometaskfragments5

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Contact : Parcelable {
    var id: Int = 0
    var firstName: String? = null
    var lastName: String? = null
    var phoneNumber: String? = null
    var pathImage: String? = null
}