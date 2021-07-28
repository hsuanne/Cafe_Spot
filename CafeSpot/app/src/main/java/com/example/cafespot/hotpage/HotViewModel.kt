package com.example.cafespot.hotpage

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.cafespot.USER_ID
import com.example.cafespot.cafepage.Comment
import com.example.cafespot.cafepage.Ranking
import com.example.cafespot.cafepage.fragments.RatingPost
import com.example.cafespot.profile.Record
import com.example.cafespot.profile.putServer
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class HotViewModel : ViewModel() {
    var currentHotItem = MutableLiveData<HotItem>()
    var currentLocation = MutableLiveData<Location>()
    var currentShopLocation = MutableLiveData<List<Double>>()
    var rankingData = MutableLiveData<RankingData>()
    var commentData = MutableLiveData<Int>()
    var profileData = MutableLiveData<Profile>()
    var recordLive = MutableLiveData<List<Record>>()

    var HotItemList = MutableLiveData<List<HotItem>>()
    var aroundList = MutableLiveData<List<HotItem>>()
    var cafePicList = MutableLiveData<List<String>>()

    //    var ModeWorkList = MutableLiveData<List<HotItem>>()
    var RankingList = MutableLiveData<List<Ranking>>()
    var CommentList = MutableLiveData<List<Comment>>()
    var postUserState = MutableLiveData<Boolean>()
    private var rankingList: MutableList<Ranking> = mutableListOf(
        Ranking("wifi穩定", 0f),
        Ranking("插座夠多", 0f),
        Ranking("安靜程度", 0f),
        Ranking("咖啡好喝", 0f),
        Ranking("餐點好吃", 0f),
        Ranking("價格便宜", 0f),
        Ranking("裝潢美觀", 0f),
        Ranking("明亮程度", 0f),
        Ranking("音樂好聽", 0f)
    )

    //code
    var postCommentCode = MutableLiveData<Int>()
    var postRatingCode = MutableLiveData<Int>()
    var putStatus = MutableLiveData<Boolean>()

    fun setCurrentHotItem(hotItem: HotItem) {
        currentHotItem.value = hotItem
    }

    fun setCurrentLocation(location: Location) {
        currentLocation.value = location
    }

    fun fetchCafeInfo(cafeId: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                val tmpItem = fetchInfo(cafeId)
                currentHotItem.value = tmpItem!!
                currentShopLocation.value = listOf(tmpItem.latitude, tmpItem.longitude)
            }.join()
        }
    }

    fun fetchCafeScore() {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                val tmpItem = fetchScore(currentHotItem.value?.cafeId!!)
                currentHotItem.value?.cafeScore = tmpItem.cafeScore
                currentHotItem.value?.allScorePeople = tmpItem.allScorePeople
                currentHotItem.value?.averageScore = tmpItem.averageScore

                //rankingData
                rankingData.value = RankingData(tmpItem.averageScore, tmpItem.allScorePeople)

                //把Score轉成ranking
                for (ranking in rankingList) {
                    when (ranking.title) {
                        "wifi穩定" -> ranking.star = currentHotItem.value?.cafeScore?.wifiStablilty!!
                        "插座夠多" -> ranking.star = currentHotItem.value?.cafeScore?.plugCountDegree!!
                        "安靜程度" -> ranking.star = currentHotItem.value?.cafeScore?.quietDegree!!
                        "咖啡好喝" -> ranking.star = currentHotItem.value?.cafeScore?.coffeeDegree!!
                        "餐點好吃" -> ranking.star = currentHotItem.value?.cafeScore?.foodDegree!!
                        "價格便宜" -> ranking.star = currentHotItem.value?.cafeScore?.priceDegree!!
                        "裝潢美觀" -> ranking.star = currentHotItem.value?.cafeScore?.decorateDegree!!
                        "明亮程度" -> ranking.star = currentHotItem.value?.cafeScore?.brightness!!
                        "音樂好聽" -> ranking.star = currentHotItem.value?.cafeScore?.musicDegree!!
                    }
                    println("title:" + ranking.title + " star:" + ranking.star)
                }
                RankingList.value = rankingList
            }.join()
        }
    }

    fun fetchCafeComment() {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                val tmpItem = fetchComment(currentHotItem.value?.cafeId!!)
                currentHotItem.value?.allDiscussPeople = tmpItem.allDiscussPeople
                commentData.value = tmpItem.allDiscussPeople
                CommentList.value = tmpItem.cafeDiscuss
            }.join()
        }
    }

    fun fetchHotItemList(mode: String, pageIndex: Int, lat: Double, long: Double) {
        println("fetchHotItemList")
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                HotItemList.value = fetchHotItem(mode, pageIndex, lat, long)!!

                cafePicList.value = mutableListOf()
                var cafePictureList: MutableList<String> = mutableListOf()
                for (hot in HotItemList.value!!){
                    if (hot.cafePicture.size>0) {
                        cafePictureList.add(hot.cafePicture[0])
                        println("cafePicture.size>0"+hot.cafePicture[0])
                    } else {
                        cafePictureList.add("")
                        println("cafePicture.size<0")
                    }
                }
                cafePicList.value = cafePictureList
            }.join()
        }
    }

    fun fetchAroundList(selected: String, lat: Double, long: Double) {
        println("fetchAroundList")
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                aroundList.value = fetchAround(selected, lat, long)!!
            }.join()
        }
    }

    fun fetchUserProfile() {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                putStatus.value = false
            }

            viewModelScope.launch {
                profileData.value = fetchProfile()!!
            }.join()

            viewModelScope.launch {
                var it = profileData.value!!
                // create cafeId List
                var cafeIdList: MutableList<Int> = mutableListOf()
                if (it.discussCafe.size > 0) {
                    for (discuss in it.discussCafe) {
                        cafeIdList.add(discuss.cafeId)
                    }
                }
                if (it.scoreCafe.size > 0) {
                    for (score in it.scoreCafe) {
                        cafeIdList.add(score.cafeId)
                    }
                }
                cafeIdList = cafeIdList.distinct().toMutableList()

                println("cafeIdList.size:${cafeIdList.size}")

                //recordList
                var recordList: MutableList<Record> = mutableListOf()
                for (cafeId in cafeIdList) {
                    var record = Record(cafeId, "", "", "", 0f, "")
                    recordList.add(record)
                }

                //put discuss in recordList
                for (discuss in it.discussCafe) {
                    for (record in recordList) {
                        if (discuss.cafeId == record.cafeId) {
                            record.date = discuss.discussTime
                            if(discuss.cafePicture.size>0) {
                                record.cafeImg = discuss.cafePicture[0]
                            } else {
                                record.cafeImg = ""
                            }
                            record.cafeName = discuss.cafeName
                            record.myComment = discuss.discussContent
                        }
                    }
                }

                // put rating in recordList

                for (score in it.scoreCafe) {
                    for (record in recordList) {
                        if (score.cafeId == record.cafeId) {
                            record.date = score.scoreTime
                            if(score.cafePicture.size>0) {
                                record.cafeImg = score.cafePicture[0]
                            } else {
                                record.cafeImg = ""
                            }
                            record.cafeName = score.cafeName
                            record.myRating = score.averageScore
                        }
                    }
                }
                recordList = recordList.sortedWith(compareByDescending { it.date }).toMutableList()
                recordLive.value = recordList
            }.join()
        }
    }

    suspend fun fetchInfo(cafeId: Int): HotItem {
        println("Attempting to Fetch Info")

        val url =
            "http://35.221.213.180/api/CafeInfo/CafeInfos/$cafeId"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val call = client.newCall(request)
        //requestCall: 非同步
        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response?.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<HotItem>(body, object : TypeToken<HotItem>() {}.type)
                        it.resumeWith(Result.success(result))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }

    suspend fun fetchScore(cafeId: Int): HotItem {
        println("Attempting to Fetch Score")

        println("fetchScore:$cafeId")
        val url =
            "http://35.221.213.180/api/CafeInfo/CafeScore/$cafeId"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val call = client.newCall(request)
        //requestCall: 非同步
        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response?.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<HotItem>(body, object : TypeToken<HotItem>() {}.type)
                        it.resumeWith(Result.success(result))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }

    suspend fun fetchComment(cafeId: Int): HotItem {
        println("Attempting to Fetch Comment")

        val url =
            "http://35.221.213.180/api/CafeInfo/CafeDiscuss/$cafeId"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val call = client.newCall(request)
        //requestCall: 非同步
        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response?.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<HotItem>(body, object : TypeToken<HotItem>() {}.type)
                        it.resumeWith(Result.success(result))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }

    suspend fun fetchProfile(): Profile {
        println("Attempting to Fetch Profile")

        val url =
            "http://35.221.213.180/api/User/UserInfo/$USER_ID"
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val call = client.newCall(request)
        //requestCall: 非同步
        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response?.body?.string()
                        println(body)

                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<Profile>(body, object : TypeToken<Profile>() {}.type)
                        it.resumeWith(Result.success(result))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }

    suspend fun fetchHotItem(
        mode: String,
        pageIndex: Int,
        lat: Double,
        long: Double
    ): List<HotItem> {
        println("Attempting to Fetch JSON")

        var url = ""
        when (mode) {
            "hot" -> url =
                "http://35.221.213.180/api/CafeInfo/FamousCafebyPage/$pageIndex/$lat/$long"
            "study" -> url = "http://35.221.213.180/api/Story/Study/$pageIndex/$lat/$long"
            "work" -> url = "http://35.221.213.180/api/Story/Work/$pageIndex/$lat/$long"
            "meeting" -> url = "http://35.221.213.180/api/Story/Meeting/$pageIndex/$lat/$long"
            "around" -> url = "http://35.221.213.180/api/CafeInfo/AroundCafe/$lat/$long"
            "petFriendly" -> url = "http://35.221.213.180/api/Story/PetFriendly/$pageIndex/$lat/$long"
            "midnightCafe" -> url = "http://35.221.213.180/api/Story/MidnightCafe/$pageIndex/$lat/$long"
            "relax" -> url = "http://35.221.213.180/api/Story/Relax/$pageIndex/$lat/$long"
        }
        println(url)
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        val call = client.newCall(request)

        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response?.body?.string()
                        println(body)
//                val arr = JSONArray(body) //將資料轉成JSONArray
//                println(arr.length())
                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<List<HotItem>>(
                                body,
                                object : TypeToken<List<HotItem>>() {}.type
                            )
                        it.resumeWith(Result.success(result))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }

    suspend fun fetchAround(
        selected: String,
        lat: Double,
        long: Double
    ): List<HotItem> {
        println("Attempting to Fetch Around")

        var url = "http://35.221.213.180/api/CafeInfo/FamousCafebyTag/$selected/$lat/$long"
        println("url:$url")
        val request = Request.Builder().url(url).build()

//                val logging = HttpLoggingInterceptor()
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//        val client = OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .build()

//        val client = OkHttpClient()
        val call = client.newCall(request)

        return suspendCancellableCoroutine {
            call.enqueue(object : Callback { //callback:做完之後再回傳結果
                override fun onFailure(call: Call, e: IOException) {
                    println("Failed to execute Request")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        val body = response?.body?.string()
                        println(body)
//                val arr = JSONArray(body) //將資料轉成JSONArray
//                println(arr.length())
                        val gson = GsonBuilder().create()
                        val result =
                            gson.fromJson<List<HotItem>>(
                                body,
                                object : TypeToken<List<HotItem>>() {}.type
                            )
                        it.resumeWith(Result.success(result))
                    } else {
                        println("server problem")
                    }
                }
            })
            it.invokeOnCancellation { //連不到的話就取消
                call.cancel()
            }
        }
    }

    fun postComment(body: String) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                postCommentCode.value = postCommentToServer(body)!!
            }.join()
        }
    }

    fun CommentToJson(commentPost: CommentPost): String {
        val gson = Gson()
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

        val jsonComment: String = gson.toJson(commentPost)
        println(jsonComment)

        val jsonTutPretty: String = gsonPretty.toJson(commentPost)
        println(jsonTutPretty)

        return jsonComment
    }

    fun postUser(body: String) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                postUserState.value = postUserRequest(body)!!
            }.join()
        }
    }

    fun userToJson(userPost: UserPost): String {
        val gson = Gson()
        val gsonPretty = GsonBuilder().setPrettyPrinting().create()

        val jsonComment: String = gson.toJson(userPost)
        Log.d("print user Json", jsonComment)

        return jsonComment
    }

    fun postRating(body: String) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                postRatingCode.value = postRatingToServer(body)!!
            }.join()
        }
    }

    fun RatingToJson(ratingPost: RatingPost): String {
        val gson = Gson()
        val jsonRating: String = gson.toJson(ratingPost)
        println(jsonRating)
        return jsonRating
    }

    fun put(userId: Int, bitmap: Bitmap?, userName: String) {
        CoroutineScope(Dispatchers.Default).launch {
            viewModelScope.launch {
                putStatus.value = putServer(userId, bitmap, userName)!!
            }
        }
    }
}

fun userRequest(body: String): Request {
    val url = "http://35.221.213.180/api/User/AddUser"
    val requestBody = body.toRequestBody("application/json: charset=utf-8".toMediaTypeOrNull())
    println("request_body: $requestBody")
    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .post(requestBody)
        .build()
    return request
}

suspend fun postUserRequest(body: String): Boolean {
    println("Attempting to Post User")

    val request = userRequest(body)

    val client = OkHttpClient()
    val call = client.newCall(request)
    //requestCall: 非同步
    return suspendCancellableCoroutine {
        call.enqueue(object : Callback { //callback:做完之後再回傳結果
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute Request")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response?.body?.string()
                    println(body)

                    val gson = GsonBuilder().create()
                    val result =
                        gson.fromJson<Boolean>(body, object : TypeToken<Boolean>() {}.type)
                    it.resumeWith(Result.success(true))
                } else {
                    println("server problem")
                    it.resume(false)
                }
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            call.cancel()
        }
    }
}

fun postCommentRequest(body: String): Request {
    val url = "http://35.221.213.180/api/CafeInfo/AddDiscuss"
    val requestBody = body.toRequestBody("application/json: charset=utf-8".toMediaTypeOrNull())
    println("request_body: " + requestBody)
    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .post(requestBody)
        .build()
    return request
}

suspend fun postCommentToServer(body: String): Int {
    println("Attempting to Post Comment")

    val request = postCommentRequest(body)

    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .build()

//    val client = OkHttpClient()
    val call = client.newCall(request)

    //requestCall: 非同步
    return suspendCancellableCoroutine {
        call.enqueue(object : Callback { //callback:做完之後再回傳結果
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute Request")
            }

            override fun onResponse(call: Call, response: Response) {
                val httpCode = response.code
                val body = response?.body?.string()
                println(body)
                val gson = GsonBuilder().create()
                val result =
                    gson.fromJson<Int>(
                        body,
                        object : TypeToken<Int>() {}.type
                    )
                Log.i("postComment", "$httpCode", null)
                it.resumeWith(Result.success(result))
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            call.cancel()
        }
    }
}

fun postRatingRequest(body: String): Request {
    val url = "http://35.221.213.180/api/CafeInfo/AddScore"
    val requestBody = body.toRequestBody("application/json: charset=utf-8".toMediaTypeOrNull())
    println("request_body: $requestBody")
    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .post(requestBody)
        .build()
    return request
}

suspend fun postRatingToServer(body: String): Int {
    println("Attempting to Post Rating")

    val request = postRatingRequest(body)

    val client = OkHttpClient()
    val call = client.newCall(request)

    //requestCall: 非同步
    return suspendCancellableCoroutine {
        call.enqueue(object : Callback { //callback:做完之後再回傳結果
            override fun onFailure(call: Call, e: IOException) {
                println("Failed to execute Request")
            }

            override fun onResponse(call: Call, response: Response) {
                val httpCode = response.code
                    val body = response?.body?.string()
                    println(body)
                    Log.i("postRating", "$httpCode", null)
                val gson = GsonBuilder().create()
                val result =
                    gson.fromJson<Int>(
                        body,
                        object : TypeToken<Int>() {}.type
                    )
                it.resumeWith(Result.success(result))
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            call.cancel()
        }
    }
}

class CommentPost(var cafeId: Int, var userId: Int, var discussContent: String)

class UserPost(var userName: String, var passWord: String, var userPicture: File, var mail: String)

class RankingData(var averageScore: Float, var allScorePeople: Int)

class Profile(
    var userName: String,
    var userEmail: String,
    var userPicture: String,
    var discussCount: Int,
    var scoreCount: Int,
    var discussCafe: List<Discuss>,
    var scoreCafe: List<ProfileScore>
)

class ProfileScore(
    var cafeId: Int,
    var cafeName: String,
    var cafePicture: List<String>,
    var scoreTime: String,
    var averageScore: Float
)

class Discuss(
    var cafeId: Int,
    var cafeName: String,
    var cafePicture: List<String>,
    var discussTime: String,
    var discussContent: String
)