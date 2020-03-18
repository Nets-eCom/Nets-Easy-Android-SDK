package eu.nets.miasample.activity

import android.os.Bundle
import android.widget.TextView


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
interface InputKeysActivityPresenter {

    /**
     * Initialize the views and listeners. Bundle to get saved data.
     */
    fun init(savedInstanceState: Bundle?)

    /**
     * Will open an dialog with the user inputted values for confirmation
     */
    fun showConfirmationDialog()

    /**
     * Close the profile screen
     */
    fun closeScreen()

    /**
     * Validate profile data like Email, Phone Number and Postal Code before saving in SharedPref.
     */
    fun validateProfileData(): Boolean?

    /**
     * Set the profile date in SharedPref.
     */
    fun saveProfileData()

    /**
     * Show an alert dialog with a custom message
     */
    fun showValidationDialog(message: String?)

    /**
     * This method will store the user inputted keys in the Object [eu.nets.miasample.utils.KeysProvider]
     * The modifies values will be stored in RAM, the they will be cleared and restored to default BuildConfig values after the app is closed
     */
    fun saveKeys(testSecretKey: String, testCheckoutKey: String, prodSecretKey: String, prodCheckoutKey: String)

    /**
     * Method called from activity lifecycle to restore the view state
     */
    fun onResume(inputKeysActivityView: InputKeysActivityView?)

    /**
     * Method called from activity lifecycle to clear all cached data
     */
    fun onDestroy()
}