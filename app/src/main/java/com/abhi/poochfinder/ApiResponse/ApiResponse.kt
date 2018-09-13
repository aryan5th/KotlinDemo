package com.abhi.poochfinder.ApiResponse

interface ApiResponse {
    fun apiResponsePostProcessing(response: String, apiCode: Int)
    fun networkError(apiCode: Int)
    fun responseError(responseError: String, apiCode: Int)
}