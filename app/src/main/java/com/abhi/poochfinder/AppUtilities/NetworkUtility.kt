package com.abhi.poochfinder.AppUtilities

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import okhttp3.*
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * An utility class to call all okhttp3 APIs
 *
 */

object NetworkUtility {

    /**
     * Function to check whether data network is
     * available or not.
     *
     * @return : True, if connectivity is available
     *           false, if data or wifi is not available
     *
     */
    fun isDataNetworkAvailable(ctx: Context): Boolean{
        val cm = ctx!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo

        return networkInfo!= null && networkInfo.isConnected
    }

    /**
     * Function to create OkHttpClient instance
     * and get response for request
     *
     * @param: req - Response request
     *
     * @return : Response body as string
     *
     * @throws: IOException
     *
     */

    @Throws(IOException::class)
    fun getOkHttpClient(req: Request): String {
        val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

        val response = client.newCall(req).execute()
        return response.body()!!.string()
    }

    /**
     * Function to fetch response from uri
     * and get response for request
     *
     * @param: uri - Uri to fetch
     *
     * @return : Response body as string
     *
     * @throws: IOException
     *
     */

    @Throws(IOException::class)
    fun getResponseWithGet(url: String?) : String {
        Log.d("Url--->",url)

        val request = Request.Builder()
                .url(url)
                .build()

        return getOkHttpClient(request)
    }
}