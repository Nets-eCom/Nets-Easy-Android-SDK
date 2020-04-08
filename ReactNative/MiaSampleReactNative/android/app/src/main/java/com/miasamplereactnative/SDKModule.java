package com.miasamplereactnative;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import eu.nets.mia.data.MiAPaymentInfo;
import eu.nets.mia.MiASDK;
import eu.nets.mia.data.MiAResult;

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
public class SDKModule extends ReactContextBaseJavaModule implements ActivityEventListener{

    //Promise used to deliver the payment result back to JavaScript code
    private Promise paymentResult;
    //end

    public SDKModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    /**
     * OnActivityResult method -- here you can handle the payment result and deliver through promise (paymentResult) the result
     * 
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (paymentResult != null) {
            if (resultCode == Activity.RESULT_CANCELED) {
                paymentResult.reject("1", "Canceled");
            } else if (resultCode == Activity.RESULT_OK) {
                MiAResult result = data.getParcelableExtra(MiASDK.BUNDLE_COMPLETE_RESULT);
                paymentResult.resolve(true);
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent)  {
        
    }

    @Override
    public String getName() {
        return "MiaSDK";
    }

    @ReactMethod
    public void handleSDKResult(Promise paymentResult) {
        this.paymentResult = paymentResult;
    }

    @ReactMethod
    public void startSDK(String paymentId, String checkoutURL) {
        MiASDK.Companion.getInstance().startSDK(getCurrentActivity(), new MiAPaymentInfo(paymentId, checkoutURL));
    }

}