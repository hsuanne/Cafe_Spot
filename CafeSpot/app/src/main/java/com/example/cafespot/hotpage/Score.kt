package com.example.cafespot.hotpage

import android.os.Parcel
import android.os.Parcelable

class Score(
    var wifiStablilty:Float,
    var plugCountDegree:Float,
    var quietDegree:Float,
    var coffeeDegree:Float,
    var foodDegree:Float,
    var priceDegree:Float,
    var decorateDegree:Float,
    var brightness:Float,
    var musicDegree:Float
    )  : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(wifiStablilty)
        parcel.writeFloat(plugCountDegree)
        parcel.writeFloat(quietDegree)
        parcel.writeFloat(coffeeDegree)
        parcel.writeFloat(foodDegree)
        parcel.writeFloat(priceDegree)
        parcel.writeFloat(decorateDegree)
        parcel.writeFloat(brightness)
        parcel.writeFloat(musicDegree)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Score> {
        override fun createFromParcel(parcel: Parcel): Score {
            return Score(parcel)
        }

        override fun newArray(size: Int): Array<Score?> {
            return arrayOfNulls(size)
        }
    }
}