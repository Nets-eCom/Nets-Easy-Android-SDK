package eu.nets.miasample.activity

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import eu.nets.miasample.R
import eu.nets.miasample.activity.MainActivity.Companion.SCREEN_PROFILE
import eu.nets.miasample.activity.MainActivity.Companion.SCREEN_SELECTED
import eu.nets.miasample.adapter.CountryNameAdapter
import eu.nets.miasample.network.APIManager
import eu.nets.miasample.utils.KeysProvider
import eu.nets.miasample.utils.SharedPrefs
import eu.nets.miasample.utils.Utilities
import kotlinx.android.synthetic.main.activity_input_keys.*
import kotlinx.android.synthetic.main.secret_keys_confirmation_layout.view.*

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
        titleText.setText(R.string.profile)
        secretKeysView.visibility = View.GONE
        editProfileView.visibility = View.VISIBLE
        firstName.setText(SharedPrefs.getInstance().firstName)
        lastName.setText(SharedPrefs.getInstance().lastName)
        email.setText(SharedPrefs.getInstance().email)
        phonePrefix.setText(SharedPrefs.getInstance().prefix)
        phoneNumber.setText(SharedPrefs.getInstance().phoneNumber)
        addressLineOne.setText(SharedPrefs.getInstance().addressLineOne)
        addressLineTwo.setText(SharedPrefs.getInstance().addressLineTwo)
        postalCode.setText(SharedPrefs.getInstance().postalCode)
        city.setText(SharedPrefs.getInstance().city)

        val countryNameAdapter = CountryNameAdapter(this, Utilities.countryNameAndCodeList())

        countrySpinner.adapter = countryNameAdapter
        countrySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                //not required
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                countryCode = Utilities.countryNameAndCodeList().get(p2).code.toString()
            }
        }
        for (i in 0 until Utilities.countryNameAndCodeList().size) {
            if (Utilities.countryNameAndCodeList().get(i).code.equals(SharedPrefs.getInstance().countryCode)) {
                countrySpinner.setSelection(i)
                break
            }
        }
    }

    private fun setSecretKeyValues() {
        profileViewSelected = false
        titleText.setText(R.string.secret_keys_title)
        secretKeysView.visibility = View.VISIBLE
        editProfileView.visibility = View.GONE
    }

    override fun validateProfileData(): Boolean {
        if (!email.text.toString().isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
            mPresenter.showValidationDialog(getString(R.string.add_valid_email_address))
            return false
        }
        return true
    }

    override fun saveProfileData() {
        SharedPrefs.getInstance().firstName = firstName.text.toString()
        SharedPrefs.getInstance().lastName = lastName.text.toString()
        SharedPrefs.getInstance().email = email.text.toString()
        SharedPrefs.getInstance().prefix = phonePrefix.text.toString()
        SharedPrefs.getInstance().phoneNumber = phoneNumber.text.toString()
        SharedPrefs.getInstance().addressLineOne = addressLineOne.text.toString()
        SharedPrefs.getInstance().addressLineTwo = addressLineTwo.text.toString()
        SharedPrefs.getInstance().postalCode = postalCode.text.toString()
        SharedPrefs.getInstance().city = city.text.toString()
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
        actionSave.setOnClickListener {
            if (profileViewSelected) {
                if (mPresenter?.validateProfileData() ?: false) {
                    mPresenter.saveProfileData()
                    mPresenter.closeScreen()
                }
            } else {
                mPresenter.showConfirmationDialog()
            }
        }

        cancelOption.setOnClickListener {
            onBackPressed() //push user back
        }
    }

    /**
     * Fill the user saved  (or default values) into the fields
     */
    override fun showPreviousInput() {
        testSecretKey.setText(KeysProvider.testSecretKey)
        testCheckoutKey.setText(KeysProvider.testCheckoutKey)
        prodSecretKey.setText(KeysProvider.prodSecretKey)
        prodCheckoutKey.setText(KeysProvider.prodCheckoutKey)
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

        val rootView = LayoutInflater.from(this).inflate(R.layout.secret_keys_confirmation_layout, null)

        rootView.testSecretKey.text = testSecretKey.text.toString()
        rootView.testCheckoutKey.text = testCheckoutKey.text.toString()
        rootView.prodSecretKey.text = prodSecretKey.text.toString()
        rootView.prodCheckoutKey.text = prodCheckoutKey.text.toString()

        builder.setView(rootView)

        builder.setPositiveButton(getString(R.string.confirm)) { p0, _ ->
            p0.cancel()
            mPresenter.saveKeys(
                    testSecretKey.text.toString(),
                    testCheckoutKey.text.toString(),
                    prodSecretKey.text.toString(),
                    prodCheckoutKey.text.toString()
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
