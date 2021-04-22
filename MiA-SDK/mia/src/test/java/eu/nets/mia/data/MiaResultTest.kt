package eu.nets.mia.data

import android.os.Parcel
import junit.framework.TestCase.*
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.lang.IllegalArgumentException

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
class MiAResultTest {

    @Test
    fun testConstructor_Primary_Success() {
        val miaResult = MiAResult(MiAResultCode.RESULT_PAYMENT_COMPLETED)

        assertEquals(MiAResultCode.RESULT_PAYMENT_COMPLETED, miaResult.miaResultCode)
        assertNull(miaResult.miaError)
    }

    @Test
    fun testConstructor_Primary_Error() {
        val miaResult = MiAResult(MiAResultCode.RESULT_PAYMENT_FAILED)

        assertEquals(MiAResultCode.RESULT_PAYMENT_FAILED, miaResult.miaResultCode)
        assertNull(miaResult.miaError)
    }

    @Test
    fun testConstructor_Primary_Canceled() {
        val miaResult = MiAResult(MiAResultCode.RESULT_PAYMENT_CANCELLED)

        assertEquals(MiAResultCode.RESULT_PAYMENT_CANCELLED, miaResult.miaResultCode)
        assertNull(miaResult.miaError)
    }

    @Test
    fun testConstructorSecondary() {
        val miaResultErrorSDK = MiAResult(MiAError.MiASDKError)

        assertEquals(MiAError.MiASDKError, miaResultErrorSDK.miaError)
        assertEquals(MiAResultCode.RESULT_PAYMENT_FAILED, miaResultErrorSDK.miaResultCode)
    }

    @Test
    fun testParcelableImplementation_Success_NoErrorCode() {
        val miaResult = MiAResult(MiAResultCode.RESULT_PAYMENT_COMPLETED)
        val parcel: Parcel = mock(Parcel::class.java)
        `when`(parcel.readInt()).thenReturn(MiAResultCode.RESULT_PAYMENT_COMPLETED.result)
        miaResult.writeToParcel(parcel, miaResult.describeContents())
        parcel.setDataPosition(0)

        val miaResultFromParcel = MiAResult.createFromParcel(parcel)

        assertEquals(MiAResultCode.RESULT_PAYMENT_COMPLETED, miaResultFromParcel.miaResultCode)
        assertNull(miaResult.miaError)
    }


    @Test
    fun testParcelableImplementation_Error_NoErrorCode() {
        val miaResult = MiAResult(MiAResultCode.RESULT_PAYMENT_FAILED)
        val parcel: Parcel = mock(Parcel::class.java)
        `when`(parcel.readInt()).thenReturn(MiAResultCode.RESULT_PAYMENT_FAILED.result)
        miaResult.writeToParcel(parcel, miaResult.describeContents())
        parcel.setDataPosition(0)

        val miaResultFromParcel = MiAResult.createFromParcel(parcel)

        assertEquals(MiAResultCode.RESULT_PAYMENT_FAILED, miaResultFromParcel.miaResultCode)
        assertNull(miaResult.miaError)
    }

    @Test
    fun testParcelableImplementation_Error_WithErrorCode() {
        val miaResult = MiAResult(MiAError.MiASDKError)
        val parcel: Parcel = mock(Parcel::class.java)
        `when`(parcel.readInt()).thenReturn(MiAResultCode.RESULT_PAYMENT_FAILED.result)
        `when`(parcel.readSerializable()).thenReturn(MiAError.MiASDKError)
        miaResult.writeToParcel(parcel, miaResult.describeContents())
        parcel.setDataPosition(0)

        val miaResultFromParcel = MiAResult.createFromParcel(parcel)

        assertEquals(MiAResultCode.RESULT_PAYMENT_FAILED, miaResultFromParcel.miaResultCode)
        assertEquals(miaResultFromParcel.miaError, MiAError.MiASDKError)
    }


    @Test
    fun testParcelableImplementation_Error_WithNullErrorCode() {
        val miaResult = MiAResult(MiAError.MiASDKError)
        val parcel: Parcel = mock(Parcel::class.java)
        `when`(parcel.readInt()).thenReturn(MiAResultCode.RESULT_PAYMENT_FAILED.result)
        `when`(parcel.readSerializable()).thenReturn(null)
        miaResult.writeToParcel(parcel, miaResult.describeContents())
        parcel.setDataPosition(0)

        val miaResultFromParcel = MiAResult.createFromParcel(parcel)

        assertEquals(MiAResultCode.RESULT_PAYMENT_FAILED, miaResultFromParcel.miaResultCode)
        assertNull(miaResultFromParcel.miaError)
    }

    @Test
    fun testParcelableImplementation_Error_WithInvalidErrorCode() {
        val miaResult = MiAResult(MiAError.MiASDKError)
        val parcel: Parcel = mock(Parcel::class.java)
        `when`(parcel.readInt()).thenReturn(MiAResultCode.RESULT_PAYMENT_FAILED.result)
        `when`(parcel.readSerializable()).thenReturn(1)//test integer in bundle instead of MiAError
        miaResult.writeToParcel(parcel, miaResult.describeContents())
        parcel.setDataPosition(0)

        val miaResultFromParcel = MiAResult.createFromParcel(parcel)

        assertEquals(MiAResultCode.RESULT_PAYMENT_FAILED, miaResultFromParcel.miaResultCode)
        assertNull(miaResultFromParcel.miaError)
    }

    @Test
    fun testParcelableImplementation_Array() {
        val arrayOfNulls = MiAResult.newArray(3)

        assertEquals(3, arrayOfNulls.size)
    }

    @Test
    fun testMiaError_getErrorMessage() {
        assertEquals(MiAError.MiASDKError.getErrorMessage(), "Something went wrong. Please try again.")
    }

    @Test
    fun testMiaError_InheritedFunction() {
        assertEquals(MiAError.MiASDKError, MiAError.values()[0])
        assertEquals(MiAError.MiASDKError, MiAError.valueOf("MiASDKError"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testMiaError_ValueOf() {
        MiAError.valueOf("someError")
    }
}