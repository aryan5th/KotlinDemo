package com.abhi.poochfinder

import com.abhi.poochfinder.AppUtilities.AppConstants
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class FragmentsTest {
    @Test
    fun repository_urlEmpty(){
        val uri = AppConstants.ApiUrls.fetchInfoUrl
        assertNotNull(uri)
    }


}
