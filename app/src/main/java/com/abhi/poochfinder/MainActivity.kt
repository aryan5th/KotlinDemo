package com.abhi.poochfinder

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.abhi.poochfinder.AppUtilities.SecurePreferencesHelper
import kotlinx.android.synthetic.main.activity_sign_in.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = MainActivity::class.java.simpleName as String

    private var prefs: SecurePreferencesHelper? = null
    private var editTextUsername: EditText? = null
    private var editTextPass: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = SecurePreferencesHelper(this)

        /* Check if user data is available.
         * If userdata is available, then start
         * tab view activity.
         */
        if (prefs!!.getUserName().isNotEmpty()) {
            Log.d(TAG,"User already exists !!: " )

            startTabActivity()
        } else {
            setContentView(R.layout.activity_sign_in)
            editTextUsername = findViewById(R.id.username)
            editTextPass = findViewById(R.id.password)

            val btnSignup: Button = findViewById(R.id.sign_up)

            btnSignup.setOnClickListener(this)

            /* Register for click event on screen
             * Hide soft keyboard if user click on screen */
            viewContainer.setOnClickListener { v ->
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(viewContainer.windowToken, 0)
            }
        }
    }

    override fun onClick(v: View?) {
        if ((editTextUsername!!.text.isNotEmpty()) &&
                (editTextPass!!.text.isNotEmpty())) {

            //Store the user credential locally
            prefs!!.savePreferences(editTextUsername!!.text.toString(),
                                    editTextPass!!.text.toString())

            //Start tabview activity
            startTabActivity()

            //Finish main activity
            finish()
        } else {
            Snackbar.make(
                    v!!, // Parent view
                    getString(R.string.user_signup_alert), // Message to show
                    Snackbar.LENGTH_LONG // How long to display the message.
            ).show()
        }

    }

    private fun startTabActivity() {
        val intent = Intent(this, TabViewActivity::class.java)
        startActivity(intent)
        finish()
    }
}
