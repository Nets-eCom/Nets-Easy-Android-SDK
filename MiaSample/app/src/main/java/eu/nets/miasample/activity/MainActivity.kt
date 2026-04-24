package eu.nets.miasample.activity

//section-start-to-remove-by-script class=finalStep
//section-end-to-remove-by-script
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import eu.nets.mia.MiASDK
import eu.nets.mia.data.MiAPaymentInfo
import eu.nets.mia.data.MiAResult
import eu.nets.mia.data.MiAResultCode
import eu.nets.miasample.BuildConfig
import eu.nets.miasample.R
import eu.nets.miasample.adapter.CurrencyAdapter
import eu.nets.miasample.adapter.IntegrationTypeAdapter
import eu.nets.miasample.network.APIManager
import eu.nets.miasample.utils.SampleLocalHost
import eu.nets.miasample.utils.SharedPrefs

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

        //Environment types
        const val TEST = "Test Environment"
        const val PRE_PROD = "Pre Prod Environment"
        const val PROD = "Prod Environment"

        //end
        //easy hosted payment window helper constants
        val RETURN_URL = String.format("%1\$s://miasdk", BuildConfig.APPLICATION_ID)

        // Cancellation URL passed to EASY and the SDK to indentify
        // user cancellation by using the "Go back" link rendered
        // in the checkout webview.
        // Note: Pass the same `cancelURL` for
        // payment registration with Easy API and
        // when presenting Mia SDK following payment registration.
        val CANCEL_URL = String.format("%1\$s://miasdk", BuildConfig.APPLICATION_ID)
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

        setSupportActionBar(findViewById(R.id.toolbar))

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
                        showAlert(
                            getString(R.string.error_title), result.miaError?.getErrorMessage()
                                ?: getString(R.string.error_message)
                        )
                    }

                    null -> showAlert(
                        getString(R.string.error_title), getString(R.string.error_message)
                    )

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
        val currencies: List<String> = ArrayList<String>(
            listOf(CURRENCY_SEK, CURRENCY_DKK, CURRENCY_NOK, CURRENCY_EUR)
        )
        val spinnerAdapter = CurrencyAdapter(this, android.R.layout.simple_spinner_item, currencies)
        findViewById<Spinner>(R.id.currencySpinner).run {
            adapter = spinnerAdapter
            setSelection(spinnerAdapter.getPositionForItem(SharedPrefs.getInstance().currency))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //not required
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    SharedPrefs.getInstance().currency = spinnerAdapter.getItem(p2)!!
                }
            }


        }

        //Environment spinner setup for internal use for including Pre-Prod environment

        //Environment type spinner
        val environmentTypes: List<String> = ArrayList(listOf(TEST, PRE_PROD, PROD))
        val environmentTypeAdapter =
            IntegrationTypeAdapter(this, android.R.layout.simple_spinner_item, environmentTypes)
        findViewById<Spinner>(R.id.environmentTypeSpinner).run {
            adapter = environmentTypeAdapter
            setSelection(environmentTypeAdapter.getPositionForItem(SharedPrefs.getInstance().environmentType))
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //not required
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    SharedPrefs.getInstance().environmentType = environmentTypeAdapter.getItem(p2)!!
                    APIManager.recreateInstance()
                }
            }

        }
        //integration type spinner
        val integrationTypes: List<String> = ArrayList(listOf(EASY_HOSTED_PAYMENT_WINDOW))
        val integrationTypeAdapter =
            IntegrationTypeAdapter(this, android.R.layout.simple_spinner_item, integrationTypes)
        findViewById<Spinner>(R.id.integrationTypeSpinner).run {
            adapter = integrationTypeAdapter
            setSelection(integrationTypeAdapter.getPositionForItem(SharedPrefs.getInstance().integrationType))
            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //not required
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        SharedPrefs.getInstance().integrationType =
                            integrationTypeAdapter.getItem(p2)!!
                    }
                }

        }

        val consumerData: List<String> = ArrayList<String>(
            listOf(
                CONSUMER_DATA_NONE,
                CONSUMER_DATA_MERCHANT_INJECTED,
                CONSUMER_DATA_NO_SHIPPING_ADDR
            )
        )
        val consumerDataAdapter =
            IntegrationTypeAdapter(this, android.R.layout.simple_spinner_item, consumerData)

        findViewById<Spinner>(R.id.consumerDataSpinner).run {
            adapter = consumerDataAdapter
            setSelection(consumerDataAdapter.getPositionForItem(SharedPrefs.getInstance().integrationType))
            onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //not required
                    }

                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        SharedPrefs.getInstance().consumerData = consumerDataAdapter.getItem(p2)!!
                    }
                }
        }

        findViewById<Button>(R.id.btnBuy).setOnClickListener {
            if (mPresenter.validateProfileData()) {
                mPresenter.launchSDK()
            }
        }

        findViewById<Button>(R.id.btnSubscribe).setOnClickListener {
            openSubscriptionsView(true)
        }

        findViewById<LinearLayout>(R.id.subscriptionView).setOnClickListener {
            openSubscriptionsView(false)
        }

        //init drawer menu
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = ""
        }

        findViewById<TextView>(R.id.clearCache).setOnClickListener {
            try {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.clear_cache_cookies))
                builder.setMessage(getString(R.string.clear_cookies_description))
                builder.setPositiveButton(getString(R.string.action_settings)) { p0, _ ->
                    p0?.dismiss()
                    //redirect the user to app settings
                    val myAppSettings = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:$packageName")
                    )
                    myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
                    startActivity(myAppSettings)
                }
                builder.setNegativeButton(getString(R.string.cancel)) { p0, _ -> p0?.dismiss() }
                builder.create().show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        drawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.action_open,
            R.string.action_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                //push root view to the right when drawer is opening
                findViewById<RelativeLayout>(R.id.rootView).translationX =
                    slideOffset * drawerView.width
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
        drawerToggle.drawerArrowDrawable.mutate()
            .setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)

        //set old user configuration visible
        val switchChargePayment = findViewById<SwitchCompat>(R.id.switchChargePayment)
        switchChargePayment.isChecked = SharedPrefs.getInstance().chargePayment

        //setup nav drawer items listeners
        val versionText = "${MiASDK.getVersionName()} (${
            MiASDK.getTechnicalVersion()
        })"
        val span = SpannableString(versionText)
        //apply BOLD span on the version name
        span.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            MiASDK.getVersionName().length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        findViewById<TextView>(R.id.fieldVersion).text = span

        switchChargePayment.setOnCheckedChangeListener { _, checked ->
            SharedPrefs.getInstance().chargePayment = checked
        }

        findViewById<TextView>(R.id.changeKeys).setOnClickListener {
            //close drawer first
            drawerLayout.closeDrawer(GravityCompat.START)
            //make flag true; activity will be launched after the drawer is closed
            launchInputActivity = true
        }
        findViewById<TextView>(R.id.editProfile).setOnClickListener {
            profileSelected = true
            //close drawer first
            drawerLayout.closeDrawer(GravityCompat.START)
            //make flag true; activity will be launched after the drawer is closed
            launchInputActivity = true
        }
    }

    fun openSubscriptionsView(createSubscription: Boolean) {
        val intent = Intent(this, SubscriptionActivity::class.java)
        intent.putExtra("ChargeableAmount", getAmount())
        intent.putExtra("ChargeableCurrency", getCurrency())
        intent.putExtra("CreateSubscription", createSubscription)
        startActivity(intent)
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
    override fun launchEasySDK(
        paymentId: String?,
        checkoutUrl: String?,
        returnUrl: String?,
        cancelUrl: String?
    ) {
        if (paymentId == null || checkoutUrl == null) {
            showAlert(getString(R.string.error_title), getString(R.string.error_message))
            return
        } else if (validateAmount()) {
            MiASDK.startSDK(this, MiAPaymentInfo(paymentId, checkoutUrl, returnUrl, cancelUrl))
        }
    }

    /**
     * Show/hide progress view based on the boolean flag
     *
     * @param show boolean flag to show loader or to hide it
     */
    override fun showLoader(show: Boolean) {
        findViewById<RelativeLayout>(R.id.progressView).run {
            visibility = if (show) View.VISIBLE else View.GONE
            if (show) bringToFront()
        }
    }

    /**
     * Retrieve the currency from the input field
     *
     * @return amount of the order
     */
    override fun getAmount(): Long {
        val amountString: String =
            findViewById<EditText>(R.id.amountEditText).text.ifEmpty { "0" }.toString()
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
        val builder = androidx.appcompat.app.AlertDialog.Builder(this, R.style.AlertDialogTheme)

        builder.setTitle(getString(R.string.update_profile_details))
        builder.setCancelable(false)

        val rootView =
            LayoutInflater.from(this).inflate(R.layout.profile_data_validation_layout, null)

        if (SharedPrefs.getInstance().consumerData.equals(CONSUMER_DATA_MERCHANT_INJECTED)) {
            mPresenter.setTextView(
                SharedPrefs.getInstance().firstName,
                rootView.findViewById(R.id.profileFirstNameLabel),
                getString(R.string.asterisk) + " " + getString(R.string.first_name)
            )

            mPresenter.setTextView(
                SharedPrefs.getInstance().lastName,
                rootView.findViewById(R.id.profileLastNameLabel),
                getString(R.string.asterisk) + " " + getString(R.string.last_name)
            )

            mPresenter.setTextView(
                SharedPrefs.getInstance().prefix,
                rootView.findViewById(R.id.profilePrefixLabel),
                getString(R.string.asterisk) + " " + getString(R.string.prefix)
            )

            mPresenter.setTextView(
                SharedPrefs.getInstance().phoneNumber,
                rootView.findViewById(R.id.profileMobileNumberLabel),
                getString(R.string.asterisk) + " " + getString(R.string.mobile_number)
            )

            mPresenter.setTextView(
                SharedPrefs.getInstance().addressLineOne,
                rootView.findViewById(R.id.profileAddressLabel),
                getString(R.string.asterisk) + " " + getString(R.string.address_line_1)
            )

            mPresenter.setTextView(
                SharedPrefs.getInstance().city,
                rootView.findViewById(R.id.profileCityLabel),
                getString(R.string.asterisk) + " " + getString(R.string.city)
            )

            mPresenter.setTextView(
                SharedPrefs.getInstance().countryCode,
                rootView.findViewById(R.id.profileCountryLabel),
                getString(R.string.asterisk) + " " + getString(R.string.country)
            )
        }
        mPresenter.setTextView(
            SharedPrefs.getInstance().postalCode,
            rootView.findViewById(R.id.profilePostalCodeLabel),
            getString(R.string.asterisk) + " " + getString(R.string.postal_code)
        )

        mPresenter.setTextView(
            SharedPrefs.getInstance().email,
            rootView.findViewById(R.id.profileEmailLabel),
            getString(R.string.asterisk) + " " + getString(R.string.email)
        )

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
