package com.abhi.poochfinder.AppUtilities

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object NetworkUtility {

    fun isDataNetworkAvailable(ctx: Context): Boolean{
        val cm = ctx!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo


        return networkInfo!= null && networkInfo.isConnected
    }

    @Throws(IOException::class)
    fun getOkHttpClient(req: Request): String {
        val client = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

        val response = client.newCall(req   ).execute()
        return response.body()!!.string()
    }

    @Throws(IOException::class)
    fun getResponseWithGet(url: String?) : String {
        Log.d("Url--->",url)

        val request = Request.Builder()
                .url(url)
                .build()

        return getOkHttpClient(request)
    }
}