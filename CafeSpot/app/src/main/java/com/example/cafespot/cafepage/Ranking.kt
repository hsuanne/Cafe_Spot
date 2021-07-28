package com.example.cafespot.cafepage

import android.os.Parcel
import android.os.Parcelable

class Ranking(var title:String?, var star:Float) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeFloat(star)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ranking> {
        override fun createFromParcel(parcel: Parcel): Ranking {
            return Ranking(parcel)
        }

        override fun newArray(size: Int): Array<Ranking?> {
            return arrayOfNulls(size)
        }
    }
}