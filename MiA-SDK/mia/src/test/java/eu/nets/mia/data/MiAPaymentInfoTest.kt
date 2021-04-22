package eu.nets.mia.data

import android.os.Parcel
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotSame
import org.junit.Test
import org.mockito.Mockito.*

/**
 *****Copyright (c) 2020 Nets Denmark A/S*****
 *
 * NETS DENMARK A/S, ("NETS"), FOR AND ON BEHALF OF ITSELF AND ITS SUBSIDIARIES AND AFFILIATES UNDER COMMON CONTROL,
 * IS WILLING TO LICENSE THE SOFTWARE TO YOU ONLY UPON THE CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED
 * IN THIS LICENSE AGREEMENT.
 * BY USING THE SOFTWARE YOU ACKNOWLEDGE THAT YOU HAVE READ THE TERMS AND AGREE TO THEM.
 * IF YOU ARE AGREEING TO THESE TERMS ON BEHALF OF A COMPANY OR OTHER LEGAL ENTITY,
 * YOU REPRESENT THAT YOU HAVE THE LEGAL AUTHORITY TO BIND THE LEGAL ENTITY TO THESE TERMS. IF YOU DO NOT HAVE SUCH AUTHORITY,
 * OR IF YOU DO NOT WISH TO BE BOUND BY THE TERMS, YOU MUST NOT USE THE SOFTWARE ON THIS SITE OR ANY OTHER MEDIA ON WHICH THE SOFTWARE IS CONTAINED.
 *
 * Software is copyrighted. Title to Software and all associated intellectual property rights is retained by NETS and/or its licensors.
 * Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse engineer Software.
 *
 * No right, title or interest in or to any trademark, service mark, logo or trade name of NETS or its licensors is granted under this Agreement.
 *
 * Permission is hereby granted, to any person obtaining a copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * Software may only be used for commercial or production purpose together with
 * Easy services (as per https://tech.dibspayment.com/easy) provided from NETS, its subsidiaries or affiliates under common control.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
class MiAPaymentInfoTest {

    @Test
    fun testPrimaryConstructor() {
        val paymentId = "paymentId"
        val checkoutUrl = "checkoutUrl"
        val paymentInfo = MiAPaymentInfo(paymentId, checkoutUrl)

        assertEquals(paymentId, paymentInfo.paymentId)
        assertEquals(checkoutUrl, paymentInfo.checkoutUrl)
    }

    @Test
    fun testSecondaryConstructor() {
        val paymentId = "paymentId"
        val checkoutUrl = "checkoutUrl"
        val returnUrl = "returnUrl"
        val paymentInfo = MiAPaymentInfo(paymentId, checkoutUrl, returnUrl)

        assertEquals(paymentId, paymentInfo.paymentId)
        assertEquals(checkoutUrl, paymentInfo.checkoutUrl)
        assertEquals(returnUrl, paymentInfo.returnUrl)
    }

    @Test
    fun testParcelable() {
        val paymentId = "paymentId"
        val checkoutUrl = "checkoutUrl"
        val paymentInfo = MiAPaymentInfo(paymentId, checkoutUrl)
        val parcel: Parcel = mock(Parcel::class.java)

        paymentInfo.writeToParcel(parcel, paymentInfo.describeContents())
        parcel.setDataPosition(0)

        verify(parcel).writeString(paymentId)
        verify(parcel).writeString(checkoutUrl)

        `when`(parcel.readString()).thenReturn(paymentId)

        val paymentInfoFromParcel = MiAPaymentInfo.createFromParcel(parcel)

        assertEquals(paymentId, paymentInfoFromParcel.paymentId)
    }

    @Test
    fun testParcelable_NewArray() {
        val expected = 2
        val newArray = MiAPaymentInfo.newArray(expected)

        assertEquals(expected, newArray.size)
    }

    @Test
    fun testMiaErrorSetter() {
        val paymentId = "paymentId"
        val checkoutUrl = "checkoutUrl"

        val paymentInfo = MiAPaymentInfo(paymentId, checkoutUrl)

        val paymentIdNew = "paymentIdNew"
        val checkoutUrlNew = "checkoutUrlNew"
        paymentInfo.paymentId = paymentIdNew
        paymentInfo.checkoutUrl = checkoutUrlNew

        assertEquals(paymentIdNew, paymentInfo.paymentId)
        assertEquals(checkoutUrlNew, paymentInfo.checkoutUrl)

        assertNotSame(paymentId, paymentInfo.paymentId)
        assertNotSame(checkoutUrl, paymentInfo.checkoutUrl)
    }
}