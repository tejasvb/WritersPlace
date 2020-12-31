package com.example.myapplication.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class follow(val uid: String):
    Parcelable {
    constructor() : this("")
}