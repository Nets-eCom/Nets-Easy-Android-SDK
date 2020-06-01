package com.reactlibrary;

import android.app.Activity;
import android.content.Intent;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.HashMap;

import eu.nets.mia.MiASDK;
import eu.nets.mia.data.MiAPaymentInfo;
import eu.nets.mia.data.MiAResult;
import eu.nets.mia.data.MiAResultCode;

public class RnMiaModule extends ReactContextBaseJavaModule implements ActivityEventListener {

    private Callback cancellation, completion;
    private ReactApplicationContext reactContext;

    public RnMiaModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "RnMia";
    }

    /**
     * Presents a checkout screen for given payment details
     *
     * @param paymentId    Payment ID retrieved from Easy
     * @param checkoutUrl  Payment URL retrieved from Easy
     * @param returnUrl    The URL used to redirect from Easy hosted web view
     * @param completion   Callback invoked upon completion
     *                     - Returns empty parameters if successful.
     *                     - Returns an error HashMap object if payment failed.
     * @param cancellation cancellation invoked if user cancelled payment process (typically by dismissing the WebView)
     */
    @ReactMethod
    public void checkoutWithPaymentID(String paymentId, String checkoutUrl, String returnUrl, final Callback completion, final Callback cancellation) {
        this.completion = completion;
        this.cancellation = cancellation;
        MiASDK.Companion.getInstance().startSDK(getCurrentActivity(), new MiAPaymentInfo(paymentId, checkoutUrl, returnUrl));
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                cancellation.invoke();
                break;
            case Activity.RESULT_OK:
                MiAResult result = data.getParcelableExtra(MiASDK.BUNDLE_COMPLETE_RESULT);
                MiAResultCode miAResultCode = result.getMiaResultCode();
                switch (miAResultCode) {
                    case RESULT_PAYMENT_COMPLETED:
                        completion.invoke();
                        break;
                    case RESULT_PAYMENT_CANCELLED:
                        cancellation.invoke();
                        break;
                    case RESULT_PAYMENT_FAILED:
                        HashMap rnError = new HashMap<String, Object>();
                        rnError.put("Error", result.getMiaError().getErrorMessage());
                        completion.invoke(rnError);
                        break;
                }
                break;
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
    }
}