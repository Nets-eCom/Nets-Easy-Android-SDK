package eu.nets.mia.webview.base

import android.os.Bundle
import android.widget.FrameLayout


/**
 *****Copyright (c) 2020 Nets Denmark A/S*****
 *
 * NETS DENMARK A/S, ("NETS"), FOR AND ON BEHALF OF ITSELF AND ITS SUBSIDIARIES AND AFFILIATES UNDER COMMON CONTROL,
 * IS WILLING TO LICENSE THE SOFTWARE TO YOU ONLY UPON THE CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED
 * IN THIS LICENSE AGREEMENT.
 * BY USING THE SOFTWARE YOU ACKNOWLEDGE THAT YOU HAVE READ THE TERMS AND AGREE TO THEM.
 * IF YOU ARE AGREEING TO THESE TERMS ON BEHALF OF A COMPANY OR OTHER LEGAL ENTITY,
 * YOU REPRESENT THAT YOU HAVE THE LEGAL AUTHORITY TO BIND THE LEGAL ENTITY TO THESE TERMS. IF YOU DO NOT HAVE SUCH AUTHORITY,
 * OR IF YOU DO NOT WISH TO BE BOUND BY THE TERMS, YOU MUST NOT USE THE SOFTWARE ON THIS SITE OR ANY OTHER MEDIA ON WHICH THE SOFTWARE IS CONTAINED.
 *
 * Software is copyrighted. Title to Software and all associated intellectual property rights is retained by NETS and/or its licensors.
 * Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse engineer Software.
 *
 * No right, title or interest in or to any trademark, service mark, logo or trade name of NETS or its licensors is granted under this Agreement.
 *
 * Permission is hereby granted, to any person obtaining a copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * Software may only be used for commercial or production purpose together with
 * Easy services (as per https://tech.dibspayment.com/easy) provided from NETS, its subsidiaries or affiliates under common control.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
interface BaseWebView {

    /**
     * Initialize the UI listeners and setup the WebView configuration
     */
    fun init(bundle: Bundle?)

    /**
     * Close the current activity and deliver success result to application
     */
    fun sendOKResult()

    /**
     * Close the current activity and deliver canceled result to application
     */
    fun sendCancelResult()

    /**
     * Get the root view of the WebView to load multiple WebViews as child pop-ups
     */
    fun getRootFrame(): FrameLayout

    /**
     * Enable/disable the forward button navigation
     *
     * @param enable flag to specify is button is enabled or disabled
     */
    fun enableForwardNavigation(enable: Boolean)

    /**
     * Enable/disable the back button navigation
     *
     * @param enable flag to specify is button is enabled or disabled
     */
    fun enableBackNavigation(enable: Boolean)

    /**
     * Load the received URL in the parent WebView
     *
     * @param url the checkout URL provided by the merchant
     * @param paymentId the paymentId provided by the merchant
     */
    fun loadUrl(url: String, paymentId: String)

    /**
     * Update the icon on the action button in the navigation bar (For parent WebView it will be Close,
     * and for child WebView it will be Dismiss)
     *
     * @param resId the resource id of the Drawable
     */
    fun handleNavBarActionText(resId: Int)

    /**
     * Returns the checkout URL provided by merchant app
     * @return URL String
     */
    fun getCheckoutPage(): String

    /**
     * Returns the payment identifier provided by merchant app
     * @return paymentId String
     */
    fun getPaymentIdentifier(): String

    /**
     * Show/hide the loader based on WebView callbacks
     */
    fun showProgressView(show: Boolean)

    /**
     * Returns the return URL received from the merchant app
     * @return returnUrl String
     */
    fun getRedirectUrl(): String?

    /**
     * Returns the cancel URL received from the merchant app
     * @return cancelUrl String
     */
    fun getCancelURL(): String?

    /**
     * Case when customer's credit card supports BankId
     *
     * @param deepLink bankId deeplink URL for app switch;
     * (if it's null, will launch the app using launcher intent; of not, will launch it using scheme url)
     */
    fun openBankIdApp(deepLink: String?)
}