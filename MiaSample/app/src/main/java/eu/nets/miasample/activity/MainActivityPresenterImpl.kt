package eu.nets.miasample.activity

import android.content.Context
import android.util.Patterns
import android.view.View
import android.widget.TextView
import eu.nets.miasample.R
import eu.nets.miasample.activity.MainActivity.Companion.CANCEL_URL
import eu.nets.miasample.activity.MainActivity.Companion.CONSUMER_DATA_MERCHANT_INJECTED
import eu.nets.miasample.activity.MainActivity.Companion.CONSUMER_DATA_NONE
import eu.nets.miasample.activity.MainActivity.Companion.CONSUMER_DATA_NO_SHIPPING_ADDR
import eu.nets.miasample.network.APIManager
import eu.nets.miasample.network.callback.HttpResponse
import eu.nets.miasample.network.request.*
import eu.nets.miasample.network.response.ChargePaymentResponse
import eu.nets.miasample.network.response.HttpError
import eu.nets.miasample.network.response.PaymentResponse
import eu.nets.miasample.network.response.RegisterPaymentResponse
import eu.nets.miasample.utils.SampleHtmlProvider
import eu.nets.miasample.utils.SampleLocalHost
import eu.nets.miasample.utils.SharedPrefs
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
class MainActivityPresenterImpl(private var mView: MainActivityView?) : MainActivityPresenter {

    private var mPaymentId: String? = null
    private var mCheckoutUrl: String = SampleLocalHost.CHECKOUT_URL
    private var mTermsUrl: String = SampleLocalHost.TERMS_URL

    //main activity presenter interface
    /**
     * Call view's initListeners
     */
    override fun init() {
        mView?.initListeners()
    }

    /**
     * Launches the SDK with the paymentId and checkoutUrl
     */
    override fun launchSDK() {
        //register payment
        registerPayment()
    }

    /**
     * Method called from activity lifecycle to restore the view state
     */
    override fun onResume(mainActivityView: MainActivityView?) {
        this.mView = mainActivityView
    }

    /**
     * Method called from activity lifecycle to clear all cached data
     */
    override fun onDestroy() {
        this.mView = null
    }

    /**
     * Call /payment API to get a paymentID to be used in the checkout page
     * If paymentId is created, the localhost server will be started and the SDK is launched
     */
    override fun registerPayment() {
        mView?.showLoader(true)
        APIManager.getInstance().registerPayment(getPaymentRequest(), object : HttpResponse<RegisterPaymentResponse>() {
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

    /**
     * Call /payment/{paymentId} to retrieve the payment status
     */
    override fun getPayment() {
        //call get payment and based on the payment status call api or show popup
        mView?.showLoader(true)

        APIManager.getInstance().getPayment(mPaymentId
                ?: "", object : HttpResponse<PaymentResponse>() {
            override fun onSuccess(response: PaymentResponse?) {
                //determine the payment status
                when {
                    response?.payment?.paymentReserved() == true -> //the amount was reserverd - proceed with charge or cancel
                        when (SharedPrefs.getInstance().chargePayment) {
                            true -> chargePayment()
                            else -> cancelPayment()
                        }
                    response?.payment?.paymentCancelled() == true -> {
                        mView?.showLoader(false)
                        //the payment was canceled using the option inside the payment page
                        mView?.showAlert(getContext().getString(R.string.canceled_title), getContext().getString(R.string.transaction_canceled))
                    }
                    response?.payment?.paymentCharged() == true -> {
                        mView?.showLoader(false)
                        //the payment was already charged
                        mView?.showAlert(getContext().getString(R.string.success_title), getContext().getString(R.string.success_message))
                    }
                    else -> mView?.showLoader(false)
                }
            }

            override fun onError(code: Int, error: HttpError) {
                mView?.showLoader(false)
            }
        })
    }

    /**
     * If the payment is successful, finish the payment process by charging the reserved amount
     */
    override fun chargePayment() {
        val chargePaymentRequest = getOrderRequest()
        APIManager.getInstance().chargePayment(mPaymentId
                ?: "", chargePaymentRequest, object : HttpResponse<ChargePaymentResponse>() {
            override fun onSuccess(response: ChargePaymentResponse?) {
                mView?.showLoader(false)
                mView?.showAlert(
                        getContext().getString(R.string.success_title),
                        "${getContext().getString(R.string.success_message)} (Authorization charged)")
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

    /**
     * Cancel the authorization for a specific payment. For current flow in this DEMO app,
     * the Cancel will be made after each payment, so no amounts will be taken from the card.
     * If you want to test the entire flow, call [chargePayment] instead of [cancelPayment]
     */
    override fun cancelPayment() {
        val cancelPaymentRequest = getOrderRequest()
        APIManager.getInstance().cancelPayment(mPaymentId
                ?: "", cancelPaymentRequest, object : HttpResponse<String>() {
            override fun onSuccess(response: String?) {
                mView?.showLoader(false)
                mView?.showAlert(
                        getContext().getString(R.string.success_title),
                        "${getContext().getString(R.string.success_message)} (Authorization cancelled)")
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

    //end

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
     *
     * @return RegisterPaymentRequest object
     */
    private fun getPaymentRequest(): RegisterPaymentRequest {
        val registerPaymentRequest = RegisterPaymentRequest()

        val order = Order()
        val checkout = Checkout()
        val consumer = Consumer()
        val shippingAddress = ShippingAddress()
        val phoneNumber = PhoneNumber()
        val privatePerson = PrivatePerson()

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
        checkout.charge = false

        if (!SharedPrefs.getInstance().consumerData.equals(CONSUMER_DATA_NONE)) {
            if (SharedPrefs.getInstance().consumerData.equals(CONSUMER_DATA_MERCHANT_INJECTED)) {
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

        return registerPaymentRequest
    }


    /**
     * Builds the object to be send in the /charges API body
     *
     * @return PaymentActionRequest object
     */
    private fun getOrderRequest(): PaymentActionRequest {
        val paymentActionRequest = PaymentActionRequest()

        val items = Items()
        items.reference = "MiASDK-Android"
        items.name = "Lightning Cable"
        items.quantity = 1
        items.unit = "pcs"
        items.taxRate = 0
        items.taxAmount = 0
        items.grossTotalAmount = mView?.getAmount()
        items.grossTotalAmount = mView?.getAmount()

        paymentActionRequest.items = ArrayList(Arrays.asList(items))
        paymentActionRequest.amount = mView?.getAmount()

        return paymentActionRequest
    }
    //end

    override fun validateProfileData(): Boolean {
        if (SharedPrefs.getInstance().consumerData.equals(CONSUMER_DATA_MERCHANT_INJECTED)) {
            return validateProfileDataForMerchantInjected()
        } else if (SharedPrefs.getInstance().consumerData.equals(CONSUMER_DATA_NO_SHIPPING_ADDR)) {
            return validateProfileDataForNoShipping()
        }
        return true
    }

    fun validateProfileDataForMerchantInjected(): Boolean {
        if (isStringEmpty(SharedPrefs.getInstance().firstName)
                || isStringEmpty(SharedPrefs.getInstance().lastName)
                || isStringEmpty(SharedPrefs.getInstance().prefix)
                || isStringEmpty(SharedPrefs.getInstance().phoneNumber)
                || isStringEmpty(SharedPrefs.getInstance().addressLineOne)
                || isStringEmpty(SharedPrefs.getInstance().postalCode)
                || isStringEmpty(SharedPrefs.getInstance().city)
                || isStringEmpty(SharedPrefs.getInstance().countryCode)
                || isStringEmpty(SharedPrefs.getInstance().email)
                || !Patterns.EMAIL_ADDRESS.matcher(SharedPrefs.getInstance().email).matches()) {
            mView?.showProfileDataValidationDialog()
            return false
        }
        return true
    }

    override fun setTextView(stringToCheck: String, textElement: TextView, stringToSet: String) {
        if (isStringEmpty(stringToCheck)) {
            textElement.visibility = View.VISIBLE
            textElement.text = stringToSet
        }
    }

    fun isStringEmpty(stringToCheck: String): Boolean {
        if (stringToCheck.isEmpty()) {
            return true
        }
        return false
    }

    fun validateProfileDataForNoShipping(): Boolean {
        if (isStringEmpty(SharedPrefs.getInstance().postalCode)
                || isStringEmpty(SharedPrefs.getInstance().email)
                || !Patterns.EMAIL_ADDRESS.matcher(SharedPrefs.getInstance().email).matches()) {
            mView?.showProfileDataValidationDialog()
            return false
        }
        return true
    }

}