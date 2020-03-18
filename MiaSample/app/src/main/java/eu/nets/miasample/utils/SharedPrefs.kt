package eu.nets.miasample.utils

import android.content.Context
import android.content.SharedPreferences
import eu.nets.miasample.activity.MainActivity


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
class SharedPrefs {
    companion object {
        private lateinit var sharedPrefs: SharedPreferences
        private const val SHARED_PREFS_NAME = "EasySampleApp"
        private lateinit var instance: SharedPrefs
        fun init(context: Context) {
            sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            instance = SharedPrefs()
        }

        fun getInstance(): SharedPrefs {
            return instance
        }
    }

    private val emptyString = ""
    private val currencyKey = "KEY_CURRENCY"
    private val testModeKey = "TEST_MODE"
    private val chargePaymentKey = "CHARGE_PAYMENT"
    private val integrationTypeKey = "INTEGRATION_TYPE"
    private val firstNameKey = "FIRSTNAME"
    private val lastNameKey = "LASTNAME"
    private val emailKey = "EMAIL"
    private val prefixKey = "PREFIX"
    private val phoneNumberKey = "PHONE_NUMBER"
    private val addressLineOneKey = "ADDRESS_LINE_ONE"
    private val addressLineTwoKey = "ADDRESS_LINE_TWO"
    private val postalCodeKey = "POSTAL_CODE"
    private val cityKey = "CITY"
    private val countryCodeKey = "COUNTRY_CODE"
    private val consumerDataKey = "CONSUMER_DATA"

    var currency: String
        get() {
            return sharedPrefs.getString(currencyKey, MainActivity.CURRENCY_SEK)
        }
        set(value) {
            sharedPrefs.edit().putString(currencyKey, value).apply()
        }

    var testMode: Boolean
        get() {
            return sharedPrefs.getBoolean(testModeKey, true)
        }
        set(value) {
            sharedPrefs.edit().putBoolean(testModeKey, value).apply()
        }

    var chargePayment: Boolean
        get() {
            return sharedPrefs.getBoolean(chargePaymentKey, false)
        }
        set(value) {
            sharedPrefs.edit().putBoolean(chargePaymentKey, value).apply()
        }

    var integrationType: String
        get() {
            return sharedPrefs.getString(integrationTypeKey, MainActivity.EASY_HOSTED_PAYMENT_WINDOW)
        }
        set(value) {
            sharedPrefs.edit().putString(integrationTypeKey, value).apply()
        }
    var firstName: String
        get() {
            return sharedPrefs.getString(firstNameKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(firstNameKey, value).apply()
        }
    var lastName: String
        get() {
            return sharedPrefs.getString(lastNameKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(lastNameKey, value).apply()
        }
    var email: String
        get() {
            return sharedPrefs.getString(emailKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(emailKey, value).apply()
        }
    var prefix: String
        get() {
            return sharedPrefs.getString(prefixKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(prefixKey, value).apply()
        }
    var phoneNumber: String
        get() {
            return sharedPrefs.getString(phoneNumberKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(phoneNumberKey, value).apply()
        }
    var addressLineOne: String
        get() {
            return sharedPrefs.getString(addressLineOneKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(addressLineOneKey, value).apply()
        }
    var addressLineTwo: String
        get() {
            return sharedPrefs.getString(addressLineTwoKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(addressLineTwoKey, value).apply()
        }
    var postalCode: String
        get() {
            return sharedPrefs.getString(postalCodeKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(postalCodeKey, value).apply()
        }
    var city: String
        get() {
            return sharedPrefs.getString(cityKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(cityKey, value).apply()
        }
    var countryCode: String
        get() {
            return sharedPrefs.getString(countryCodeKey, emptyString)
        }
        set(value) {
            sharedPrefs.edit().putString(countryCodeKey, value).apply()
        }
    var consumerData: String
        get() {
            return sharedPrefs.getString(consumerDataKey, MainActivity.CONSUMER_DATA_NONE)
        }
        set(value) {
            sharedPrefs.edit().putString(consumerDataKey, value).apply()
        }

}
