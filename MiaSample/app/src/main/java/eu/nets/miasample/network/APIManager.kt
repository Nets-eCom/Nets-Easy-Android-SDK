package eu.nets.miasample.network

import eu.nets.miasample.BuildConfig
import eu.nets.miasample.network.callback.HttpResponse
import eu.nets.miasample.network.interceptor.HeaderInterceptor
import eu.nets.miasample.network.interceptor.LoggerInterceptor
import eu.nets.miasample.network.request.PaymentActionRequest
import eu.nets.miasample.network.request.RegisterPaymentRequest
import eu.nets.miasample.network.response.ChargePaymentResponse
import eu.nets.miasample.network.response.GetPaymentResponse
import eu.nets.miasample.network.response.RegisterPaymentResponse
import eu.nets.miasample.utils.KeysProvider
import eu.nets.miasample.utils.SharedPrefs
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
class APIManager private constructor(

        private var baseUrl: String = if (SharedPrefs.getInstance().testMode) BuildConfig.TEST_BASE_URL else BuildConfig.PROD_BASE_URL,

        private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
                .addInterceptor(LoggerInterceptor())
                .addInterceptor(HeaderInterceptor())
                .build(),
        private var easyAPIService: APIService = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(APIService::class.java)) {

    companion object {
        private var instance = APIManager()
        var secretKey = if (SharedPrefs.getInstance().testMode) KeysProvider.testSecretKey else KeysProvider.prodSecretKey
        var checkoutKey = if (SharedPrefs.getInstance().testMode) KeysProvider.testCheckoutKey else KeysProvider.prodCheckoutKey
        var checkoutJS = if (SharedPrefs.getInstance().testMode) BuildConfig.TEST_CHECKOUT_JS else BuildConfig.PROD_CHECKOUT_JS

        fun getInstance(): APIManager {
            return instance
        }

        fun recreateInstance() {
            instance = APIManager()
            secretKey = if (SharedPrefs.getInstance().testMode) KeysProvider.testSecretKey else KeysProvider.prodSecretKey
            checkoutKey = if (SharedPrefs.getInstance().testMode) KeysProvider.testCheckoutKey else KeysProvider.prodCheckoutKey
            checkoutJS = if (SharedPrefs.getInstance().testMode) BuildConfig.TEST_CHECKOUT_JS else BuildConfig.PROD_CHECKOUT_JS
        }
    }

    fun registerPayment(request: RegisterPaymentRequest, response: HttpResponse<RegisterPaymentResponse>) {
        val call: Call<RegisterPaymentResponse> = easyAPIService.registerPayment(request)
        call.enqueue(response)
    }

    fun chargePayment(paymentId: String, request: PaymentActionRequest, response: HttpResponse<ChargePaymentResponse>) {
        val call: Call<ChargePaymentResponse> = easyAPIService.chargePayment(paymentId, request)
        call.enqueue(response)
    }

    fun cancelPayment(paymentId: String, request: PaymentActionRequest, response: HttpResponse<String>) {
        val call: Call<String> = easyAPIService.cancelPayment(paymentId, request)
        call.enqueue(response)
    }

    fun getPayment(paymentId: String, response: HttpResponse<GetPaymentResponse>) {
        val call: Call<GetPaymentResponse> = easyAPIService.getPayment(paymentId)
        call.enqueue(response)
    }

}