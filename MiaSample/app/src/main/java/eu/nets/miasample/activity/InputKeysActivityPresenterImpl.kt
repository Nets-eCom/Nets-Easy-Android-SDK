package eu.nets.miasample.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import eu.nets.miasample.R
import eu.nets.miasample.activity.MainActivity.Companion.CONSUMER_DATA_MERCHANT_INJECTED
import eu.nets.miasample.utils.KeysProvider
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

class InputKeysActivityPresenterImpl(var mView: InputKeysActivityView?) : InputKeysActivityPresenter {

    //input keys activity presenter interface
    /**
     * Initialize the views and listeners
     */
    override fun init(savedInstanceState: Bundle?) {
        mView?.initData(savedInstanceState)
        mView?.initListeners()
        mView?.showPreviousInput()

    }

    /**
     * Will open an dialog with the user inputted values for confirmation
     */
    override fun showConfirmationDialog() {
        mView?.showConfirmationDialog()
    }

    /**
     * This method will store the user inputted keys in the Object [eu.nets.miasample.utils.KeysProvider]
     * The modifies values will be stored in RAM, the they will be cleared and restored to default BuildConfig values after the app is closed
     */
    override fun saveKeys(testSecretKey: String, testCheckoutKey: String, prodSecretKey: String, prodCheckoutKey: String) {
        KeysProvider.testSecretKey = testSecretKey
        KeysProvider.testCheckoutKey = testCheckoutKey
        KeysProvider.prodSecretKey = prodSecretKey
        KeysProvider.prodCheckoutKey = prodCheckoutKey
    }
    /**
     * Method called from activity lifecycle to restore the view state
     */
    override fun onResume(inputKeysActivityView: InputKeysActivityView?) {
        this.mView = inputKeysActivityView
    }

    override fun showValidationDialog(message: String?) {
        mView?.showValidationDialog(message)
    }

    override fun closeScreen() {
        mView?.closeScreen()
    }

    override fun saveProfileData() {
        mView?.saveProfileData()
    }

    override fun validateProfileData(): Boolean? {
       return mView?.validateProfileData()
    }

    /**
     * Casts the view to Context
     * This is used to access the string resources
     *
     * @return InputKeysActivity context
     */
    private fun getContext(): Context {
        return mView as Context
    }

    /**
     * Method called from activity lifecycle to clear all cached data
     */
    override fun onDestroy() {
        this.mView = null
    }
    //end

}
