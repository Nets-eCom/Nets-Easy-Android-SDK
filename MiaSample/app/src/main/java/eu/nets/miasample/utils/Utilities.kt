package eu.nets.miasample.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eu.nets.miasample.model.CountryNameCode
import eu.nets.miasample.network.response.SubscriptionDetailsResponse
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * *****Copyright (c) 2020 Nets Denmark A/S*****
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

class Utilities {

    companion object {

        fun countryNameAndCodeList(): List<CountryNameCode> {

            val countryCodeList = ArrayList<CountryNameCode>()

            val countryNameCode1 = CountryNameCode("Norway", "NOR")
            countryCodeList.add(countryNameCode1)

            val countryNameCode2 = CountryNameCode("Finland", "FIN")
            countryCodeList.add(countryNameCode2)

            val countryNameCode3 = CountryNameCode("Denmark", "DNK")
            countryCodeList.add(countryNameCode3)

            val countryNameCode4 = CountryNameCode("Sweden", "SWE")
            countryCodeList.add(countryNameCode4)

            val countryNameCode5 = CountryNameCode("Albania", "ALB")
            countryCodeList.add(countryNameCode5)

            val countryNameCode6 = CountryNameCode("Andorra", "AND")
            countryCodeList.add(countryNameCode6)

            val countryNameCode7 = CountryNameCode("Armenia", "ARM")
            countryCodeList.add(countryNameCode7)

            val countryNameCode8 = CountryNameCode("Austria", "AUT")
            countryCodeList.add(countryNameCode8)

            val countryNameCode9 = CountryNameCode("Azerbaijan", "AZE")
            countryCodeList.add(countryNameCode9)

            val countryNameCode10 = CountryNameCode("Belgium", "BEL")
            countryCodeList.add(countryNameCode10)

            val countryNameCode11 = CountryNameCode("Bulgaria", "BGR")
            countryCodeList.add(countryNameCode11)

            val countryNameCode12 = CountryNameCode("Bosnia and Herzegovina", "BIH")
            countryCodeList.add(countryNameCode12)

            val countryNameCode13 = CountryNameCode("Belarus", "BLR")
            countryCodeList.add(countryNameCode13)

            val countryNameCode14 = CountryNameCode("Switzerland", "CHE")
            countryCodeList.add(countryNameCode14)

            val countryNameCode15 = CountryNameCode("Cyprus", "CYP")
            countryCodeList.add(countryNameCode15)

            val countryNameCode16 = CountryNameCode("Czechia", "CZE")
            countryCodeList.add(countryNameCode16)

            val countryNameCode17 = CountryNameCode("Germany", "DEU")
            countryCodeList.add(countryNameCode17)

            val countryNameCode18 = CountryNameCode("Spain", "ESP")
            countryCodeList.add(countryNameCode18)

            val countryNameCode19 = CountryNameCode("Estonia", "EST")
            countryCodeList.add(countryNameCode19)

            val countryNameCode20 = CountryNameCode("France", "FRA")
            countryCodeList.add(countryNameCode20)

            val countryNameCode21 = CountryNameCode("United Kingdom", "GBR")
            countryCodeList.add(countryNameCode21)

            val countryNameCode22 = CountryNameCode("Georgia", "GEO")
            countryCodeList.add(countryNameCode22)

            val countryNameCode23 = CountryNameCode("Greece", "GRC")
            countryCodeList.add(countryNameCode23)

            val countryNameCode24 = CountryNameCode("Croatia", "HRV")
            countryCodeList.add(countryNameCode24)

            val countryNameCode25 = CountryNameCode("Hungary", "HUN")
            countryCodeList.add(countryNameCode25)

            val countryNameCode26 = CountryNameCode("Ireland", "IRL")
            countryCodeList.add(countryNameCode26)

            val countryNameCode27 = CountryNameCode("Iceland", "ISL")
            countryCodeList.add(countryNameCode27)

            val countryNameCode28 = CountryNameCode("Italy", "ITA")
            countryCodeList.add(countryNameCode28)

            val countryNameCode29 = CountryNameCode("Kazakhstan", "KAZ")
            countryCodeList.add(countryNameCode29)

            val countryNameCode30 = CountryNameCode("Liechtenstein", "LIE")
            countryCodeList.add(countryNameCode30)

            val countryNameCode31 = CountryNameCode("Lithuania", "LTU")
            countryCodeList.add(countryNameCode31)

            val countryNameCode32 = CountryNameCode("Luxembourg", "LUX")
            countryCodeList.add(countryNameCode32)

            val countryNameCode33 = CountryNameCode("Latvia", "LVA")
            countryCodeList.add(countryNameCode33)

            val countryNameCode34 = CountryNameCode("Monaco", "MCO")
            countryCodeList.add(countryNameCode34)

            val countryNameCode35 = CountryNameCode("Moldova", "MDA")
            countryCodeList.add(countryNameCode35)

            val countryNameCode36 = CountryNameCode("Macedonia", "MKD")
            countryCodeList.add(countryNameCode36)

            val countryNameCode37 = CountryNameCode("Malta", "MLT")
            countryCodeList.add(countryNameCode37)

            val countryNameCode38 = CountryNameCode("Montenegro", "MNE")
            countryCodeList.add(countryNameCode38)

            val countryNameCode39 = CountryNameCode("Netherlands", "NLD")
            countryCodeList.add(countryNameCode39)

            val countryNameCode40 = CountryNameCode("Poland", "POL")
            countryCodeList.add(countryNameCode40)

            val countryNameCode41 = CountryNameCode("Portugal", "PRT")
            countryCodeList.add(countryNameCode41)

            val countryNameCode42 = CountryNameCode("Romania", "ROU")
            countryCodeList.add(countryNameCode42)

            val countryNameCode43 = CountryNameCode("Russia", "RUS")
            countryCodeList.add(countryNameCode43)

            val countryNameCode44 = CountryNameCode("San Marino", "SMR")
            countryCodeList.add(countryNameCode44)

            val countryNameCode45 = CountryNameCode("Serbia", "SRB")
            countryCodeList.add(countryNameCode45)

            val countryNameCode46 = CountryNameCode("Slovakia", "SVK")
            countryCodeList.add(countryNameCode46)

            val countryNameCode47 = CountryNameCode("Slovenia", "SVN")
            countryCodeList.add(countryNameCode47)

            val countryNameCode48 = CountryNameCode("Turkey", "TUR")
            countryCodeList.add(countryNameCode48)

            val countryNameCode49 = CountryNameCode("Ukraine", "UKR")
            countryCodeList.add(countryNameCode49)

            val countryNameCode50 = CountryNameCode("Vatican City", "VAT")
            countryCodeList.add(countryNameCode50)

            return countryCodeList
        }

        /**
         * Returns Subscription end date
         * Date returned by this method is kept as the current date plus 3 years.
         */
        fun createSubscriptionEndDate(): String? {
            val df = SimpleDateFormat("yyyy-MM-dd")
            val c = Calendar.getInstance()
            c.add(Calendar.YEAR, 3)
            val future = c.time
            val dateInISOFormat = df.format(future)
            return dateInISOFormat
        }

        fun isStringNullorEmpty(stringToCheck: String?): Boolean {
            if (stringToCheck == null || stringToCheck.isEmpty()) {
                return true
            }
            return false
        }

        /**
         * Saving the Subscription list in the SharedPreference
         */
        fun saveSubscriptionDetails(response: SubscriptionDetailsResponse?) {
            try {
                val subscriptionDetailsList: MutableList<SubscriptionDetailsResponse>

                var subscriptionDetails = SharedPrefs.getInstance().subscriptionData
                if (isStringNullorEmpty(subscriptionDetails)) {
                    subscriptionDetailsList = mutableListOf();
                } else {
                    val modalType = object : TypeToken<List<SubscriptionDetailsResponse>>() {}.type
                    subscriptionDetailsList = Gson().fromJson<MutableList<SubscriptionDetailsResponse>>(subscriptionDetails, modalType)
                }
                if (response != null) {
                    subscriptionDetailsList.add(0, response)
                    subscriptionDetails = Gson().toJson(subscriptionDetailsList)
                    SharedPrefs.getInstance().subscriptionData = subscriptionDetails
                }
            } catch (e: Exception) {
            }
        }

    }


}