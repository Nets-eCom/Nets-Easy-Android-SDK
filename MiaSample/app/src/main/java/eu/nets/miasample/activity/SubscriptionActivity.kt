package eu.nets.miasample.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.nets.mia.MiASDK
import eu.nets.mia.data.MiAPaymentInfo
import eu.nets.mia.data.MiAResult
import eu.nets.mia.data.MiAResultCode
import eu.nets.miasample.R
import eu.nets.miasample.adapter.SubscriptionAdapter
import eu.nets.miasample.network.response.SubscriptionDetailsResponse
import eu.nets.miasample.utils.SampleLocalHost
import eu.nets.miasample.utils.SharedPrefs
import kotlinx.android.synthetic.main.activity_subscription.*

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

class SubscriptionActivity : AppCompatActivity(), SubscriptionActivityView {


    private lateinit var mPresenter: SubscriptionActivityPresenterImpl
    lateinit var subscriptionDetailsList: MutableList<SubscriptionDetailsResponse>
    lateinit var subscriptionAdapter: SubscriptionAdapter
    var chargeableAmount: Long = 0
    var chargeableCurency: String = "SEK"
    var createSubscription: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)
        chargeableAmount = intent.getLongExtra("ChargeableAmount", 0)
        createSubscription = intent.getBooleanExtra("CreateSubscription", false)
        if (intent.hasExtra("ChargeableCurrency"))
            chargeableCurency = intent.getStringExtra("ChargeableCurrency")
        mPresenter = SubscriptionActivityPresenterImpl(this)
        mPresenter.init()
        setupRecycler()
        if (createSubscription)
            createSubscription()
    }

    private fun setupRecycler() {
        var subscriptionDetails = SharedPrefs.getInstance().subscriptionData
        if (subscriptionDetails == "") {
            subscriptionDetailsList = mutableListOf()
        } else {
            try {
                val modalType = object : TypeToken<List<SubscriptionDetailsResponse>>() {}.type
                subscriptionDetailsList = Gson().fromJson<MutableList<SubscriptionDetailsResponse>>(subscriptionDetails, modalType)
            } catch (e: Exception) {
                subscriptionDetailsList = mutableListOf()
            }
        }
        val isEmptyList = (subscriptionDetailsList.size == 0)
        recycler_view.visibility = if (isEmptyList) View.GONE else View.VISIBLE
        tv_no_subs.visibility = if (isEmptyList) View.VISIBLE else View.GONE

        recycler_view.layoutManager = LinearLayoutManager(this)
        subscriptionAdapter = SubscriptionAdapter(subscriptionDetailsList, this)
        recycler_view.adapter = subscriptionAdapter
    }

    //activity lifecycle
    /**
     * Activity onResume()
     * Call presenter onResume to restore the new instance of the view
     */
    override fun onResume() {
        super.onResume()
        mPresenter.onResume(this)
    }

    /**
     * Activity onDestroy()
     * Call presenter onDestroy to clear all cached data
     */
    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }


    /**
     * Retrieve the SDK result and handle the payment flow
     * 1. If the transaction was canceled by the user: a popup is shown with canceled message
     * 2. If the transaction was finished with error: a popup is shown with error message
     * 3. If the transaction was finished with success: call /charges API to charge the reserved amount;
     *      Display the result of the charge call into the popup
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == MiASDK.EASY_SDK_REQUEST_CODE) {

            //SDK was closed, so close the socket server
            SampleLocalHost.getInstance().closeServer()

            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getParcelableExtra<MiAResult>(MiASDK.BUNDLE_COMPLETE_RESULT)

                when (result?.miaResultCode) {
                    //user completed the payment
                    MiAResultCode.RESULT_PAYMENT_COMPLETED -> {
                        mPresenter.getSubscriptionId()
                    }
                    //user has cancelled the payment
                    MiAResultCode.RESULT_PAYMENT_CANCELLED -> {
                        mPresenter.getSubscriptionId()
                    }

                    //user encountered and error and cannot proceed with the payment
                    MiAResultCode.RESULT_PAYMENT_FAILED -> {
                        showAlert(getString(R.string.error_title), result.miaError?.getErrorMessage()
                                ?: getString(R.string.error_message))
                    }
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun initListeners() {
        add_subscription.setOnClickListener {
            createSubscription()
        }
    }

    override fun launchEasySDK(paymentId: String?, checkoutUrl: String?, returnUrl: String?, cancelUrl: String?) {
        if (paymentId == null || checkoutUrl == null) {
            showAlert(getString(R.string.error_title), getString(R.string.error_message))
            return
        } else {
            MiASDK.getInstance().startSDK(this, MiAPaymentInfo(paymentId, checkoutUrl, returnUrl, cancelUrl))
        }
    }

    override fun showLoader(show: Boolean) {
        rl_progressView.visibility = if (show) View.VISIBLE else View.GONE
        if (show) rl_progressView.bringToFront()
    }

    override fun getAmount(): Long {
        return chargeableAmount
    }

    override fun getCurrency(): String {
        return SharedPrefs.getInstance().currency
    }

    override fun showAlert(title: String, message: String) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton(getString(R.string.action_ok)) { p0, _ -> p0?.dismiss() }
            builder.create().show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun updateList(subscriptionDetailsResponse: SubscriptionDetailsResponse) {
        recycler_view.visibility = View.VISIBLE
        tv_no_subs.visibility = View.GONE
        subscriptionDetailsList.add(0, subscriptionDetailsResponse)
        subscriptionAdapter.updateList(subscriptionDetailsList)
    }

    override fun chargeSubscription(subscriptionId: String, isChargeable: Boolean) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)

            if (isChargeable) {
                builder.setTitle(getString(R.string.charge_subscription))
                var amount = getAmount().toFloat() / 100
                builder.setMessage(getString(R.string.charge_subscription_alert, amount, chargeableCurency))
                builder.setPositiveButton(getString(R.string.action_ok)) { p0, _ ->
                    run {
                        mPresenter.chargeSubscription(subscriptionId)
                        p0.dismiss()
                    }
                }
                builder.setNegativeButton(getString(R.string.cancel)) { p0, _ -> p0.dismiss() }
            } else {
                builder.setTitle(getString(R.string.charge_disabled))
                builder.setMessage(getString(R.string.enable_charge))
                builder.setPositiveButton(getString(R.string.action_ok)) { p0, _ -> p0.dismiss() }
            }

            builder.create().show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createSubscription() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.add_subscription))
            var amount = getAmount().toFloat() / 100
            builder.setMessage(getString(R.string.create_subscription_alert, amount, chargeableCurency))
            builder.setPositiveButton(getString(R.string.add)) { p0, _ ->
                run {
                    mPresenter.launchSDK()
                    p0.dismiss()
                }
            }
            builder.setNegativeButton(getString(R.string.cancel)) { p0, _ -> p0.dismiss() }
            builder.create().show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
