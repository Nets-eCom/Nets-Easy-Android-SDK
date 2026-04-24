package eu.nets.miasample.activity

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import eu.nets.miasample.R
import eu.nets.miasample.activity.MainActivity.Companion.SCREEN_PROFILE
import eu.nets.miasample.activity.MainActivity.Companion.SCREEN_SELECTED
import eu.nets.miasample.adapter.CountryNameAdapter
import eu.nets.miasample.network.APIManager
import eu.nets.miasample.utils.KeysProvider
import eu.nets.miasample.utils.SharedPrefs
import eu.nets.miasample.utils.Utilities

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
class InputKeysActivity : AppCompatActivity(), InputKeysActivityView {

    private lateinit var mPresenter: InputKeysActivityPresenter
    private var profileViewSelected = false
    private var countryCode: String = ""

    //region activity lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_keys)

        mPresenter = InputKeysActivityPresenterImpl(this)
        mPresenter.init(savedInstanceState)
    }

    override fun initData(bundle: Bundle?) {
        val bundleValue: Bundle? = intent.extras
        val extras = if (bundle != null) bundle else bundleValue
        if (extras != null) {
            if (extras.getString(SCREEN_SELECTED).equals(SCREEN_PROFILE)) {
                setProfileValues()
            } else {
                setSecretKeyValues()
            }
        }
    }

    private fun setProfileValues() {
        profileViewSelected = true
        findViewById<TextView>(R.id.titleText).setText(R.string.profile)
        findViewById<ScrollView>(R.id.secretKeysView).visibility = View.GONE
        findViewById<ScrollView>(R.id.editProfileView).visibility = View.VISIBLE
        findViewById<EditText>(R.id.firstName).setText(SharedPrefs.getInstance().firstName)
        findViewById<EditText>(R.id.lastName).setText(SharedPrefs.getInstance().lastName)
        findViewById<EditText>(R.id.email).setText(SharedPrefs.getInstance().email)
        findViewById<EditText>(R.id.phonePrefix).setText(SharedPrefs.getInstance().prefix)
        findViewById<EditText>(R.id.phoneNumber).setText(SharedPrefs.getInstance().phoneNumber)
        findViewById<EditText>(R.id.addressLineOne).setText(SharedPrefs.getInstance().addressLineOne)
        findViewById<EditText>(R.id.addressLineTwo).setText(SharedPrefs.getInstance().addressLineTwo)
        findViewById<EditText>(R.id.postalCode).setText(SharedPrefs.getInstance().postalCode)
        findViewById<EditText>(R.id.city).setText(SharedPrefs.getInstance().city)

        val countryNameAdapter = CountryNameAdapter(this, Utilities.countryNameAndCodeList())

        findViewById<Spinner>(R.id.countrySpinner).run {
            adapter = countryNameAdapter
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    //not required
                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    countryCode = Utilities.countryNameAndCodeList().get(p2).code.toString()
                }
            }
        }
        for (i in 0 until Utilities.countryNameAndCodeList().size) {
            if (Utilities.countryNameAndCodeList()
                    .get(i).code.equals(SharedPrefs.getInstance().countryCode)
            ) {
                findViewById<Spinner>(R.id.countrySpinner).setSelection(i)
                break
            }
        }
    }

    private fun setSecretKeyValues() {
        profileViewSelected = false
        findViewById<TextView>(R.id.titleText).setText(R.string.secret_keys_title)
        findViewById<ScrollView>(R.id.secretKeysView).visibility = View.VISIBLE
        findViewById<ScrollView>(R.id.editProfileView).visibility = View.GONE
        //section-start-to-remove-by-script class=finalStep
        //disable input if not developer mode
        listOf(R.id.testSecretKey, R.id.testCheckoutKey, R.id.prodSecretKey, R.id.prodCheckoutKey)
            .forEach {
                findViewById<EditText>(it).isEnabled = SharedPrefs.getInstance().developerMode
            }
        //section-end-to-remove-by-script
    }

    override fun validateProfileData(): Boolean {
        val emailText = findViewById<TextView>(R.id.email).text.toString()
        if (!emailText.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            mPresenter.showValidationDialog(getString(R.string.add_valid_email_address))
            return false
        }
        return true
    }

    override fun saveProfileData() {
        SharedPrefs.getInstance().firstName = findViewById<EditText>(R.id.firstName).text.toString()
        SharedPrefs.getInstance().lastName = findViewById<EditText>(R.id.lastName).text.toString()
        SharedPrefs.getInstance().email = findViewById<EditText>(R.id.email).text.toString()
        SharedPrefs.getInstance().prefix = findViewById<EditText>(R.id.phonePrefix).text.toString()
        SharedPrefs.getInstance().phoneNumber =
            findViewById<EditText>(R.id.phoneNumber).text.toString()
        SharedPrefs.getInstance().addressLineOne =
            findViewById<EditText>(R.id.addressLineOne).text.toString()
        SharedPrefs.getInstance().addressLineTwo =
            findViewById<EditText>(R.id.addressLineTwo).text.toString()
        SharedPrefs.getInstance().postalCode =
            findViewById<EditText>(R.id.postalCode).text.toString()
        SharedPrefs.getInstance().city = findViewById<EditText>(R.id.city).text.toString()
        SharedPrefs.getInstance().countryCode = countryCode
    }

    override fun onResume() {
        super.onResume()
        mPresenter.onResume(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDestroy()
    }
    //end

    //region InputKeysActivityView interface
    /**
     * Initialize views and listeners
     */
    override fun initListeners() {
        findViewById<TextView>(R.id.actionSave).setOnClickListener {
            if (profileViewSelected) {
                if (mPresenter?.validateProfileData() ?: false) {
                    mPresenter.saveProfileData()
                    mPresenter.closeScreen()
                }
            } else {
                mPresenter.showConfirmationDialog()
            }
        }

        findViewById<TextView>(R.id.cancelOption).setOnClickListener {
            onBackPressed() //push user back
        }
    }

    /**
     * Fill the user saved  (or default values) into the fields
     */
    override fun showPreviousInput() {
        findViewById<EditText>(R.id.testSecretKey).setText(KeysProvider.testSecretKey)
        findViewById<EditText>(R.id.testCheckoutKey).setText(KeysProvider.testCheckoutKey)
        findViewById<EditText>(R.id.prodSecretKey).setText(KeysProvider.prodSecretKey)
        findViewById<EditText>(R.id.prodCheckoutKey).setText(KeysProvider.prodCheckoutKey)
    }

    override fun closeScreen() {
        onBackPressed()
    }

    override fun showValidationDialog(message: String?) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)

        builder.setTitle(message)
        builder.setCancelable(false)

        builder.setPositiveButton(getString(R.string.action_ok)) { p0, _ ->
            p0.cancel()
        }

        try {
            builder.create().show()
        } catch (e: Exception) {

        }
    }

    /**
     * Will open an dialog with the user inputted values for confirmation
     */
    override fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)

        builder.setTitle(getString(R.string.new_configuration))
        builder.setCancelable(false)

        val rootView =
            LayoutInflater.from(this).inflate(R.layout.secret_keys_confirmation_layout, null)

        rootView.findViewById<TextView>(R.id.testSecretKey).text =
            findViewById<EditText>(R.id.testSecretKey).text.toString()
        rootView.findViewById<TextView>(R.id.testCheckoutKey).text =
            findViewById<EditText>(R.id.testCheckoutKey).text.toString()
        rootView.findViewById<TextView>(R.id.prodSecretKey).text =
            findViewById<EditText>(R.id.prodSecretKey).text.toString()
        rootView.findViewById<TextView>(R.id.prodCheckoutKey).text =
            findViewById<EditText>(R.id.prodCheckoutKey).text.toString()

        builder.setView(rootView)

        builder.setPositiveButton(getString(R.string.confirm)) { p0, _ ->
            p0.cancel()
            mPresenter.saveKeys(
                findViewById<EditText>(R.id.testSecretKey).text.toString(),
                findViewById<EditText>(R.id.testCheckoutKey).text.toString(),
                findViewById<EditText>(R.id.prodSecretKey).text.toString(),
                findViewById<EditText>(R.id.prodCheckoutKey).text.toString()
            )
            APIManager.recreateInstance()
            onBackPressed() //push user back
        }

        builder.setNegativeButton(getString(R.string.cancel)) { p0, _ ->
            p0.cancel()
        }

        try {
            builder.create().show()
        } catch (e: Exception) {

        }
    }
    //end

}
