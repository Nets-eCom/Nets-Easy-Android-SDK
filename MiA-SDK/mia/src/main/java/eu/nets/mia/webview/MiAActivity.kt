package eu.nets.mia.webview

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AlertDialog
import eu.nets.mia.MiASDK
import eu.nets.mia.R
import eu.nets.mia.data.MiAError
import eu.nets.mia.data.MiAPaymentInfo
import eu.nets.mia.data.MiAResult
import eu.nets.mia.data.MiAResultCode
import eu.nets.mia.webview.base.BaseWebViewActivity


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
class MiAActivity : BaseWebViewActivity(), MiAActivityView {

    //bundle keys
    companion object {
        const val BUNDLE_PAYMENT_INFO = "BUNDLE_PAYMENT_INFO"
    }
    //end

    private lateinit var mPresenter: MiAActivityPresenter
    private var miAPaymentInfo: MiAPaymentInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mPresenter = MiAActivityPresenterImpl(this)
        mPresenter.init(intent?.extras ?: savedInstanceState)
    }

    //activity lifecycle
    /**
     * Activity onResume()
     * Call presenter onResume to store new state of view
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
     * Store the variables in activity state so it can be retrieved later
     */
    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        outState?.putParcelable(BUNDLE_PAYMENT_INFO, miAPaymentInfo)
        super.onSaveInstanceState(outState, outPersistentState)
    }
    //end

    //easy activity view interface
    /**
     * Initialize the data received from the merchant app
     * @param bundle Can be the Intent bundle data (from the merchant app directly), or
     *              savedInstanceState bundle data (SDK stored in case the app goes to background)
     */
    override fun initData(bundle: Bundle?) {
        miAPaymentInfo = bundle?.getParcelable(BUNDLE_PAYMENT_INFO)
    }

    /**
     * Validates again the paymentId and checkoutUrl
     *  - if are valid, it will notify the BaseActivity to load the specific URL
     *  - if not valid, cancel the process with error - something went wrong
     *
     *  @param paymentId the ID of the payment provided by merchant app
     *  @param checkoutUrl the checkout url provided by merchant app
     */
    override fun loadWebViewData(paymentId: String?, checkoutUrl: String?) {
        if (paymentId == null || checkoutUrl == null) {
            //cannot continue if one of these are null -- SDK Internal error
            setResult(Activity.RESULT_OK, getResultIntent(MiAResult(MiAError.MiASDKError)))
            finish()
            return
        }
        loadUrl(checkoutUrl, paymentId)
    }

    /**
     * Hide/show the ProgressView based on the flag
     * @param show Boolean flag
     */
    override fun showLoader(show: Boolean) {
        showProgressView(show)
    }

    /**
     * Returns the checkout URL received from the merchant app
     * @return URL String
     */
    override fun getCheckoutUrl(): String? {
        return miAPaymentInfo?.checkoutUrl
    }

    /**
     * Returns the paymentId received from the merchant app
     * @return paymentId String
     */
    override fun getPaymentId(): String? {
        return miAPaymentInfo?.paymentId
    }
    //base activity abstract methods

    /**
     * Returns the return URL received from the merchant app
     * @return returnUrl String
     */
    override fun getReturnUrl(): String? {
        return miAPaymentInfo?.returnUrl
    }

    /**
     * Returns the cancel URL received from the merchant app
     * @return cancelUrl String
     */
    override fun getCancelUrl(): String? {
        return miAPaymentInfo?.cancelUrl
    }

    /**
     * BaseWebViewActivity callback
     * Notifies this activity that the payment has finished with success
     * Handle here the way the result gets delivered to the application
     */
    override fun onProcessFinishedSuccess() {
        setResult(Activity.RESULT_OK, getResultIntent(MiAResult(MiAResultCode.RESULT_PAYMENT_COMPLETED)))
        finish()
    }

    /**
     * BaseWebViewActivity callback
     * Notifies this activity that the payment was canceled
     * Handle here the way the result gets delivered to the application
     */
    override fun onProcessCanceled() {
        setResult(Activity.RESULT_OK, getResultIntent(MiAResult(MiAResultCode.RESULT_PAYMENT_CANCELLED)))
        finish()
    }
    //end

    //helpers
    /**
     * Helper method to build the result intent with data
     */
    private fun getResultIntent(miaResult: MiAResult): Intent {
        val intent = Intent()
        intent.putExtra(MiASDK.BUNDLE_COMPLETE_RESULT, miaResult)
        return intent
    }
    //

    override fun openBankIdApp(deepLink: String?) {
        var intent = packageManager.getLaunchIntentForPackage("com.bankid.bus")
        if (intent != null) {
            // BankId app is found -- launch it

            if (deepLink == null) {
                //is nordea visa case -- launch app directly using launcher intent
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
                //is mastercard ID check -- launch app using deeplink
                val bankIdIntent = Intent(Intent.ACTION_VIEW)
                bankIdIntent.setPackage("com.bankid.bus")
                bankIdIntent.data = Uri.parse(deepLink)
                startActivity(bankIdIntent)
            }
        } else {
            // BankId app is not found on user's device -- open Market to download it
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("market://details?id=" + "com.bankid.bus")
            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                //cannot open market -- show error toast
                showBankIdErrorAlert()
            }

        }
    }

    private fun showBankIdErrorAlert() {

        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.mia_error_title))
            builder.setMessage(getString(R.string.mia_bank_id_error_open))
            builder.setCancelable(false)

            builder.setPositiveButton(
                    getString(R.string.mia_alert_ok_action),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })

            val dialog = builder.create()
            dialog.show()
            //apply font to dialog after the dialog.show() has been called to avoid NullPointerException
        } catch (e: Exception) {
            //in case activity is not attached to window -- catch exception here and do nothing
        }

    }

    interface AlertCallback {
        fun onPositiveAction()

        fun onNegativeAction()
    }
}