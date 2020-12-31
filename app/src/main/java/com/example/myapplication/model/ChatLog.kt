package com.example.myapplication.model

import android.os.Message
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class ChatLog(val uid: String, val sender: String, val senderProflie: String, val message: String): Parcelable {
    constructor() : this("", "", "","")
}