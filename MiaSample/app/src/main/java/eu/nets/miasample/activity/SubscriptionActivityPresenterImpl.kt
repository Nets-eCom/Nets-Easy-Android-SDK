package eu.nets.miasample.activity

import android.content.Context
import eu.nets.miasample.R
import eu.nets.miasample.network.APIManager
import eu.nets.miasample.network.callback.HttpResponse
import eu.nets.miasample.network.request.*
import eu.nets.miasample.network.request.Subscription
import eu.nets.miasample.network.response.*
import eu.nets.miasample.utils.SampleHtmlProvider
import eu.nets.miasample.utils.SampleLocalHost
import eu.nets.miasample.utils.SharedPrefs
import eu.nets.miasample.utils.Utilities
import java.util.*
import kotlin.collections.ArrayList


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
class SubscriptionActivityPresenterImpl(private var mView: SubscriptionActivityView?) : SubscriptionActivityPresenter {

    private var mPaymentId: String? = null
    private var mSubscriptionId: String? = null
    private var mCheckoutUrl: String = SampleLocalHost.CHECKOUT_URL
    private var mTermsUrl: String = SampleLocalHost.TERMS_URL
    /**
     * Launches the SDK with the paymentId and checkoutUrl
     */
    override fun launchSDK() {
        //register subscription
        createSubscription()
    }

    /**
     * Call /payment API to create a subscription and get a paymentID for the Subscription created to be used in checkout page
     * Api request has the data for creating a subscription
     * If paymentId is created, the localhost server will be started and the SDK is launched
     */
    override fun createSubscription() {
        mView?.showLoader(true)
        APIManager.getInstance().createSubscription(getSubscriptionRequest(), object : HttpResponse<RegisterPaymentResponse>() {
            override fun onSuccess(response: RegisterPaymentResponse?) {
                mPaymentId = response?.paymentId
                mView?.showLoader(false)
                //start server first
                SampleLocalHost.getInstance().startServer(SampleHtmlProvider.getCheckoutHtml(mPaymentId))
                //launch sdk
                when (SharedPrefs.getInstance().integrationType) {
                    MainActivity.MERCHANT_HOSTED_PAYMENT_WINDOW -> {
                        //is merchant hosted payment window - do not send return url
                        mView?.launchEasySDK(mPaymentId, mCheckoutUrl, null, MainActivity.CANCEL_URL)
                    }
                    MainActivity.EASY_HOSTED_PAYMENT_WINDOW -> {
                        //is easy hosted payment window - send the return url
                        mView?.launchEasySDK(mPaymentId, response?.hostedPaymentPageUrl, MainActivity.RETURN_URL, MainActivity.CANCEL_URL)
                    }
                    else -> {
                        //to be continued on future integration types
                    }
                }
            }

            override fun onError(code: Int, error: HttpError) {
                mView?.showLoader(false)
                if (error.errors == null) {
                    mView?.showAlert("${error.code ?: code}: ${
                        error.source
                                ?: getContext().getString(R.string.error_title)
                    }",
                            error.message ?: getContext().getString(R.string.error_message))
                } else {
                    mView?.showAlert(getContext().getString(R.string.error_title), error.parseErrors())
                }
            }
        })
    }

    override fun getSubscriptionId() {
        //call get payment and based on the payment status call api or show popup
        mView?.showLoader(true)

        APIManager.getInstance().getSubscriptionPayment(mPaymentId
                ?: "", object : HttpResponse<SubscriptionRegistrationResponse>() {
            override fun onSuccess(response: SubscriptionRegistrationResponse?) {
                //determine the payment status
                mSubscriptionId = response?.payment?.subscription?.id
                when {
                    Utilities.isStringNullorEmpty(mSubscriptionId) -> {
                        mView?.showLoader(false)
                        //the payment was canceled using the option inside the payment page
                        mView?.showAlert(getContext().getString(R.string.canceled_title), getContext().getString(R.string.transaction_canceled))
                    }
                    else -> {
                        fetchSubscriptionDetails()
                    }
                }
            }

            override fun onError(code: Int, error: HttpError) {
                mView?.showLoader(false)
            }
        })
    }

    override fun chargeSubscription(subscriptionId: String) {
        mView?.showLoader(true)
        val chargePaymentRequest = getChargeSubscriptionRequest()
        APIManager.getInstance().chargeSubscription(subscriptionId
                ?: "", chargePaymentRequest, object : HttpResponse<ChargePaymentResponse>() {
            override fun onSuccess(response: ChargePaymentResponse?) {
                mView?.showLoader(false)
                mView?.showAlert(
                        getContext().getString(R.string.charge_successful),
                        "${getContext().getString(R.string.charge_successful_details, subscriptionId, response?.paymentId)}")
            }

            override fun onError(code: Int, error: HttpError) {
                mView?.showLoader(false)
                if (error.errors == null) {
                    mView?.showAlert("${error.code ?: code}: ${
                        error.source
                                ?: getContext().getString(R.string.error_title)
                    }",
                            error.message ?: getContext().getString(R.string.error_message))
                } else {
                    mView?.showAlert(getContext().getString(R.string.error_message), error.parseErrors())
                }
            }
        })
    }

    override fun fetchSubscriptionDetails() {
        APIManager.getInstance().fetchSubscriptionDetails(mSubscriptionId
                ?: "", object : HttpResponse<SubscriptionDetailsResponse>() {
            override fun onSuccess(response: SubscriptionDetailsResponse?) {
                Utilities.saveSubscriptionDetails(response)
                mView?.showLoader(false)
                mView?.showAlert(
                        getContext().getString(R.string.subscription_created),
                        "${getContext().getString(R.string.subscription_created_alert, mSubscriptionId)}")
                mView?.updateList(response!!)
            }

            override fun onError(code: Int, error: HttpError) {
                mView?.showLoader(false)
                if (error.errors == null) {
                    mView?.showAlert("${error.code ?: code}: ${
                        error.source
                                ?: getContext().getString(R.string.error_title)
                    }",
                            error.message ?: getContext().getString(R.string.error_message))
                } else {
                    mView?.showAlert(getContext().getString(R.string.error_message), error.parseErrors())
                }
            }
        })
    }

    override fun init() {
        mView?.initListeners()
    }

    /**
     * Method called from activity lifecycle to restore the view state
     */
    override fun onResume(subscriptionActivityView: SubscriptionActivity?) {
        this.mView = subscriptionActivityView
    }

    /**
     * Method called from activity lifecycle to clear all cached data
     */
    override fun onDestroy() {
        this.mView = null
    }

    //helper functions
    /**
     * Casts the view to Context
     * This is used to access the string resources
     *
     * @return MainActivity context
     */
    private fun getContext(): Context {
        return mView as Context
    }

    /**
     * Build object to be sent in the /payment API body
     * @return RegisterPaymentRequest object
     */
    private fun getSubscriptionRequest(): RegisterPaymentRequest {
        val registerPaymentRequest = RegisterPaymentRequest()

        val order = Order()
        val checkout = Checkout()
        val consumer = Consumer()
        val shippingAddress = ShippingAddress()
        val phoneNumber = PhoneNumber()
        val privatePerson = PrivatePerson()
        var merchantNumber: String? = null
        var notifications: String? = null

        val subscription = Subscription()
        subscription.endDate = Utilities.createSubscriptionEndDate()/*"2023-03-10T14:43:27+02:00"*/
        subscription.interval = 0
        registerPaymentRequest.subscription = subscription

        val items = Items()
        items.reference = "MiASDK-Android"
        items.name = "Lightning Cable"
        items.quantity = 1
        items.unit = "pcs"
        items.taxRate = 0
        items.taxAmount = 0
        items.grossTotalAmount = mView?.getAmount()
        items.grossTotalAmount = mView?.getAmount()

        order.items = ArrayList(Arrays.asList(items))
        order.amount = mView?.getAmount()
        order.currency = mView?.getCurrency()
        order.reference = "MiASDK-Android"

        registerPaymentRequest.order = order

        val consumerType = ConsumerType()
        consumerType.supportedTypes = ArrayList(Arrays.asList(ConsumerTypeEnum.B2B, ConsumerTypeEnum.B2C))
        consumerType.default = ConsumerTypeEnum.B2C

        //build checkout object based on integration type
        when (SharedPrefs.getInstance().integrationType) {
            MainActivity.MERCHANT_HOSTED_PAYMENT_WINDOW -> {
                /**
                 * Is Merchant Hosted Payment Window
                 * Set here your checkout page URL
                 */
                checkout.url = mCheckoutUrl
                checkout.integrationType = SharedPrefs.getInstance().integrationType
            }
            MainActivity.EASY_HOSTED_PAYMENT_WINDOW -> {
                /**
                 * Is Easy hosted payment window
                 */
                checkout.integrationType = MainActivity.INTEGRATION_TYPE_PARAM
                checkout.returnURL = MainActivity.RETURN_URL
                checkout.cancelUrl = MainActivity.CANCEL_URL
            }
            else -> {//to be continued for future integration types
            }
        }

        /**
         * Set here your Terms And Conditions page URL; The content of this page must contain
         * your terms and conditions for user payments
         */
        checkout.termsUrl = mTermsUrl
        checkout.consumerType = consumerType
        checkout.charge = true

        if (!SharedPrefs.getInstance().consumerData.equals(MainActivity.CONSUMER_DATA_NONE)) {
            if (SharedPrefs.getInstance().consumerData.equals(MainActivity.CONSUMER_DATA_MERCHANT_INJECTED)) {
                privatePerson.firstName = SharedPrefs.getInstance().firstName
                privatePerson.lastName = SharedPrefs.getInstance().lastName
                consumer.privatePerson = privatePerson

                phoneNumber.prefix = SharedPrefs.getInstance().prefix
                phoneNumber.number = SharedPrefs.getInstance().phoneNumber
                consumer.phoneNumber = phoneNumber

                shippingAddress.addressLine1 = SharedPrefs.getInstance().addressLineOne
                shippingAddress.addressLine2 = SharedPrefs.getInstance().addressLineTwo
                shippingAddress.city = SharedPrefs.getInstance().city
                shippingAddress.country = SharedPrefs.getInstance().countryCode
            }
            shippingAddress.postalCode = SharedPrefs.getInstance().postalCode
            consumer.email = SharedPrefs.getInstance().email
            consumer.shippingAddress = shippingAddress
            checkout.merchantHandlesConsumerData = true
            checkout.consumer = consumer
        }

        registerPaymentRequest.checkout = checkout
        registerPaymentRequest.merchantNumber = merchantNumber
        registerPaymentRequest.notifications = notifications
        return registerPaymentRequest
    }

    /**
     * Build object to be sent in the charge subscription API body
     * @return RegisterPaymentRequest object
     */
    private fun getChargeSubscriptionRequest(): ChargeRequest {
        val registerPaymentRequest = ChargeRequest()
        val order = Order()
        val items = Items()
        items.reference = "MiASDK-Android"
        items.name = "Lightning Cable"
        items.quantity = 1
        items.unit = "pcs"
        items.taxRate = 0
        items.taxAmount = 0
        items.grossTotalAmount = mView?.getAmount()
        items.grossTotalAmount = mView?.getAmount()

        order.items = ArrayList(Arrays.asList(items))
        order.amount = mView?.getAmount()
        order.currency = mView?.getCurrency()
        order.reference = "MiASDK-Android"

        registerPaymentRequest.order = order
        return registerPaymentRequest
    }

}