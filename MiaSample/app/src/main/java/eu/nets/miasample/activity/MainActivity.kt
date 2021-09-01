package eu.nets.miasample.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import eu.nets.mia.MiASDK
import eu.nets.mia.data.MiAPaymentInfo
import eu.nets.mia.data.MiAResult
import eu.nets.mia.data.MiAResultCode
import eu.nets.miasample.R
import eu.nets.miasample.adapter.CurrencyAdapter
import eu.nets.miasample.adapter.IntegrationTypeAdapter
import eu.nets.miasample.network.APIManager
import eu.nets.miasample.utils.SharedPrefs
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*
import eu.nets.miasample.utils.SampleLocalHost
import kotlinx.android.synthetic.main.profile_data_validation_layout.view.*

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
class MainActivity : AppCompatActivity(), MainActivityView {

    private lateinit var mPresenter: MainActivityPresenter
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private var launchInputActivity = false
    private var profileSelected = false

    companion object {
        //currency codes
        const val CURRENCY_SEK = "SEK"
        const val CURRENCY_DKK = "DKK"
        const val CURRENCY_NOK = "NOK"
        const val CURRENCY_EUR = "EUR"

        //end
        //integration types
        const val EASY_HOSTED_PAYMENT_WINDOW = "HostedPaymentPage"
        const val MERCHANT_HOSTED_PAYMENT_WINDOW = "EmbeddedCheckout"

        //end
        //easy hosted payment window helper constants
        const val RETURN_URL = "http://localhost/redirect.php"

        // Cancellation URL passed to EASY and the SDK to indentify
        // user cancellation by using the "Go back" link rendered
        // in the checkout webview.
        // Note: Pass the same `cancelURL` for
        // payment registration with Easy API and
        // when presenting Mia SDK following payment registration.
        const val CANCEL_URL = "https://cancellation-identifier-url"
        const val INTEGRATION_TYPE_PARAM = "HostedPaymentPage"

        const val CONSUMER_DATA_NONE = "None"
        const val CONSUMER_DATA_MERCHANT_INJECTED = "Injected by merchant"
        const val CONSUMER_DATA_NO_SHIPPING_ADDR = "No shipping address"

        //end
        val SCREEN_SELECTED = "screen_selected"
        val SCREEN_PROFILE = "screen_profile"
        val SCREEN_SECRET_KEY = "screen_secret_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        mPresenter = MainActivityPresenterImpl(this)

        mPresenter.init()
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
                        mPresenter.getPayment()
                    }
                    //user has cancelled the payment
                    MiAResultCode.RESULT_PAYMENT_CANCELLED -> {
                        mPresenter.getPayment()
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
    //end

    //main activity view interface
    /**
     * Initialize the views and listeners
     */
    override fun initListeners() {
        val currencies: List<String> = ArrayList<String>(Arrays.asList(
                CURRENCY_SEK,
                CURRENCY_DKK,
                CURRENCY_NOK,
                CURRENCY_EUR
        ))
        val spinnerAdapter = CurrencyAdapter(this, android.R.layout.simple_spinner_item, currencies)

        currencySpinner.adapter = spinnerAdapter
        currencySpinner.setSelection(spinnerAdapter.getPositionForItem(SharedPrefs.getInstance().currency))
        currencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //not required
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SharedPrefs.getInstance().currency = spinnerAdapter.getItem(p2)
            }
        }

        //integration type spinner
        val integrationTypes: List<String> = ArrayList<String>(Arrays.asList(
                EASY_HOSTED_PAYMENT_WINDOW,
                MERCHANT_HOSTED_PAYMENT_WINDOW
        ))
        val integrationTypeAdapter = IntegrationTypeAdapter(this, android.R.layout.simple_spinner_item, integrationTypes)

        integrationTypeSpinner.adapter = integrationTypeAdapter
        integrationTypeSpinner.setSelection(integrationTypeAdapter.getPositionForItem(SharedPrefs.getInstance().integrationType))
        integrationTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //not required
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SharedPrefs.getInstance().integrationType = integrationTypeAdapter.getItem(p2)
            }
        }

        val consumerData: List<String> = ArrayList<String>(Arrays.asList(
                CONSUMER_DATA_NONE,
                CONSUMER_DATA_MERCHANT_INJECTED,
                CONSUMER_DATA_NO_SHIPPING_ADDR
        ))
        val consumerDataAdapter = IntegrationTypeAdapter(this, android.R.layout.simple_spinner_item, consumerData)

        consumerDataSpinner.adapter = consumerDataAdapter
        consumerDataSpinner.setSelection(consumerDataAdapter.getPositionForItem(SharedPrefs.getInstance().integrationType))
        consumerDataSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //not required
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                SharedPrefs.getInstance().consumerData = consumerDataAdapter.getItem(p2)
            }
        }

        btnBuy.setOnClickListener {
            if (mPresenter.validateProfileData()) {
                mPresenter.launchSDK()
            }
        }

        btnSubscribe.setOnClickListener {
            openSubscriptionsView(true)
        }

        subscriptionView.setOnClickListener {
            openSubscriptionsView(false)
        }

        //init drawer menu
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        clearCache.setOnClickListener {
            try {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.clear_cache_cookies))
                builder.setMessage(getString(R.string.clear_cookies_description))
                builder.setPositiveButton(getString(R.string.action_settings)) { p0, _ ->
                    p0?.dismiss()
                    //redirect the user to app settings
                    val myAppSettings = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                    startActivity(myAppSettings)
                }
                builder.setNegativeButton(getString(R.string.cancel)) { p0, _ -> p0?.dismiss() }
                builder.create().show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        drawerToggle = object : ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.action_open, R.string.action_close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                //push root view to the right when drawer is opening
                rootView.translationX = slideOffset * drawerView.width
                drawerLayout.bringChildToFront(drawerView)
                drawerLayout.requestLayout()
                //remove the drawer shadow
                drawerLayout.setScrimColor(Color.TRANSPARENT)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                //drawer is closed; check if the flag notifies that activity should be launched
                if (launchInputActivity) {
                    val intent = Intent(this@MainActivity, InputKeysActivity::class.java)
                    val bundle = Bundle()
                    if (profileSelected) {
                        bundle.putString(SCREEN_SELECTED, SCREEN_PROFILE)
                    } else {
                        bundle.putString(SCREEN_SELECTED, SCREEN_SECRET_KEY)
                    }
                    intent.putExtras(bundle)
                    startActivity(intent)
                    launchInputActivity = false
                    profileSelected = false
                }
            }
        }

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerToggle.syncState()
        drawerToggle.drawerArrowDrawable.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

        //set old user configuration visible
        switchProdEnv.isChecked = !SharedPrefs.getInstance().testMode
        switchChargePayment.isChecked = SharedPrefs.getInstance().chargePayment

        //setup nav drawer items listeners
        val versionText = "${MiASDK.getInstance().getVersionName()} (${MiASDK.getInstance().getTechnicalVersion()})"
        val span = SpannableString(versionText)
        //apply BOLD span on the version name
        span.setSpan(StyleSpan(Typeface.BOLD), 0, MiASDK.getInstance().getVersionName().length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        fieldVersion.text = span

        switchProdEnv.setOnCheckedChangeListener { _, checked ->
            SharedPrefs.getInstance().testMode = !checked
            APIManager.recreateInstance()
        }

        switchChargePayment.setOnCheckedChangeListener { _, checked ->
            SharedPrefs.getInstance().chargePayment = checked
        }

        changeKeys.setOnClickListener {
            //close drawer first
            drawerLayout.closeDrawer(Gravity.START)
            //make flag true; activity will be launched after the drawer is closed
            launchInputActivity = true
        }
        editProfile.setOnClickListener {
            profileSelected = true
            //close drawer first
            drawerLayout.closeDrawer(Gravity.START)
            //make flag true; activity will be launched after the drawer is closed
            launchInputActivity = true
        }
    }

    /**
     * Validates the inputted amount
     */
    override fun validateAmount(): Boolean {
        if (getAmount() == 0L) {
            showAlert(getString(R.string.error_title), getString(R.string.amount_error))
            return false
        }
        return true
    }

    /**
     * Launches the SDK with the paymentId and checkoutUrl
     *
     * @param paymentId the paymentId received in registerPayment API call
     * @param checkoutUrl the checkout page url sent in the register payment API call
     * @param returnUrl the return url of success case when Integration Type is Easy Hosted Checkout
     * @param cancelUrl the url that you would want to redirect to in case of cancel.
     */
    override fun launchEasySDK(paymentId: String?, checkoutUrl: String?, returnUrl: String?, cancelUrl: String?) {
        if (paymentId == null || checkoutUrl == null) {
            showAlert(getString(R.string.error_title), getString(R.string.error_message))
            return
        } else if (validateAmount()) {
            MiASDK.getInstance().startSDK(this, MiAPaymentInfo(paymentId, checkoutUrl, returnUrl, cancelUrl))
        }
    }

    /**
     * Show/hide progress view based on the boolean flag
     *
     * @param show boolean flag to show loader or to hide it
     */
    override fun showLoader(show: Boolean) {
        progressView.visibility = if (show) View.VISIBLE else View.GONE
        if (show) progressView.bringToFront()
    }

    /**
     * Retrieve the currency from the input field
     *
     * @return amount of the order
     */
    override fun getAmount(): Long {
        val amountString: String = if (amountEditText.text.isEmpty()) "0" else amountEditText.text.toString()
        return (amountString.toDouble() * 100).toLong()
    }

    /**
     * Retrieve the selected currency by the user
     *
     * @return currency code
     */
    override fun getCurrency(): String {
        return SharedPrefs.getInstance().currency
    }

    /**
     * Show an alert dialog with a custom title and message
     *
     * @param title the alert dialog title
     * @param message the alert dialog message
     */
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
    //end

    override fun showProfileDataValidationDialog() {
        val builder = android.support.v7.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

        builder.setTitle(getString(R.string.update_profile_details))
        builder.setCancelable(false)

        val rootView = LayoutInflater.from(this).inflate(R.layout.profile_data_validation_layout, null)

        if (SharedPrefs.getInstance().consumerData.equals(CONSUMER_DATA_MERCHANT_INJECTED)) {
            mPresenter.setTextView(SharedPrefs.getInstance().firstName,
                    rootView.profileFirstNameLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.first_name))

            mPresenter.setTextView(SharedPrefs.getInstance().lastName,
                    rootView.profileLastNameLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.last_name))

            mPresenter.setTextView(SharedPrefs.getInstance().prefix,
                    rootView.profilePrefixLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.prefix))

            mPresenter.setTextView(SharedPrefs.getInstance().phoneNumber,
                    rootView.profileMobileNumberLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.mobile_number))

            mPresenter.setTextView(SharedPrefs.getInstance().addressLineOne,
                    rootView.profileAddressLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.address_line_1))

            mPresenter.setTextView(SharedPrefs.getInstance().city,
                    rootView.profileCityLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.city))

            mPresenter.setTextView(SharedPrefs.getInstance().countryCode,
                    rootView.profileCountryLabel,
                    getString(R.string.asterisk) + " " + getString(R.string.country))
        }
        mPresenter.setTextView(SharedPrefs.getInstance().postalCode,
                rootView.profilePostalCodeLabel,
                getString(R.string.asterisk) + " " + getString(R.string.postal_code))

        mPresenter.setTextView(SharedPrefs.getInstance().email,
                rootView.profileEmailLabel,
                getString(R.string.asterisk) + " " + getString(R.string.email))

        builder.setView(rootView)

        builder.setPositiveButton(getString(R.string.action_ok)) { p0, _ ->
            p0.cancel()
        }

        try {
            builder.create().show()
        } catch (e: Exception) {

        }
    }
}
