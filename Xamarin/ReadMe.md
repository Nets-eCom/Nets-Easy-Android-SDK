# Xamarin Integration Guide

## This is a reference source code of an application (under MIT license) using the SDK, provided for demo purpose!
---

## Purpose
This document provides the basic information to include the **Easy - Android SDK** (Kotlin native) in your Xamarin application. Please check below the instructions on how to get started.

## Prerequisites
Need-to-know basics on how to get started:
+ IDE: Visual Studio
+ SDK public APIs can be found in the [documentation](../documentation)
+ Android SDK minimum supported API version is 21
+ The SDK requires **Kotlin STDLib** external dependency 

**Note:** The `MiA Xamarin Android` library is also available on [NuGet](https://www.nuget.org/).

We have provided a [MiASampleXamarin](MiASampleXamarin) application which integrates the `MiASDK` Xamarin Bindings Library and implements basic functionalities.

## Step-by-step instructions
1. Include **MiA Xamarin SDK** in your application. You can do this in two ways:
    + Include NuGet solution `eu.nets.mia.mia-xamarin-android` from **Nuget Gallery** 
    + Or manually include `.dll` files we provided in your Xamarin application
        + In your solution explorer, Right click on the application's References folder: **Add Reference** - **Assemblies** - **Browse** - Go to the `DLL Files` folder and select all files and click **OK**
2. Include required external dependencies
    + In your solution explorer, Right click on the application's References folder: **Manage NuGet Packages** - Search and install the following libraries:
        + `Xamarin.Kotlin.StdLib` (the SDK is Kotlin native, so your application will need to access the Kotlin Dex classes)


## Example

+ Generate **paymentId** and retrieve **checkoutURL** from your backend

+ Start the SDK with **paymentID** and **checkoutURL**

```c#
	MiAPaymentInfo paymentInfo = new MiAPaymentInfo(paymentId, checkoutUrl);
	MiASDK.Instance.StartSDK.StartSDK(this, paymentInfo);
```

+ Handle the SDK Result
```c#
	protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
	{
		base.OnActivityResult(requestCode, resultCode, data);

		if (requestCode == MiASDK.EasySdkRequestCode)
		{
			if (resultCode == Result.Ok)
			{
				MiAResult result = (MiAResult)data.GetParcelableExtra(MiASDK.BundleCompleteResult);
				if (result.Success)
				{
					//call charge Payment on your backend
				}
				else
				{
					//something went wrong
				}
			}
			else
			{
				//user cancelled
			}
		}
	}
```

For technical information, please check our detailed overview documentation on our [GitHub Page](../documentation).

## MiA Sample Xamarin uses the following 3rd party library:
---
+ **[NanoHttpd](https://github.com/NanoHttpd/nanohttpd)**