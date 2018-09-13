package com.abhi.poochfinder.AppUtilities

import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.ProgressBar
import com.abhi.poochfinder.ApiResponse.ApiResponse
import com.abhi.poochfinder.R
import java.io.IOException
import java.net.SocketTimeoutException


class NetworkBackgroundOp : AsyncTask<Any, Void, String> {

    private var context: Context? = null
    private var url: String? = null
    private var apiResponse: ApiResponse? = null
    private var code: Int = 0
    private var isGET: Boolean = false
    private var isProgressBar: Boolean = false
    private var isFile: Boolean = false
    private var alertDialogBuilder : AlertDialog.Builder? = null
    private var alertDialog : AlertDialog? = null

    private var TAG = NetworkBackgroundOp::class.java.simpleName as String

    constructor(context: Context,isProgressBar:Boolean, url: String?, code: Int) {
        Log.d(TAG, "Constructor called")
        this.context = context

        this.url = url
        this.code = code
        this.isGET = true
        this.isProgressBar = isProgressBar
        this.isFile = false
    }

    override fun onPreExecute() {
        Log.e(TAG,"onPreExecute Request URL :"+url + "Connection: " +
                NetworkUtility.isDataNetworkAvailable(context!!));
        if (!NetworkUtility.isDataNetworkAvailable(context!!)) {
            showErrorDialog(context!!.getString(R.string.connectivity_error),
                            context!!.getString(R.string.connectivity_error_msg))

            this.cancel(true)
            return
        }
        else        {
            if(isProgressBar) {
                showProgressDialog()
            }
        }

    }

    override fun doInBackground(vararg arg0: Any): String? {

        apiResponse = arg0[0] as ApiResponse
        var response: String? = null
        if (isGET) {
            Log.d(TAG, "calling NetworkUtility.getResponseWithGet for url : " + url)
            try {
                response = NetworkUtility.getResponseWithGet(url)//get method
            }
            catch (e: Exception) {
                when(e) {
                    is SocketTimeoutException,
                    is IOException -> {
                        Log.d(TAG, "Exception occured !!!")
                        showErrorDialog(context!!.getString(R.string.connectivity_error),
                                context!!.getString(R.string.connectivity_error_msg))

                    }
                }

            }
        }

        return response
    }

    override fun onPostExecute(response: String?) {

        Log.e(TAG,"onPostExecute: Response in ASYNC :" + response)
        if(alertDialog !=null) {
            if(alertDialog!!.isShowing)
            {
                alertDialog!!.dismiss()
            }
        }
        if(response == null) {
            Log.d(TAG,"Response is null :" + response)

            showErrorDialog(context!!.getString(R.string.server_error),
                            context!!.getString(R.string.server_error_msg))
        } else {
            apiResponse!!.apiResponsePostProcessing(response, code)
        }
    }

    private fun showProgressDialog(){
        alertDialogBuilder = AlertDialog.Builder(context!!)
        alertDialogBuilder!!.setTitle(R.string.progress_dialog_title)
        alertDialogBuilder!!.setCancelable(true)
        var progressBar = ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal)

        progressBar!!.isIndeterminate = true
        alertDialogBuilder!!.setView(progressBar)
        alertDialog = alertDialogBuilder!!.show()
    }

    private fun showErrorDialog(title: String, msg: String) {
        alertDialogBuilder = AlertDialog.Builder(context!!)
        alertDialogBuilder!!.setTitle(title)
        alertDialogBuilder!!.setMessage(msg)
        alertDialogBuilder!!.setCancelable(false)
        alertDialogBuilder!!.setNegativeButton(R.string.error_dialog_ok) {
            dialog,
                which -> dialog.dismiss()
        }
        alertDialogBuilder!!.setPositiveButton(R.string.error_dialog_retry) {
            dialog,
                which ->
                    NetworkBackgroundOp(context!!,isProgressBar, url, code).execute(context)
        }
        alertDialogBuilder!!.show()
    }
}