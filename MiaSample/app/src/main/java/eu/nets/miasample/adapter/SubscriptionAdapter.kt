package eu.nets.miasample.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.nets.miasample.R
import eu.nets.miasample.activity.SubscriptionActivityView
import eu.nets.miasample.network.response.SubscriptionDetailsResponse
import eu.nets.miasample.utils.SharedPrefs
import android.support.v4.text.HtmlCompat
import kotlinx.android.synthetic.main.subscription_row.view.*

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

class SubscriptionAdapter(var subscriptionList: MutableList<SubscriptionDetailsResponse>, val context: Context) : RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder>() {

    var isChargeable: Boolean
    var mView: SubscriptionActivityView

    init {
        isChargeable = SharedPrefs.getInstance().chargePayment
        mView = context as SubscriptionActivityView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        return SubscriptionViewHolder(LayoutInflater.from(context).inflate(R.layout.subscription_row, parent, false))
    }

    override fun getItemCount(): Int {
        return subscriptionList.size
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        holder.setupSubscriptionRow(subscriptionList[position], isChargeable, mView)
    }

    fun updateList(subscriptionList: MutableList<SubscriptionDetailsResponse>) {
        isChargeable = SharedPrefs.getInstance().chargePayment
        this.subscriptionList = subscriptionList
        notifyDataSetChanged()
    }

    class SubscriptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvCard_details = itemView.card_details
        val tvCardExpiry = itemView.card_expiry
        val tvSubscriptionId = itemView.subscription_id
        val tvSubscriptionExpiry = itemView.subscription_expiry
        val btnChargeSubscription = itemView.charge_subscription

        fun setupSubscriptionRow(subscriptionData: SubscriptionDetailsResponse, isChargeable: Boolean, mView: SubscriptionActivityView) {
            try {
                val paymentDetails = subscriptionData.paymentDetails
                val cardDetails = paymentDetails?.cardDetails
                val subscriptionId = subscriptionData.subscriptionId
                if (subscriptionId != null) {
                    val subscriptionIdString = "<b>Subscription ID:</b> $subscriptionId"
                    val cardExpireString = "<b>Card Expires:</b> ${parseCardExpDate(cardDetails?.expiryDate!!)}"
                    val subscriptionExpireString = "<b>Subscription Expires:</b> ${parseISO8601Date(subscriptionData.endDate!!)}"
                    val cardDetailsString = "${paymentDetails.paymentMethod} - ${cardDetails.maskedPan}"

                    tvSubscriptionId.text = HtmlCompat.fromHtml(subscriptionIdString, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvCardExpiry.text = HtmlCompat.fromHtml(cardExpireString, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    tvCard_details.text = cardDetailsString
                    tvSubscriptionExpiry.text = HtmlCompat.fromHtml(subscriptionExpireString, HtmlCompat.FROM_HTML_MODE_LEGACY)

                    if (isChargeable) {
                        btnChargeSubscription.setBackgroundColor(ContextCompat.getColor(mView as Context, R.color.colorPrimaryDark))
                    }
                    btnChargeSubscription.setOnClickListener {
                        mView.chargeSubscription(subscriptionData.subscriptionId!!, isChargeable)
                    }
                }
            } catch (e: Exception) {
            }
        }

        /**
         * Parsing the received date and returning the Subscription's expiry month and year
         */
        private fun parseISO8601Date(data: String): String? {
            try {
                return data.split("-")[1] + " / " + data.split("-")[0]
            } catch (e: Exception) {
                return data
            }
        }

        /**
         * Parsing the received date and returning the card's expiry month and year
         */
        private fun parseCardExpDate(data: String): String? {
            try {
                return "${data[0]}${data[1]} / ${data[2]}${data[3]}"
            } catch (e: Exception) {
                return data
            }
        }
    }
}

