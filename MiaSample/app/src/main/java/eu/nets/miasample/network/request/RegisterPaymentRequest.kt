package eu.nets.miasample.network.request

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
class RegisterPaymentRequest {
    @Expose
    @SerializedName("order")
    var order: Order? = null
    @Expose
    @SerializedName("checkout")
    var checkout: Checkout? = null
    @Expose
    @SerializedName("subscription")
    var subscription: Subscription? = null
    @Expose
    @SerializedName("merchantNumber")
    var merchantNumber: String? = null
    @Expose
    @SerializedName("notifications")
    var notifications: String? = null

}

class Order {
    @Expose
    @SerializedName("items")
    var items: List<Items>? = null
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

class Items {
    @Expose
    @SerializedName("reference")
    var reference: String? = null
    @Expose
    @SerializedName("name")
    var name: String? = null
    @Expose
    @SerializedName("quantity")
    var quantity: Long? = null
    @Expose
    @SerializedName("unit")
    var unit: String? = null
    @Expose
    @SerializedName("unitPrice")
    var unitPrice: Long? = null
    @Expose
    @SerializedName("taxRate")
    var taxRate: Long? = null
    @Expose
    @SerializedName("taxAmount")
    var taxAmount: Long? = null
    @Expose
    @SerializedName("grossTotalAmount")
    var grossTotalAmount: Long? = null
    @Expose
    @SerializedName("netTotalAmount")
    var netTotalAmount: Long? = null
}

class Checkout {
    @Expose
    @SerializedName("url")
    var url: String? = null
    @Expose
    @SerializedName("termsUrl")
    var termsUrl: String? = null
    @Expose
    @SerializedName("consumerType")
    var consumerType: ConsumerType? = null
    @Expose
    @SerializedName("publicDevice")
    var publicDevice: Boolean? = null
    @Expose
    @SerializedName("charge")
    var charge: Boolean? = null
    @Expose
    @SerializedName("integrationType")
    var integrationType: String? = null
    @Expose
    @SerializedName("returnURL")
    var returnURL: String? = null
    @Expose
    @SerializedName("cancelUrl")
    var cancelUrl: String? = null
    @Expose
    @SerializedName("merchantHandlesConsumerData")
    var merchantHandlesConsumerData: Boolean = false
    @Expose
    @SerializedName("consumer")
    var consumer: Consumer? = null
}

class ConsumerType {
    @Expose
    @SerializedName("supportedTypes")
    var supportedTypes: List<ConsumerTypeEnum>? = null
    @Expose
    @SerializedName("default")
    var default: ConsumerTypeEnum? = null
}

enum class ConsumerTypeEnum(val type: String) {
    B2B("B2B"), B2C("B2C");

    override fun toString(): String {
        return type
    }
}

class Consumer {
    @Expose
    @SerializedName("email")
    var email: String? = null
    @Expose
    @SerializedName("shippingAddress")
    var shippingAddress: ShippingAddress? = null
    @Expose
    @SerializedName("phoneNumber")
    var phoneNumber: PhoneNumber? = null
    @Expose
    @SerializedName("privatePerson")
    var privatePerson: PrivatePerson? = null
}

class ShippingAddress {
    @Expose
    @SerializedName("addressLine1")
    var addressLine1: String? = null
    @Expose
    @SerializedName("addressLine2")
    var addressLine2: String? = null
    @Expose
    @SerializedName("postalCode")
    var postalCode: String? = null
    @Expose
    @SerializedName("city")
    var city: String? = null
    @Expose
    @SerializedName("country")
    var country: String? = null
}

class PhoneNumber {
    @Expose
    @SerializedName("prefix")
    var prefix: String? = null
    @Expose
    @SerializedName("number")
    var number: String? = null
}

class PrivatePerson {
    @Expose
    @SerializedName("firstName")
    var firstName: String? = null
    @Expose
    @SerializedName("lastName")
    var lastName: String? = null
}


class Subscription {
    @Expose
    @SerializedName("endDate")
    var endDate: String? = null
    @Expose
    @SerializedName("interval")
    var interval: Long? = null
}