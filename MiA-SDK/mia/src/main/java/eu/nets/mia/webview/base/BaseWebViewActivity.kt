package eu.nets.mia.webview.base

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import eu.nets.mia.BuildConfig
import eu.nets.mia.R
import kotlinx.android.synthetic.main.activity_base_webview.*

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
abstract class BaseWebViewActivity : AppCompatActivity(), BaseWebView {

    abstract fun onProcessFinishedSuccess()
    abstract fun onProcessCanceled()
    abstract fun getReturnUrl(): String?
    abstract fun getCancelUrl(): String?

    private lateinit var mPresenter: BaseWebViewPresenter
    private lateinit var checkoutPage: String
    private lateinit var paymentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //disable screenshot
        if (BuildConfig.DEBUG) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        }

        setContentView(R.layout.activity_base_webview)

        mPresenter = BaseWebViewPresenterImpl(this)
        mPresenter.onCreate(savedInstanceState)
    }

    //activity lifecycle
    /**
     * Activity onResume()
     * Call presenter onResume to store new state of view and to resume the WebView timers
     */
    override fun onResume() {
        super.onResume()
        mPresenter.onResume(this)
    }

    /**
     * Activity onStop()
     * Call presenter onStop to clear the state of view
     */
    override fun onStop() {
        super.onStop()
        mPresenter.onStop()
    }

    /**
     * Activity onPause()
     * Call presenter onPause to pause the WebView timers
     */
    override fun onPause() {
        super.onPause()
        mPresenter.onPause()
    }

    /**
     * Activity onBackPressed()
     * Check if there are opened WebView pop-ups to be closed. If not, navigate back
     */
    override fun onBackPressed() {
        if (mPresenter.getLatestWebView() != null) {
            mPresenter.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }
    //end

    //base web view interface
    /**
     * Initialize the UI listeners and setup the WebView configuration
     */
    override fun init(bundle: Bundle?) {
        //set listeners
        actionBack.setOnClickListener { mPresenter.onNavigateBack() }
        actionForward.setOnClickListener { mPresenter.onNavigateForward() }
        actionClose.setOnClickListener {
            if (mPresenter.getLatestWebView() != null) mPresenter.onBackPressed() else sendCancelResult()
        }

        mPresenter.configureWebView(parentWebView)
    }

    /**
     * Close the current activity and deliver success result to application
     */
    override fun sendOKResult() {
        onProcessFinishedSuccess()
    }

    /**
     * Close the current activity and deliver canceled result to application
     */
    override fun sendCancelResult() {
        onProcessCanceled()
    }

    /**
     * Get the root view of the WebView to load multiple WebViews as child pop-ups
     */
    override fun getRootFrame(): FrameLayout {
        return baseFrameLayout
    }

    /**
     * Enable/disable the forward button navigation
     *
     * @param enable flag to specify is button is enabled or disabled
     */
    override fun enableForwardNavigation(enable: Boolean) {
        actionForward.isEnabled = enable
        actionForward.alpha = if (enable) 1f else 0.3f
    }

    /**
     * Enable/disable the back button navigation
     *
     * @param enable flag to specify is button is enabled or disabled
     */
    override fun enableBackNavigation(enable: Boolean) {
        actionBack.isEnabled = enable
        actionBack.alpha = if (enable) 1f else 0.3f
    }

    /**
     * Load the received URL in the parent WebView
     *
     * @param url the checkout URL provided by the merchant
     * @param paymentId the paymentId provided by the merchant
     */
    override fun loadUrl(url: String, paymentId: String) {
        this.checkoutPage = url
        this.paymentId = paymentId
        parentWebView.loadUrl(url)
    }

    /**
     * Update the icon on the action button in the navigation bar (For parent WebView it will be Close,
     * and for child WebView it will be Dismiss)
     *
     * @param resId the resource id of the Drawable
     */
    override fun handleNavBarActionText(resId: Int) {
        val actionDrawable: Drawable? = ContextCompat.getDrawable(this, resId)
        actionDrawable?.setColorFilter(ContextCompat.getColor(this, R.color.miaColorPrimary), PorterDuff.Mode.SRC_IN)
        actionClose.setImageDrawable(actionDrawable)
    }

    /**
     * Returns the checkout URL provided by merchant app
     * @return URL String
     */
    override fun getCheckoutPage(): String {
        return checkoutPage
    }

    /**
     * Returns the payment identifier provided by merchant app
     * @return paymentId String
     */
    override fun getPaymentIdentifier(): String {
        return paymentId
    }

    /**
     * Show/hide the loader based on WebView callbacks
     */
    override fun showProgressView(show: Boolean) {
        progressView.visibility = if (show) View.VISIBLE else View.GONE
        parentWebView.setOnTouchListener { _, _ -> show }
    }

    /**
     * Returns the return URL received from the merchant app
     * @return returnUrl String
     */
    override fun getRedirectUrl(): String? {
        return getReturnUrl()
    }

    /**
     * Returns the cancel URL received from the merchant app
     * @return cancelUrl String
     */
    override fun getCancelURL(): String? {
        return getCancelUrl()
    }

    //end
}
