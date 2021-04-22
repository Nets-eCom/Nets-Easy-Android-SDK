package eu.nets.mia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import eu.nets.mia.data.MiAPaymentInfo
import eu.nets.mia.webview.MiAActivity


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
open class MiASDK {

    companion object {
        /**
         * Request code used when starting the SDK as #startActivityForResult(). Check for this code on your *onActivityResult()
         */
        const val EASY_SDK_REQUEST_CODE = 1001
        /**
         * Parcelable Extra - [eu.nets.mia.data.MiAResult] will be found in the Intent Data under this key.
         *
         */
        const val BUNDLE_COMPLETE_RESULT: String = "BUNDLE_COMPLETE_RESULT"

        private val instance: MiASDK = MiASDK()

        /**
         * Returns Singleton instance if MiaSDK class
         */
        @JvmStatic
        fun getInstance(): MiASDK {
            return instance
        }
    }

    /**
     * Starts the SDK from an Activity, with startActivityForResult() with the requestCode = EASY_SDK_REQUEST_CODE
     *
     * @param activity The activity from where the SDK is started
     * @param miAPaymentInfo The payment information (paymentId and checkoutUrl) provided by merchant application
     */
    fun startSDK(activity: Activity, miAPaymentInfo: MiAPaymentInfo) {
        validateFields(miAPaymentInfo)

        val intent = Intent(activity, MiAActivity::class.java)
        intent.putExtras(createBundle(miAPaymentInfo))

        activity.startActivityForResult(intent, EASY_SDK_REQUEST_CODE)
    }

    /**
     * Starts the SDK from an Fragment, with startActivityForResult() with the requestCode = EASY_SDK_REQUEST_CODE
     *
     * @param fragment The fragment from where the SDK is started
     * @param miAPaymentInfo The payment information (paymentId and checkoutUrl) provided by merchant application
     */
    fun startSDK(fragment: Fragment, miAPaymentInfo: MiAPaymentInfo) {
        validateFields(miAPaymentInfo)

        val intent = Intent(fragment.activity, MiAActivity::class.java)
        intent.putExtras(createBundle(miAPaymentInfo))

        fragment.startActivityForResult(intent, EASY_SDK_REQUEST_CODE)
    }

    /**
     * It will validate the parameters received form merchant application
     * Will throw IllegalArgumentException if the conditions are not met
     */
    private fun validateFields(miAPaymentInfo: MiAPaymentInfo?) {
        //check made for java code for nullable values
        if (miAPaymentInfo == null) throw IllegalArgumentException("MiAPaymentInfo must not be null.")
        //check for localhost url
        if (!miAPaymentInfo.checkoutUrl.contains("http://") && !miAPaymentInfo.checkoutUrl.contains("https://")) {
            throw IllegalArgumentException("Checkout URL is not valid.")
        }
    }

    /**
     * Helper function to create the bundle to be sent in the target activity
     */
    private fun createBundle(miAPaymentInfo: MiAPaymentInfo): Bundle {
        val bundle = Bundle()
        bundle.putParcelable(MiAActivity.BUNDLE_PAYMENT_INFO, miAPaymentInfo)

        return bundle
    }

    /**
     * Formats the technical version of the SDK in a String. It is composed of:
     * - the branch name from where the build was made
     * - branch revision id
     * - branch hash key
     *
     * @return Technical version String
     */
    fun getTechnicalVersion(): String {
        return "${BuildConfig.TECHNICAL_VERSION_INFO_BRANCH}.${BuildConfig.TECHNICAL_VERSION_INFO_HASH}.${BuildConfig.TECHNICAL_VERSION_INFO_ID}"
    }

    /**
     * Get the version name of the SDK
     *
     * @return SDK version name
     */
    fun getVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    /**
     * Get the version code of the SDK
     *
     * @return SDK version code
     */
    fun getVersionCode(): String {
        return BuildConfig.VERSION_CODE.toString()
    }
}