package com.abhi.poochfinder

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.abhi.poochfinder.AppUtilities.SecurePreferencesHelper

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = MainActivity::class.java.simpleName as String

    var prefs: SecurePreferencesHelper? = null
    var editTextUsrname: EditText? = null
    var editTextPass: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = SecurePreferencesHelper(this)
        Log.e(TAG, "Calling getUserName");
        if (prefs!!.getUserName().isNotEmpty()) {
            Log.e(TAG,"User already exists !!: " )

            startTabActivity()
        } else {
            setContentView(R.layout.activity_sign_in)
            editTextUsrname = findViewById(R.id.username)
            editTextPass = findViewById(R.id.password)

            var btnSignup: Button = findViewById<Button>(R.id.sign_up)

            btnSignup.setOnClickListener(this)
        }



    }

    override fun onClick(v: View?) {
        if ((editTextUsrname!!.text.isNotEmpty()) &&
                (editTextPass!!.text.isNotEmpty())) {
            //Store the user credential locally
            prefs!!.savePreferences(editTextUsrname!!.text.toString(),editTextPass!!.text.toString())
            //Start tabview activity
            startTabActivity()
            finish()
        } else {
            Snackbar.make(
                    v!!, // Parent view
                    getString(R.string.user_signup_alert), // Message to show
                    Snackbar.LENGTH_LONG // How long to display the message.
            ).show()
        }

    }

    fun startTabActivity() {
        val intent = Intent(this, TabViewActivity::class.java)
        startActivity(intent)
    }
}
