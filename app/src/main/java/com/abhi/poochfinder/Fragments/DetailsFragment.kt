package com.abhi.poochfinder.Fragments

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.abhi.poochfinder.ApiResponse.ApiResponse
import com.abhi.poochfinder.AppUtilities.AppConstants
import com.abhi.poochfinder.AppUtilities.NetworkBackgroundOp
import com.abhi.poochfinder.Model.PoochUriResponse
import com.abhi.poochfinder.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DetailsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
var imgSrc: ImageView?= null
var breedName: TextView? = null
var wm: WindowManager? = null


class DetailsFragment : Fragment(), View.OnClickListener, ApiResponse {
    private var listener: OnFragmentInteractionListener? = null

    private val TAG = DetailsFragment::class.java.simpleName as String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_details, container, false)

        val btnName = view.findViewById<Button>(R.id.btnName)

        imgSrc = view.findViewById(R.id.imgSource)
        breedName = view.findViewById(R.id.txtBreedName)

        imgSrc!!.scaleType = ImageView.ScaleType.FIT_CENTER

        btnName.setOnClickListener(this)

        wm = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onClick(v: View?) {
        Log.e(TAG,"Button clicked")
        NetworkBackgroundOp(context!!,true, AppConstants.ApiUrls.fetchInfoUrl,
                AppConstants.ApiCode.FETCH_DATA)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DetailsFragment.
         */
        @JvmStatic
        fun newInstance() =
                DetailsFragment().apply {
                    arguments = Bundle().apply {
                    }
                }
    }

    override fun apiResponsePostProcessing(response: String, apiCode: Int) {
        Log.e(TAG, " apiResponsePostProcessing FETCH_DATA apiCode : $apiCode")
        when (apiCode) {
            AppConstants.ApiCode.FETCH_DATA -> {
                Log.e("PageFragment", " apiResponsePostProcessing FETCH_DATA: $response")
                val gson = Gson()

                val topic = gson.fromJson(response, PoochUriResponse::class.java)

                val parts = topic!!.message.split("/")

                var breed: String = parts[parts.size - 2]

                val strArray = breed.split("-")

                val builder = StringBuilder()
                for (s in strArray) {
                    val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
                    builder.append("$cap ")
                }
                breed = builder.toString()

                Log.d(TAG, "Breed is: $breed" )

                updateViewContent(breed, topic.message)
            }
        }
    }

    override fun networkError(apiCode: Int) {
        Log.d("PageFragment", " networkError")
    }

    override fun responseError(responseError: String, apiCode: Int) {
        Log.d("PageFragment", " responseError")
    }

    fun fetchDataFromServer(ctx: Context ) {
        if(breedName!!.text.equals(ctx.getString(R.string.unknown_breed))) {
            NetworkBackgroundOp(ctx, true, AppConstants.ApiUrls.fetchInfoUrl,
                    AppConstants.ApiCode.FETCH_DATA)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this)
        } else {
            Log.e(TAG, "No need to fetch data")
        }
    }

    private fun updateViewContent(breed: String, imgUri: String) {

        breedName!!.text = breed

        val metrics = DisplayMetrics()

        wm!!.defaultDisplay.getMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val maxWidthAndHeight = 1040
        var newWidth = width
        var newHeight = maxWidthAndHeight


        if (width >= height){
            val ratio:Float = width.toFloat() / height.toFloat()

            newWidth = maxWidthAndHeight
            // Calculate the new height for the scaled bitmap
            newHeight = Math.round(maxWidthAndHeight / ratio)
        }

        Log.d(TAG, "Width: " + width + "height: " + height)
        Log.d(TAG, "newWidth: " + newWidth + "newHeight: " + newHeight)


        Picasso.get()
                .load(imgUri)
                .error(R.drawable.error)
                .resize(newWidth, newHeight)
                .into(imgSrc)
    }
}
