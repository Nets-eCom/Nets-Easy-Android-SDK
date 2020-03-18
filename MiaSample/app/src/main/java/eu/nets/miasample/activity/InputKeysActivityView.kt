package eu.nets.miasample.activity

import android.os.Bundle


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
interface InputKeysActivityView {

    /**
     * Initialize variables either from intent bundle or from savedInstanceState
     *
     * @param bundle - contains data passed to the activity
     */
    fun initData(bundle: Bundle?)

    /**
     * Initialize views and listeners
     */
    fun initListeners()

    /**
     * Fill the user saved  (or default values) into the fields
     */
    fun showPreviousInput()

    /**
     * Will open an dialog with the user inputted values for confirmation
     */
    fun showConfirmationDialog()

    /**
     * Will set the profile screen data and close the screen
     */
    fun closeScreen()

    /**
     * Set the profile date in SharedPref.
     */
    fun saveProfileData()

    /**
     * Validate profile data like Email, Phone Number and Postal Code before saving in SharedPref.
     */
    fun validateProfileData(): Boolean?

    /**
     * Show an alert dialog with a custom message
     */
    fun showValidationDialog(message: String?)
}