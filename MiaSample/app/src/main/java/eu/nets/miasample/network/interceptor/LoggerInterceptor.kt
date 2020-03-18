package eu.nets.miasample.network.interceptor

import android.util.Log
import eu.nets.miasample.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import java.lang.Exception
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*


/**
 *  *****Copyright (c) 2020 Nets Denmark A/S*****
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy  of this software
 * and associated documentation files (the "Software"), to deal  in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is  furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
class LoggerInterceptor : Interceptor {

    private val dateFormat = SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss'.'SSS", Locale.getDefault())

    /**
     * Intercept the request and log the URL, BODY with timestamp to facilitate the debugging
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val responseBody = response.body()
        val responseBodyString = responseBody?.string()

        logRequest(request)
        logResponse(response, responseBodyString)

        return response.newBuilder().body(ResponseBody.create(responseBody?.contentType(), responseBodyString!!.toByteArray(Charset.forName("UTF-8")))).build()
    }

    private fun logResponse(response: Response, responseString: String?) {
        if (BuildConfig.DEBUG) {
            Log.e("----- RESPONSE -----", "----")
            Log.w(LoggerInterceptor::class.java.simpleName + dateFormat.format(Date()), "CODE: ${response.code()}")
            Log.w(LoggerInterceptor::class.java.simpleName + dateFormat.format(Date()), "$responseString")
            Log.e("--------------------", "----")
        }
    }

    private fun logRequest(request: Request) {
        if (BuildConfig.DEBUG) {
            Log.e("----- REQUEST -----", "----")
            Log.w(LoggerInterceptor::class.java.simpleName + dateFormat.format(Date()), request.url().toString())
            Log.w(LoggerInterceptor::class.java.simpleName + dateFormat.format(Date()), request.headers().toString())
            Log.w(LoggerInterceptor::class.java.simpleName + dateFormat.format(Date()), request.method())
            Log.e("--------------------", "----")

            val buffer = Buffer()

            try {
                request.body()?.writeTo(buffer)
                Log.w(LoggerInterceptor::class.java.simpleName + dateFormat.format(Date()), buffer.readUtf8())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}