package com.abhi.poochfinder.Fragments

import android.app.Activity
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
import android.widget.ProgressBar
import android.widget.TextView
import com.abhi.poochfinder.ApiResponse.ApiResponse
import com.abhi.poochfinder.AppUtilities.AppConstants
import com.abhi.poochfinder.AppUtilities.NetworkBackgroundOp
import com.abhi.poochfinder.Model.PoochUriResponse
import com.abhi.poochfinder.R
import com.google.gson.Gson
import com.squareup.picasso.Picasso


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
var myprogressBar:ProgressBar?  = null

class DetailsFragment : Fragment(), View.OnClickListener, ApiResponse {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null


    private val TAG = DetailsFragment::class.java.simpleName as String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_details, container, false)
        var btnName: Button = view.findViewById(R.id.btnName)

        imgSrc = view.findViewById(R.id.imgSource)
        breedName = view.findViewById(R.id.txtBreedName)

        Log.e(TAG, "Breedname is : " + breedName!!.text)

        btnName.setOnClickListener(this)

        wm = getContext()!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        return view
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        Log.e(TAG, "onButtonPressed")
        listener?.onFragmentInteraction(uri)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onClick(v: View?) {
        Log.e(TAG,"Button clicked")
        NetworkBackgroundOp(activity as Activity,true, AppConstants.ApiUrls.fetchInfoUrl,
                AppConstants.ApiCode.FETCH_DATA)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
                DetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun apiResponsePostProcessing(response: String, apiCode: Int) {
        Log.e("PageFragment", " apiResponsePostProcessing FETCH_DATA apiCode" + apiCode);
        when (apiCode) {
            AppConstants.ApiCode.FETCH_DATA -> {
                Log.e("PageFragment", " apiResponsePostProcessing FETCH_DATA" + response);
                val gson = Gson()

                val topic = gson.fromJson(response, PoochUriResponse::class.java)

                //val domain: String? = topic!!.message.substringAfterLast("@")

                val parts = topic!!.message.split("/")
                Log.e("Abhi:", "Splitted string size: "+ parts.size + "name :" + parts[parts.size - 2]);
                var breed: String = parts[parts.size - 2]

                val strArray = breed.split("-")

                val builder = StringBuilder()
                for (s in strArray) {
                    val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
                    builder.append("$cap ")
                }
                breed = builder.toString()
                Log.e(TAG, "Breed is: " + breed )
                //var pooch = PoochInfo(breed, "My cute puppy", topic!!.message)

                //detailsAdapter!!.updateData(pooch)
                updateViewContent(breed, topic!!.message);

            }
        }
    }

    override fun networkError(apiCode: Int) {
        Log.d("PageFragment", " networkError");
    }

    override fun responseError(responseError: String, apiCode: Int) {
        Log.d("PageFragment", " responseError");
    }

    fun fetchDataFromServer(ctx: Context) {
        Log.d("Abhi:", "Calling BackgroundAsyncTask ")
        if(breedName!!.text.equals(ctx.getString(R.string.unknown_breed))) {
            NetworkBackgroundOp(ctx, true, AppConstants.ApiUrls.fetchInfoUrl,
                    AppConstants.ApiCode.FETCH_DATA)
                    .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, this)
        } else {
            Log.e(TAG, "No need to fetch data")
        }
    }

    fun updateViewContent(breed: String, imgUri: String) {

        breedName!!.text = breed

        val metrics = DisplayMetrics()

        wm!!.defaultDisplay.getMetrics(metrics)

        val width = metrics.widthPixels
        val height = metrics.heightPixels

        var newWidth = 0
        var newHeight = 0
        var maxWidthAndHeight = 1040

        if (width >= height){
            val ratio:Float = width.toFloat() / height.toFloat()

            newWidth = maxWidthAndHeight
            // Calculate the new height for the scaled bitmap
            newHeight = Math.round(maxWidthAndHeight / ratio)
        } else {
            val ratio:Float = height.toFloat() / width.toFloat()

            // Calculate the new width for the scaled bitmap
            newWidth = width //Math.round(maxWidthAndHeight / ratio)
            newHeight = maxWidthAndHeight
        }

        Log.d(TAG, "Width: " + width + "height: " + height)
        Log.d(TAG, "newWidth: " + newWidth + "newHeight: " + newHeight)

        Picasso.get()
                .load(imgUri)
                .resize(newWidth, newHeight)
                .centerCrop()
                .into(imgSrc)
    }
}
