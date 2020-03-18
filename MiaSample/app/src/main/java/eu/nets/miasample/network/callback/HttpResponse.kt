package eu.nets.miasample.network.callback

import com.google.gson.Gson
import eu.nets.miasample.network.response.HttpError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


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
abstract class HttpResponse<T> : Callback<T> {

    private val failureCode = 99
    abstract fun onSuccess(response: T?)
    abstract fun onError(code: Int, error: HttpError)

    /**
     * Intercept the server response here, and deliver it through the abstract functions declared
     * Mainly, handle the error cases to have one generic error object
     */
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            onSuccess(response.body())
        } else {
            //handle error case
            val error: HttpError
            val errorBody = response.errorBody()?.string()
            error = try {
                if (errorBody != null) {
                    //is error with fields: code, source, message
                    Gson().fromJson(errorBody, HttpError::class.java)
                } else {
                    HttpError(response.raw()?.code()?.toString(), "Unknown error")
                }
            } catch (e: Exception) {
                //is error with error list of objects, with dynamic key
                HttpError(errorBody)
            }

            onError(response.raw()?.code() ?: failureCode, error)
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        onError(failureCode, HttpError(failureCode.toString(), t.message))
    }
}