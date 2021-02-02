package eu.nets.miasample.network.interceptor

import eu.nets.miasample.network.APIManager
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import java.nio.charset.Charset


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
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = addHeader(chain.request())
        val response = chain.proceed(request)
        val responseBody = response.body()
        val responseBodyString = responseBody?.string()

        return response.newBuilder().body(ResponseBody.create(responseBody?.contentType(), responseBodyString!!.toByteArray(Charset.forName("UTF-8")))).build()

    }

    private fun addHeader(request: Request): Request {
        val builder = request.newBuilder().method(request.method(), request.body())

        builder.addHeader("Content-Type", "application/json")
        builder.addHeader("Authorization", APIManager.secretKey)
        /**
         * @param commercePlatformTag This is critical to identify the platform from which the payment is initiated
         */
        builder.addHeader("commercePlatformTag", "AndroidSDK")
        return builder.build()
    }

}