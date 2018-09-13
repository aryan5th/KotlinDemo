package com.abhi.poochfinder.AppUtilities


import android.content.Context
import android.preference.PreferenceManager
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec


class SecurePreferencesHelper(context: Context) {

    object FieldType {
        val USERNAME = 1
        val PASSWORD = 2
    }

    companion object {
        private const val USERNAME = "data.source.prefs.USERNAME"
        private const val PASSWORD = "data.source.prefs.PASSWORD"
        private const val USER_ENCRYPTIV = "data.source.prefs.USERENCRYPTIV"
        private const val PASSWORD_ENCRYPTIV = "data.source.prefs.PASSENCRYPTIV"

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TRANSFORMATION = (KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)

        private const val KEY_NAME = "key"

        private val TAG = SecurePreferencesHelper::class.java.simpleName as String

    }

    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    fun savePreferences(username: String, password: String) {
        saveCredentialsAndLogin(username, password)
    }

    fun getUserName() : String {
        return getDecryptedString(FieldType.USERNAME)
    }

    fun getPassword() : String{
        return getDecryptedString(FieldType.PASSWORD)
    }

    private fun createKey(): SecretKey {
        try {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
            keyGenerator.init(KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(false)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())

            return keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException("Failed to create a symmetric key", e)
        }

    }

    private fun saveCredentialsAndLogin(username: String, password: String) {
        try {
            // encrypt the password and username

            val secretKey = createKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val passwordEncryptionIv = cipher.getIV()

            val editor = preferences.edit()

            // store the login data in the shared preferences

            val passwordBytes = password.toByteArray(Charsets.UTF_8)
            val encryptedPasswordBytes = cipher.doFinal(passwordBytes)
            val encryptedPassword = Base64.getEncoder().encodeToString(encryptedPasswordBytes)

            editor.putString(PASSWORD, encryptedPassword).apply()
            editor.putString(PASSWORD_ENCRYPTIV, Base64.getEncoder().encodeToString(passwordEncryptionIv)).apply()

            val userCipher = Cipher.getInstance(TRANSFORMATION)
            userCipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val userEncryptionIv = userCipher.getIV()

            val userNameBytes = username.toByteArray(Charsets.UTF_8)
            val encryptedUserNameBytes = userCipher.doFinal(userNameBytes)
            val encryptedUserName = Base64.getEncoder().encodeToString(encryptedUserNameBytes)

            editor.putString(USERNAME, encryptedUserName).apply()
            editor.putString(USER_ENCRYPTIV, Base64.getEncoder().encodeToString(userEncryptionIv)).apply()


            Log.e("Abhi:", "encryptedPassword: " + encryptedPassword)

        } catch (e: UserNotAuthenticatedException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw RuntimeException(e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException(e)
        }
    }

    private fun getDecryptedString(type: Int): String {
        try {
            // load login data from shared preferences
            // IV used for the encryption is loaded from shared preferences
            var base64EncryptedString: String? = null
            var base64EncryptionIv: String? = null

            val sharedPreferences = preferences

            if (type == FieldType.USERNAME) {
                base64EncryptedString = sharedPreferences.getString(USERNAME, null)
                base64EncryptionIv = sharedPreferences.getString(USER_ENCRYPTIV, null)

            } else {
                base64EncryptedString = sharedPreferences.getString(PASSWORD, null)
                base64EncryptionIv = sharedPreferences.getString(PASSWORD_ENCRYPTIV, null)
            }

            base64EncryptedString?.length ?:  return ""

            val encryptionIv = Base64.getDecoder().decode(base64EncryptionIv)
            val encryptedPassword = Base64.getDecoder().decode(base64EncryptedString)

            // decrypt the password
            val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
            keyStore.load(null)
            val secretKey = keyStore.getKey(KEY_NAME, null) as SecretKey
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(encryptionIv))
            val passwordBytes = cipher.doFinal(encryptedPassword)
            val decryptedStr = String(passwordBytes, Charsets.UTF_8)

            Log.e(TAG, "string after decryption is : " + decryptedStr)

            return decryptedStr
        } catch (e: UserNotAuthenticatedException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException(e)
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return ""
    }
}