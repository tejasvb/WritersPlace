package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String,val background: String,val description:String): Parcelable {
    constructor() : this("", "", "","","")
}