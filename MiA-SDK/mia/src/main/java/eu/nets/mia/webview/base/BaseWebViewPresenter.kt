package eu.nets.mia.webview.base

import android.os.Bundle
import android.webkit.WebView


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
interface BaseWebViewPresenter {

    /**
     * Initialize the view and make startup configurations
     */
    fun onCreate(savedInstanceState: Bundle?)

    /**
     * Configure the useful settings for the WebView
     *
     * @param webView the WebView to be configured
     */
    fun configureWebView(webView: WebView?)

    /**
     * In case of Easy Host Payment window integration type, do the following:
     * - check if the redirect happens to the url provided by the application
     * - check if the redirect url contains the actual payment id
     * - if so, stop the SDK process and deliver payment result OK
     *
     * In case of other integration types, do nothing!
     */
    fun handleShouldOverrideUrlLoading(url: String?) : Boolean

    /**
     * Creates a new WebView called "Child" to be loaded on top of the parent (pop-up)
     * It will have same functionalities as the parent, except the detection of the payment status.
     * Usually, this child will only show pages like: Help, Terms&Conditions, etc.
     *
     * @return WebView popup
     */
    fun createChildWebView(): WebView?

    /**
     * Apply required settings to the given WebView
     *
     * @param webView The WebView which will be modified to have required settings
     * @return WebView with specific settings
     */
    fun applyWebViewSettings(webView: WebView?): WebView?

    /**
     * Handles hardware back button interactions
     */
    fun onBackPressed()

    /**
     * Navigate to previous loaded page in the visible WebView
     */
    fun onNavigateBack()

    /**
     * Navigate to next loaded page in the visible WebView
     */
    fun onNavigateForward()

    /**
     * Mark Forward button as enabled or disabled
     * @param enable Boolean flag
     */
    fun enableForwardNavigation(enable: Boolean)

    /**
     * Mark Back button as enabled or disabled
     * @param enable Boolean flag
     */
    fun enableBackNavigation(enable: Boolean)

    /**
     * Returns the latest loaded WebView (the visible one)
     * @return WebView child or parent
     */
    fun getLatestWebView(): WebView?

    /**
     * Pauses the JavaScript functions in WebView that are running periodically
     */
    fun pauseTimers()

    /**
     * Resumes the JavaScript functions in WebView that are running periodically
     */
    fun resumeTimers()

    /**
     *  Send callbacks to activity to enable/disable both navigation arrows in the same time
     */
    fun notifyNavigationEnabled()

    /**
     * Removed the visible WebView from UI and from local stack
     */
    fun removeLatestWebView()

    /**
     * Handles the case of Canceled or Not Authenticated user cases
     */
    fun handlePageFinished(url: String?)

    /**
     * Update the icon on the action button in the navigation bar (For parent WebView it will be Close,
     * and for child WebView it will be Dismiss)
     *
     * @param resId the resource id of the Drawable
     */
    fun handleNavBarActionIcon(resId: Int)

    /**
     * Update locally the recreated view instance and resume the WebView JavaScript functions
     * @param baseWebView recreated instance of the view
     */
    fun onResume(baseWebView: BaseWebView?)

    /**
     * Remove the cached instance of the view
     */
    fun onStop()

    /**
     * Stop the JavaScript functions
     */
    fun onPause()
}