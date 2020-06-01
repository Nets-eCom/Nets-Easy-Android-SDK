# react-native-rn-mia


## Installation

`$ npm install react-native-rn-mia --save`

This links the module in Android but more steps are required to complete the *iOS* integration.

    - Navigate to the ios folder and install pods: `cd ios && pod install && cd ..`

    - Open your application xcworkspace and add Mia.framework to the Libraries folder
        - Right click on Libraries - Add Files to "Your project" and find Mia.framework in *react-native-rn-mia/ios* directory
        - Under application target - *Frameworks, Libraries, and Embedded Content*, select *Embed & Sign* for Mia.framework


## Usage (Access Mia API)

The react native wrapper module can be accessed from your javascript by importing RNMia module. 
```js
import RNMia from 'react-native-rn-mia';
```

### Mia Checkout API

Present Mia checkout after obtaining a payment ID and payment URL from EASY `create payment` REST API.
See the [example](### Example - Create Payment with EASY) to create payment with order and merchant details.

```js
// This example presents Mia SDK's checkout WebView and handles 
// the callbacks by presenting an alert accordingly
RNMia.checkoutWithPaymentID(paymentID, paymentURL, redirectURL, 
(error) => {
   alertTitle = error ? "Error" : "Payment Successful"
   Alert.alert(alertTitle, error, [{text:"Ok"}])
}, 
(cancellation) => {
   Alert.alert("Cancelled", "You have cancelled the payment", [{text:"Ok"}])
});
```

Note: A redirect URL is used to identify navigation from Mia SDK back to the application.
Pass the same `redirectURL` when creating the payment with Easy API and 
when presenting checkout with Mia SDK as shown above. 

Callbacks:
    - Success: The first callback is invoked with no `error` if payment was successful
    - Failure: The first callback returns an `error` map object in case of error. User "Error" key to obtain error message. 
    - Cancellation: The second callback is invoked if the user cancelled the process. The callback does not contain any argument. 

### Example (Create Payment with EASY)

Create the request body:

```js 
requestBody = {
    "checkout": {
        "termsUrl": URL,
        "returnURL": redirectURL,
        "consumerType": {
            "supportedTypes": [
                "B2C",
                "B2B"
             ],
            "default": "B2C"
        },
        "integrationType": "HostedPaymentPage"
    },
    "order": {
        "reference": "reference",
        "currency": "SEK",
        "amount": amount,
        "items": [
            {
                "unit": "pcs",
                "netTotalAmount": amount,
                "taxAmount": 0,
                "grossTotalAmount": amount,
                "quantity": 1,
                "name": "Lightning Cable",
                "unitPrice": amount,
                "taxRate": 0,
                "reference": "reference"
             }
        ]
    },
}
```

Initiate payment using your merchant secret key to authorize with EASY

```js 
// Test env. is used for this demo
baseURL = "https://test.api.dibspayment.eu/v1/"
fetch(baseURL + '/payments', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Token ' + secretKey
    },
    body: JSON.stringify(requestBody),
})
.then((response) => response.json())
.then((json) => {

    // Use these `paymentID` and `paymentURL` to present Mia SDK (see above)
    paymentID = json.paymentId 
    paymentURL = json.hostedPaymentPageUrl  
    
})
.catch((error) => {
    console.error(error);
});
```

Mia SDK presents a checkout WebView with the payment information and returns 
the result to the application via callbacks.

In order to initiate a subscription payment, include a `subscription` key in the request body 
containing `endDate` and `interval` values. See EasyAPI.js of the sample app for a complete example. 
