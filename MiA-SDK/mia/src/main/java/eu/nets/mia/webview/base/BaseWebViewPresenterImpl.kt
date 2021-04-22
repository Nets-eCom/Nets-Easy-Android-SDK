package eu.nets.mia.webview.base

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Message
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import eu.nets.mia.R
import eu.nets.mia.utils.HTMLInterceptor
import eu.nets.mia.utils.JSCallbackInterceptor
import eu.nets.mia.utils.JSPaymentCallback


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
open class BaseWebViewPresenterImpl(var mView: BaseWebView?) : BaseWebViewPresenter, JSPaymentCallback {

    companion object {
        val TAG: String = BaseWebViewPresenterImpl::class.java.simpleName
    }

    protected var parentWebView: WebView? = null
    protected var childWebViewList: ArrayList<WebView> = ArrayList()
    private var transactionCanceled: Boolean = false
    protected val pdfViewerUrl: String = "https://drive.google.com/viewerng/viewer?embedded=true&url="

    protected var parentWebViewClient = object : WebViewClient() {

        //NOTE: This method will be called for API 24 and above
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            return handleShouldOverrideUrlLoading(request?.url?.toString())
        }

        //NOTE: This method will be called for API 23 and below. THis was deprecated in API 24
        override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
            return handleShouldOverrideUrlLoading(url)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            mView?.showProgressView(true)
            enableBackNavigation(false)
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            mView?.showProgressView(false)
            view?.loadUrl("javascript:window.HTMLInterceptor.showHTML" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            notifyNavigationEnabled()
            handlePageFinished(url)
        }
    }

    protected var childWebViewClient = object : WebViewClient() {
        var urlToUpload: String? = null
        var urlToUploadFlag: Boolean? = false

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            mView?.showProgressView(true)
            enableBackNavigation(false)
            if (!url!!.contains(pdfViewerUrl) && url.endsWith(".pdf")) {
                urlToUpload = url
                urlToUploadFlag = true
            }

            if (urlToUpload != null && urlToUploadFlag ?: true) {
                val completeUrl = pdfViewerUrl + urlToUpload
                view?.loadUrl(completeUrl)
            }

            if (url == "about:blank") {
                urlToUploadFlag = false
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            mView?.showProgressView(false)
            view?.loadUrl("javascript:window.HTMLInterceptor.showHTML" +
                    "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');")
            view?.clearCache(true)
            view?.clearHistory()
            notifyNavigationEnabled()
        }
    }

    /**
     * Initialize the view and make startup configurations
     */
    override
    fun onCreate(savedInstanceState: Bundle?) {
        mView?.init(savedInstanceState)

        enableBackNavigation(false)
        enableForwardNavigation(false)
    }

    /**
     * Configure the useful settings for the WebView
     *
     * @param webView the WebView to be configured
     */
    override fun configureWebView(webView: WebView?) {

        parentWebView = applyWebViewSettings(webView)

        webView?.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val childWebView = createChildWebView() ?: return false
                (resultMsg?.obj as WebView.WebViewTransport).webView = childWebView
                resultMsg.sendToTarget()
                return true
            }
        }

        webView?.webViewClient = parentWebViewClient

        //on parent WebView show cancel icon
        handleNavBarActionIcon(R.drawable.mia_ic_close)
    }

    /**
     * In case of Easy Host Payment window integration type, do the following:
     * - check if the redirect happens to the url provided by the application
     * - check if the redirect url contains the actual payment id
     * - if so, stop the SDK process and deliver payment result OK
     *
     * In case of other integration types, do nothing!
     */
    override fun handleShouldOverrideUrlLoading(url: String?): Boolean {
        if (url == null) return false //don't interfere with the redirect

        val redirectUrl = mView?.getRedirectUrl()
        val cancelUrl = mView?.getCancelURL()
        if (redirectUrl != null && url.contains(redirectUrl)
                && url.endsWith(mView?.getPaymentIdentifier() ?: "")) {
            //is Easy Host Payment window integration type
            mView?.sendOKResult()
            return true
        } else if (cancelUrl != null && url.contains(cancelUrl)) {
            mView?.sendCancelResult()
            return true
        } else if (url.toLowerCase().contains("com.bankid.bus")) run {
            //check if user chose BankId option and open app
            mView?.openBankIdApp(null) //deeplink is null -- open the app using launcher intent
            return true
        } else if (url.toLowerCase().startsWith("bankid://")) run {
            mView?.openBankIdApp(url) //deeplink is valid -- use app switch with scheme url
            return true
        }

        return false //don't interfere with the redirect
    }

    /**
     * Creates a new WebView called "Child" to be loaded on top of the parent (pop-up)
     * It will have same functionalities as the parent, except the detection of the payment status.
     * Usually, this child will only show pages like: Help, Terms&Conditions, etc.
     *
     * @return WebView popup
     */
    override fun createChildWebView(): WebView? {
        if (mView?.getRootFrame() == null) return null

        val childWebView = applyWebViewSettings(WebView(mView as Context))

        childWebViewList.add(childWebView!!)
        mView?.getRootFrame()?.addView(childWebView)

        handleNavBarActionIcon(R.drawable.mia_ic_dismiss)

        childWebView.webChromeClient = object : WebChromeClient() {

            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val child: WebView = createChildWebView() ?: return false
                (resultMsg?.obj as WebView.WebViewTransport).webView = child
                resultMsg.sendToTarget()
                return true
            }

            override fun onCloseWindow(window: WebView?) {
                super.onCloseWindow(window)
                removeLatestWebView()
            }
        }

        childWebView.webViewClient = childWebViewClient

        notifyNavigationEnabled()

        return childWebView
    }

    /**
     * Apply required settings to the given WebView
     *
     * @param webView The WebView which will be modified to have required settings
     * @return WebView with specific settings
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun applyWebViewSettings(webView: WebView?): WebView? {
        webView?.settings?.builtInZoomControls = true
        webView?.settings?.domStorageEnabled = true
        webView?.settings?.javaScriptEnabled = true
        webView?.settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.settings?.setSupportMultipleWindows(true)
        webView?.settings?.useWideViewPort = true
        webView?.settings?.loadWithOverviewMode = true

        webView?.addJavascriptInterface(HTMLInterceptor(), "HTMLInterceptor")
        webView?.addJavascriptInterface(JSCallbackInterceptor(this), "JSCallbackInterceptor")

        return webView
    }

    /**
     * Handles hardware back button interactions
     */
    override fun onBackPressed() {
        removeLatestWebView()
    }

    /**
     * Navigate to previous loaded page in the visible WebView
     */
    override fun onNavigateBack() {
        if (getLatestWebView() != null) {
            if (getLatestWebView()?.canGoBack() == true) getLatestWebView()?.goBack()
        } else {
            if (parentWebView?.canGoBack() == true) parentWebView?.goBack()
        }
    }

    /**
     * Navigate to next loaded page in the visible WebView
     */
    override fun onNavigateForward() {
        if (getLatestWebView() != null) {
            if (getLatestWebView()?.canGoForward() == true) getLatestWebView()?.goForward()
        } else {
            if (parentWebView?.canGoForward() == true) parentWebView?.goForward()
        }
    }

    /**
     * Mark Forward button as enabled or disabled
     * @param enable Boolean flag
     */
    override fun enableForwardNavigation(enable: Boolean) {
        mView?.enableForwardNavigation(enable)
    }

    /**
     * Mark Back button as enabled or disabled
     * @param enable Boolean flag
     */
    override fun enableBackNavigation(enable: Boolean) {
        mView?.enableBackNavigation(enable)
    }

    /**
     * Returns the latest loaded WebView (the visible one)
     * @return WebView child or parent
     */
    override fun getLatestWebView(): WebView? {
        return childWebViewList.lastOrNull()
    }

    /**
     * Pauses the JavaScript functions in WebView that are running periodically
     */
    override fun pauseTimers() {
        parentWebView?.pauseTimers()
    }

    /**
     * Resumes the JavaScript functions in WebView that are running periodically
     */
    override fun resumeTimers() {
        parentWebView?.resumeTimers()
    }

    /**
     *  Send callbacks to activity to enable/disable both navigation arrows in the same time
     */
    override fun notifyNavigationEnabled() {
        enableBackNavigation(getLatestWebView()?.canGoBack() == true)
        enableForwardNavigation(getLatestWebView()?.canGoForward() == true)
    }

    /**
     * Removed the visible WebView from UI and from local stack
     */
    override fun removeLatestWebView() {
        val child = childWebViewList.lastOrNull()
        mView?.showProgressView(false)

        if (child != null) {
            mView?.getRootFrame()?.removeView(child)
            child.destroy()
            childWebViewList.remove(child)
        }

        if (childWebViewList.size == 0) handleNavBarActionIcon(R.drawable.mia_ic_close)
        notifyNavigationEnabled()
    }

    /**
     * Handles the case of Canceled or Not Authenticated user cases
     */
    override fun handlePageFinished(url: String?) {
        if (transactionCanceled) {
            mView?.sendCancelResult()
        }
        if (url?.contains("authenticated=false") == true) {
            transactionCanceled = true
        }
    }

    /**
     * Update the icon on the action button in the navigation bar (For parent WebView it will be Close,
     * and for child WebView it will be Dismiss)
     *
     * @param resId the resource id of the Drawable
     */
    override fun handleNavBarActionIcon(resId: Int) {
        mView?.handleNavBarActionText(resId)
    }

    /**
     * Update locally the recreated view instance and resume the WebView JavaScript functions
     * @param baseWebView recreated instance of the view
     */
    override fun onResume(baseWebView: BaseWebView?) {
        this.mView = baseWebView
        resumeTimers()
    }

    /**
     * Remove the cached instance of the view
     */
    override fun onStop() {
        mView = null
    }

    /**
     * Stop the JavaScript functions
     */
    override fun onPause() {
        pauseTimers()
    }

    /**
     * Callback method to notify the SDK that the payment is completed and successful
     */
    override fun onPaymentCompleted() {
        mView?.sendOKResult()
    }

}