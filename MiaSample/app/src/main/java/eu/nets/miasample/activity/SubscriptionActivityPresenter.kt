package eu.nets.miasample.activity

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
interface SubscriptionActivityPresenter{

    /**
     * Call view's initListeners
     */
    fun init()

    /**
     * Launches the SDK with the paymentId and checkoutUrl
     */
    fun launchSDK()

    /**
     * Call /payment API to create a subscription and get a paymentID for the Subscription created to be used in checkout page
     * Api request has the data for creating a subscription
     * If paymentId is created, the localhost server will be started and the SDK is launched
     */
    fun createSubscription()

    /**
     * Call /payment/{paymentId} to retrieve the subscription id
     */
    fun getSubscriptionId()

    /**
     * Charging a specific subscription with its ID
     */
    fun chargeSubscription(subscriptionId: String)

    /**
     * If the subscription is successfully created, fetch the details using subscription id
     */
    fun fetchSubscriptionDetails()

    /**
     * Method called from activity lifecycle to restore the view state
     */
    fun onResume(subscriptionActivityView: SubscriptionActivity?)

    /**
     * Method called from activity lifecycle to clear all cached data
     */
    fun onDestroy()

}