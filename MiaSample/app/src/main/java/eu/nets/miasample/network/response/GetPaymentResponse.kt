package eu.nets.miasample.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


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
class GetPaymentResponse {

    @Expose
    @SerializedName("payment")
    var payment: Payment? = null
}

class Payment {
    @Expose
    @SerializedName("orderDetails")
    var orderDetails: OrderDetails? = null
    @Expose
    @SerializedName("summary")
    var summary: Summary? = null


    fun paymentReserved(): Boolean {
        return summary?.reservedAmount != null && orderDetails?.amount != null
                && summary?.reservedAmount == orderDetails?.amount
    }

    fun paymentCharged(): Boolean {
        return summary?.chargedAmount != null && orderDetails?.amount != null
                && summary?.chargedAmount == orderDetails?.amount
    }

    fun paymentCancelled(): Boolean {
        return summary?.reservedAmount == null && summary?.cancelledAmount == null &&
                summary?.chargedAmount == null && summary?.refundedAmount == null
    }
}

class OrderDetails {
    @Expose
    @SerializedName("amount")
    var amount: Long? = null
    @Expose
    @SerializedName("currency")
    var currency: String? = null
    @Expose
    @SerializedName("reference")
    var reference: String? = null
}

class Summary {
    @Expose
    @SerializedName("cancelledAmount")
    var cancelledAmount: Long? = null
    @Expose
    @SerializedName("chargedAmount")
    var chargedAmount: Long? = null
    @Expose
    @SerializedName("refundedAmount")
    var refundedAmount: Long? = null
    @Expose
    @SerializedName("reservedAmount")
    var reservedAmount: Long? = null
}