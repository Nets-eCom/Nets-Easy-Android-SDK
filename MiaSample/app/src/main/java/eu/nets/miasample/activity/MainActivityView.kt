package eu.nets.miasample.activity


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
interface MainActivityView {

   /*
     * Initialize the views and listeners
     */
    fun initListeners()
    /**
     * Validates the inputted amount
     */
    fun validateAmount(): Boolean
/*

    */
/**
     * Launches the SDK with the paymentId and checkoutUrl
     *
     * @param paymentId the paymentId received in registerPayment API call
     * @param checkoutUrl the checkout page url sent in the register payment API call
     * @param returnUrl the return url of success case when Integration Type is Easy Hosted Checkout
     * @param cancelUrl the url that you would want to redirect to in case of cancel.
     */

    fun launchEasySDK(paymentId: String?, checkoutUrl: String?, returnUrl: String?, cancelUrl: String?)

/**
     * Show/hide progress view based on the boolean flag
     *
     * @param show boolean flag to show loader or to hide it
     */

    fun showLoader(show: Boolean)

/**
     * Retrieve the currency from the input field
     *
     * @return amount of the order
     */

    fun getAmount(): Long

/**
     * Retrieve the selected currency by the user
     *
     * @return currency code
     */

    fun getCurrency(): String

/**
     * Show an alert dialog with a custom title and message
     *
     * @param title the alert dialog title
     * @param message the alert dialog message
     */

    fun showAlert(title: String, message: String)

    /**
     * Show an alert dialog with a custom title and message for profile data screen
     */
    fun showProfileDataValidationDialog()

}