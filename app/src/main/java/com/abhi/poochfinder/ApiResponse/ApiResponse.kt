package com.abhi.poochfinder.ApiResponse

/**
 * An interface to handle network response.
 * Activities that want http requests must implement below interface
 * to handle network events.
 *
 * apiResponsePostProcessing : Callback for handling successful network response
 *
 * networkError: Callback for network error handling
 *
 * responseError: Callback for network response error handling
 *
 */

interface ApiResponse {
    fun apiResponsePostProcessing(response: String, apiCode: Int)
    fun networkError(apiCode: Int)
    fun responseError(responseError: String, apiCode: Int)
}