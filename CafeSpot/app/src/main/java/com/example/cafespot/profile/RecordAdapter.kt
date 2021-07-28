package com.example.cafespot.profile

import android.content.Intent
import android.media.Image
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cafespot.R
import com.example.cafespot.USER_ID
import com.example.cafespot.hotpage.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.w3c.dom.Text
import java.io.IOException
import java.lang.Exception

class RecordAdapter(val activity: ProfileActivity, val hotViewModel: HotViewModel) :
    ListAdapter<Record, RecyclerView.ViewHolder>(RecordDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.profile_layout, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RecordViewHolder -> {
                val record = getItem(position) as Record
                holder.apply {
                    if (record.cafeImg!="") {
                        Glide.with(itemView.context).load(record.cafeImg)
                            .into(cafeImg)
                    }
                    cafeName.text = record.cafeName
                    rating.text = record.myRating.toString()
                    ratingBar.rating = record.myRating
                    comment.text = record.myComment
                    date.text = record.date
                }

                //popup menu
                val popupMenu = PopupMenu(holder.itemView.context, holder.iv_image)
                popupMenu.inflate(R.menu.popup_menu)
                popupMenu.setOnMenuItemClickListener {
                    val infoBasic = InfoBasic(record.cafeId, USER_ID)
                    when (it.itemId) {
                        R.id.pop_rating -> {
//                            Toast.makeText(holder.itemView.context, "rating", Toast.LENGTH_SHORT)
//                                .show()

                            CoroutineScope(Dispatchers.Default).launch {
                                launch { deleteToServer("rating",infoBasic) }.join()
                                launch { hotViewModel.fetchUserProfile() }
                            }
                            true
                        }
                        R.id.pop_comment -> {
//                            Toast.makeText(holder.itemView.context, "comment", Toast.LENGTH_SHORT)
//                                .show()

                            CoroutineScope(Dispatchers.Default).launch {
                                launch { deleteToServer("comment",infoBasic) }.join()
                                launch { hotViewModel.fetchUserProfile() }
                            }
                            true
                        }
                        else -> true
                    }
                }

                // delete icon listener
                holder.iv_image.setOnClickListener {
                    try {
                        val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                        popup.isAccessible = true
                        val menu = popup.get(popupMenu)
                        menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                            .invoke(menu, true)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        popupMenu.show()
                    }
                    true
                }
            }
        }
    }
}

class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val cafeImg: ImageView = view.findViewById(R.id.profile_cafe_image)
    val cafeName: TextView = view.findViewById(R.id.profile_cafe_name)
    val ratingBar: RatingBar = view.findViewById(R.id.profile_ratingbar)
    val rating: TextView = view.findViewById(R.id.profile_rating)
    val comment: TextView = view.findViewById(R.id.profile_mycomment)
    val date: TextView = view.findViewById(R.id.profile_date)
    val iv_image: ImageView = view.findViewById(R.id.profile_delete)
}

class RecordDiffCallback : DiffUtil.ItemCallback<Record>() {
    override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.cafeName == newItem.cafeName
    }

}

class InfoBasic(var cafeId: Int, var userId: Int)

fun infoBasicToJson(infoBasic: InfoBasic): String {
    val gson = Gson()
    val jsonComment: String = gson.toJson(infoBasic)
    println(jsonComment)
    return jsonComment
}

suspend fun deleteToServer(mode: String, infoBasic: InfoBasic): Int {
    println("Attempting to Delete $mode")

    var url = ""
    when (mode) {
        "comment" -> {
            url =
                "http://35.221.213.180/api/CafeInfo/DeleteDiscuss/${infoBasic.cafeId}/${infoBasic.userId}"
        }
        "rating" -> {
            url =
                "http://35.221.213.180/api/CafeInfo/DeleteScore/${infoBasic.cafeId}/${infoBasic.userId}"
        }
    }

    val body = infoBasicToJson(infoBasic)
    println(body)
    val requestBody = body.toRequestBody("application/json: charset=utf-8".toMediaTypeOrNull())
    println("request_body: $requestBody")

    val request = Request.Builder()
        .url(url)
        .addHeader("Content-Type", "application/json")
        .delete(requestBody)
        .build()

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
                if (httpCode == 201) {
                    //1是成功
                    val body = response?.body?.string()
                    println(body)
                    Log.i("delete Success", "$httpCode", null)
                    val gson = GsonBuilder().create()
                    val result =
                        gson.fromJson<Int>(body, object : TypeToken<Int>() {}.type)
                    it.resumeWith(Result.success(result))
                } else {
                    //2,3,4 是不成功
                    val body = response?.body?.string()
                    println(body)
                    Log.i("delete Failure", "$httpCode", null)
                    val gson = GsonBuilder().create()
                    val result =
                        gson.fromJson<Int>(body, object : TypeToken<Int>() {}.type)
                    it.resumeWith(Result.success(result))
                }
            }
        })
        it.invokeOnCancellation { //連不到的話就取消
            call.cancel()
        }
    }
}

fun delete (mode:String, infoBasic: InfoBasic) {
    CoroutineScope(Dispatchers.Default).launch {
        launch { deleteToServer(mode,infoBasic) }.join()
    }
}