package com.example.cafespot.hotpage

import android.os.Parcel
import android.os.Parcelable
import com.example.cafespot.cafepage.Comment
import com.example.cafespot.cafepage.Ranking

class HotItem(
    var cafeId:Int,
    var cafePicture:MutableList<String>, // 串API後可能要改成String url?
    var cafeName:String?, //店家名稱
    var address:String?, //店家地址
    var cafeDiscuss: MutableList<Comment>, //留言列表
    var cafeScore: Score, //評分列表
    var hasSingleOriginCoffee:String?,
    var hasDessert:String?,
    var hasFood:String?,
    var hasLimitedTime:String?,
    var hasStandingWork:String?,
    var hasMuchPlug:String?,
    var cityName:String?,
    var regionName:String?,
    var allDiscussPeople:Int,
    var allScorePeople:Int,
    var averageScore:Float,
    var mrtStationName:String?,
    var busStationName:String?,
    var officeWebsite:String?,
    var latitude:Double,
    var longitude:Double
    )

//    :Parcelable {
//    constructor(parcel: Parcel) : this(
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readString(),
//        parcel.readString(),
//        mutableListOf<Comment>().apply { parcel.readTypedList(this, Comment.CREATOR)},
//        cafeScore = parcel.readParcelable(Score::class.java.classLoader),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readInt(),
//        parcel.readInt(),
//        parcel.readFloat(),
//        parcel.readString(),
//        parcel.readString(),
//        parcel.readString()
//        ) {
//    }
//
//    constructor(
//        cafeId: Int,
//        cafePicture: Int,
//        cafeName: String?,
//        address: String?,
//        commentList: MutableList<Comment>,
//        cafeScore: Score?,
//        hasSingleOriginCoffee: String?,
//        hasDessert: String?,
//        hasFood: String?,
//        hasLimitedTime: String?,
//        hasStandingWork: String?,
//        hasMuchPlug: String?,
//        cityName: String?,
//        regionName: String?,
//        allDiscussPeople: Int,
//        allScorePeople: Int,
//        averageScore: Float,
//        mrtStationName: String?,
//        busStationName: String?,
//        officeWebsite: String?
//    ) : this()
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(cafeId)
//        parcel.writeInt(cafePicture)
//        parcel.writeString(cafeName)
//        parcel.writeString(address)
//        parcel.writeTypedList(commentList)
//        parcel.writeParcelable(cafeScore, flags)
//        parcel.writeString(hasSingleOriginCoffee)
//        parcel.writeString(hasDessert)
//        parcel.writeString(hasFood)
//        parcel.writeString(hasLimitedTime)
//        parcel.writeString(hasStandingWork)
//        parcel.writeString(hasMuchPlug)
//        parcel.writeString(cityName)
//        parcel.writeString(regionName)
//        parcel.writeInt(allDiscussPeople)
//        parcel.writeInt(allScorePeople)
//        parcel.writeFloat(averageScore)
//        parcel.writeString(mrtStationName)
//        parcel.writeString(busStationName)
//        parcel.writeString(officeWebsite)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<HotItem> {
//        override fun createFromParcel(parcel: Parcel): HotItem {
//            return HotItem(parcel)
//        }
//
//        override fun newArray(size: Int): Array<HotItem?> {
//            return arrayOfNulls(size)
//        }
//    }
//}