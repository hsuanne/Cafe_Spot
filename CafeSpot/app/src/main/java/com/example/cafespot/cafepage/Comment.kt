package com.example.cafespot.cafepage

import android.os.Parcel
import android.os.Parcelable

class Comment(var discussTime:String?, var userId:Int, var userName:String?, var discussContent:String?, var userPicture:String?):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(discussTime)
        parcel.writeInt(userId)
        parcel.writeString(userName)
        parcel.writeString(discussContent)
        parcel.writeString(userPicture)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}