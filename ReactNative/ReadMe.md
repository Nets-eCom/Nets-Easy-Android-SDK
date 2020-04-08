# React Native Integration Guide

## This is a reference source code of an application (under MIT license) using the SDK, provided for demo purpose!
---

## Purpose
This document provides the basic information to include the **MiA - Android SDK** (Kotlin) in your React Native application. Please check below the instructions on how to get started.

## Prerequisites
Need-to-know basics on how to get started:
+ IDE: Visual Studio Code
+ SDK public APIs can be found in the [documentation](../documentation)
+ Android SDK minimum supported API version is 21

We have provided a `MiaSampleReactNative` application which integrates the `MiASDK` native library and uses a sample **Bridge** between JavaScript and Kotlin code.

## Step-by-step instructions
### Create the React Native Bridge
1. Add dependency for **MiaSDK**
    + In your **android** folder, in `build.gradle` application level file, add:
```gradle
implementation('eu.nets.mia:mia-sdk:1.1.0') { transitive = true; }
```
2. Create a _.java_ class in your Android folder which extends `ReactContextBaseJavaModule`. Make sure to override the `getName()` method, and return a proper String
3. Create a _.java_ class in your Android folder which extends `ReactPackage`. In the array returned by the `createNativeModules()` method add a new instance of the class created at the previous step
4. In your `MainApplication` class, in the array returned by `getPackages` method add a new instance of the class created at previous the step

### Configure the React Native Bridge

1. In your class which extends the `ReactContextBaseJavaModule`, make sure to use the correct imports. Please check the official [documentation](../documentation) to find the right paths for classes
2. Implement the `ActivityEventListener` in your class definition and override the `onActivityResult` method to receive the SDK result
3. Add class variable for a `Promise` object (this will be used to deliver the payment result to your react-native application)
4. Create setter method for this object, annotated with `@ReactMethod`. It will be called from your javascript file.
5. Create a method, annotated with `@ReactMethod`, which will receive the required parameters used to start the SDK. Check the official documentation to see which parameters are required for each SDK use case. This method will call the Native SDK method to launch the payment process.

**Note:** Check our [sample application](MiaSampleReactNative/) for a full example.

## Example

Below you can find an overview on how to create and call the bridge from your react-native application. For a detailed implementation, check the attached sample application.

+ Required imports
```java
import eu.nets.mia.data.MiAPaymentInfo;
import eu.nets.mia.MiASDK;
import eu.nets.mia.data.MiAResult;
```

+ Defining bridge class

```java
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
```
+ Call bridge methods from application

```javascript
launchSDK(paymentId, checkoutUrl){
    //set promise in bridge to handle result
    NativeModules.MiaSDK.handleSDKResult().then(()=>{
           //handle success case
        }).catch((error) =>{
            //handle error/canceled case
        });
    //launch SDK
    NativeModules.MiaSDK.startSDK(paymentId, checkoutUrl);
}
```

## MiA Sample ReactNative uses the following 3rd party library:
---
+ **[react-native-http-server](https://www.npmjs.com/package/react-native-http-server)**


