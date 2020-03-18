package eu.nets.miasample.activity

import android.widget.TextView


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
interface MainActivityPresenter {

    /**
     * Call view's initListeners
     */
    fun init()

    /**
     * Launches the SDK with the paymentId and checkoutUrl
     */
    fun launchSDK()

    /**
     * Method called from activity lifecycle to restore the view state
     */
    fun onResume(mainActivityView: MainActivityView?)

    /**
     * Method called from activity lifecycle to clear all cached data
     */
    fun onDestroy()

    /**
     * Call /payment API to get a paymentID to be used in the checkout page
     * If paymentId is created, the localhost server will be started and the SDK is launched
     */
    fun registerPayment()

    /**
     * Call /payment/{paymentId} to retrieve the payment status
     */
    fun getPayment()

    /**
     * If the payment is successful, finish the payment process by charging the reserved amount
     */
    fun chargePayment()

    /**
     * Cancel the authorization for a specific payment. For current flow in this DEMO app,
     * the Cancel will be made after each payment, so no amounts will be taken from the card.
     * If you want to test the entire flow, call [chargePayment] instead of [cancelPayment]
     */
    fun cancelPayment()

    /**
     * Validation method to check the Consumer Data in Settings if it is Merchant Injected,
     * No Shipping or None. If None is selected it will call the launchSDK method without validation.
     */
    fun validateProfileData(): Boolean

    /**
     * Call this method to check which field is empty in the profile screen and set the message
     * string to highlight the missing field.
     * @param stringToCheck the string to check which field is empty
     * @param textElement the TextView to be used to set the String message
     * @param stringToSet the String message to be set on the TextView
     */
    fun setTextView(stringToCheck: String, textElement: TextView, stringToSet: String)

}