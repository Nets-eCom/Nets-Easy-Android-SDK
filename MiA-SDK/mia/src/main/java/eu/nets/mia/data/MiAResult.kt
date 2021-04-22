package eu.nets.mia.data

import android.os.Parcel
import android.os.Parcelable

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

/**
 * MiaResult class used to deliver the payment result back to merchant app
 *
 * @param success flag used to specify if the payment was successful or not
 */
class MiAResult(
        /**
         * Boolean flag which specifies the the payment process is successful or not
         */
        val miaResultCode: MiAResultCode
) : Parcelable {

    /**
     * error enum item; check Overview documentation on how to Handle error codes
     */
    var miaError: MiAError? = null

    /**
     * Constructor called by restoring state from parcel
     *
     * @param parcel the Parcel containing success val and/or miaError object
     */
    constructor(parcel: Parcel) : this(MiAResultCode.findByInt(parcel.readInt())) {
        miaError = try {
            //if before writing to parcel the miaError was null, it needs to be null after createFromParcel
            parcel.readSerializable() as MiAError
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Secondary constructor; this is called only for error cases; it will set result code to RESULT_PAYMENT_FAILED by default
     *
     * @param miaError the error enum item (containing a code and a explanation message)
     */
    constructor(miaError: MiAError) : this(MiAResultCode.RESULT_PAYMENT_FAILED) {
        this.miaError = miaError
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(miaResultCode.result)
        parcel.writeSerializable(miaError)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MiAResult> {
        override fun createFromParcel(parcel: Parcel): MiAResult {
            return MiAResult(parcel)
        }

        override fun newArray(size: Int): Array<MiAResult?> {
            return arrayOfNulls(size)
        }
    }

}

enum class MiAError(val errorCode: Int) {
    /**
     * Error code 101; This occurs inside the SDK when something went wrong (e.g. references to
     * paymentId and/or checkoutUrl are cleared from RAM, and the payment cannot continue)
     */
    MiASDKError(101);//SDK Internal error; Something wen't wrong and payment cannot continue

    /**
     * Function to return a brief description of the error
     *
     * @return Explanation message for each error
     */
    fun getErrorMessage(): String {
        return "Something went wrong. Please try again."
    }
}

enum class MiAResultCode(val result: Int) {
    /**
     * The payment process has completed by the user
     */
    RESULT_PAYMENT_COMPLETED(0),
    /**
     * The payment process was canceled by the user
     */
    RESULT_PAYMENT_CANCELLED(1),
    /**
     * The payment process has encountered and error and cannot continue
     */
    RESULT_PAYMENT_FAILED(2);

    companion object {
        /**
         * Find the enum value of a specific int code
         */
        fun findByInt(resultCodeInt: Int): MiAResultCode {
            for (code in values()) {
                if (code.result == resultCodeInt) {
                    return code
                }
            }
            return RESULT_PAYMENT_FAILED
        }
    }
}