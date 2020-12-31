package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserNote(
    val uid: String,
    val username: String,
    var profileImageUrl: String,
    val title:String,
    val notes: String,
    val articalImageUrl: String,
    val key:String,
    val filename:String
): Parcelable {
    constructor():this( "","","","","","","","")

}