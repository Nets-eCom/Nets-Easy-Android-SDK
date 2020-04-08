package eu.nets.miasample.network

import eu.nets.miasample.network.request.ChargeRequest
import eu.nets.miasample.network.request.PaymentActionRequest
import eu.nets.miasample.network.request.RegisterPaymentRequest
import eu.nets.miasample.network.response.*
import retrofit2.Call
import retrofit2.http.*


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
interface APIService {

    @POST("v1/payments")
    fun registerPayment(
            @Body body: RegisterPaymentRequest
    ): Call<RegisterPaymentResponse>

    @POST("v1/payments/{PAYMENT_ID}/charges")
    fun chargePayment(
            @Path("PAYMENT_ID") paymentId: String,
            @Body body: PaymentActionRequest
    ): Call<ChargePaymentResponse>

    @POST("v1/payments/{PAYMENT_ID}/cancels")
    fun cancelPayment(
            @Path("PAYMENT_ID") paymentId: String,
            @Body body: PaymentActionRequest
    ): Call<String>

    @GET("v1/payments/{PAYMENT_ID}")
    fun getPayment(
            @Path("PAYMENT_ID") paymentId: String
    ): Call<PaymentResponse>

    @GET("v1/payments/{PAYMENT_ID}")
    fun fetchSubscriptionPayment(
            @Path("PAYMENT_ID") paymentId: String
    ): Call<SubscriptionRegistrationResponse>

    @POST("v1/payments")
    fun createSubscription(
            @Body body: RegisterPaymentRequest
    ): Call<RegisterPaymentResponse>

    @POST("v1/subscriptions/{SUBSCRIPTIONID}/charges")
    fun chargeSubscription(
            @Path("SUBSCRIPTIONID") subscriptionId: String,
            @Body body: ChargeRequest
    ): Call<ChargePaymentResponse>

    @GET("v1/subscriptions/{SUBSCRIPTIONID}")
    fun fetchSubscriptionDetails(
            @Path("SUBSCRIPTIONID") subscriptionId: String
    ): Call<SubscriptionDetailsResponse>

}