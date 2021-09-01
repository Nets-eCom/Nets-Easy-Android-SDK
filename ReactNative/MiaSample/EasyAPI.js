/**
 * Easy API
 * https://tech.dibspayment.com/easy/api/paymentapi
 */
import moment from 'moment';

export default class EasyAPI {

	constructor() {
		// A redirect URL used to identify navigation 
		// from Mia SDK interface to the application.
		//
		// Note: Pass the same `redirectURL` for 
		// payment registration with Easy API and
		// when presenting Mia SDK following payment registration. 
		this.redirectURL = "https://127.0.0.1/redirect.php"

        // Cancellation URL passed to EASY and the SDK to indentify 
        // user cancellation by using the "Go back" link rendered 
        // in the checkout webview. 
        //
		// Note: Pass the same `cancelURL` for 
		// payment registration with Easy API and
		// when presenting Mia SDK following payment registration. 
		this.cancelURL = "https://cancellation-identifier-url"

		// Cancellation URL passed to EASY and the SDK to indentify 
        // user cancellation by using the "Go back" link rendered 
        // in the checkout webview. 
		// Note: Pass the same `cancelURL` for 
		// payment registration with Easy API and
		// when presenting Mia SDK following payment registration. 
		this.cancelURL = "https://cancellation-identifier-url";

		// Default currency 
		this.currency = "SEK"


    	this.secretKey = "YOUR TEST SECRET KEY"

	}

	createPaymentWithRequestBody(requestBody, callback) {
		// EASY test environment is used for this demo
		baseURL = "https://test.api.dibspayment.eu/v1/"

		fetch(baseURL + '/payments', {
		  method: 'POST',
		  headers: {
		    'Content-Type': 'application/json',
		    'Authorization': 'Token ' + this.secretKey
		  },
		  body: JSON.stringify(requestBody),
		})
		.then((response) => response.json())
		.then((json) => {
		  callback(json)     
		})
		.catch((error) => {
		  console.error(error);
		});
	}

	makeSubscriptionRequest(price) {
		paymentRequest = this.makePaymentRequest(price)

		utc = "+03:00" // EEST timezone is used for the sample app
    	subscriptionEndDate = moment().local().utcOffset(utc).add(3, 'years').format('YYYY-MM-DDThh:mm:ss') + utc;

		paymentRequest["subscription"] = {
	      	"endDate": subscriptionEndDate,
	      	"interval": 0
	   	};

	   	return paymentRequest
	}

	// Returns the JSON request body required to create 
	// payment with Easy API. 
	makePaymentRequest(price) {
		amount = price * 100 // cents to notes 
		return {
		   "checkout": {
		      "termsUrl": "http://localhost:8080/terms",
			  "cancelURL": this.cancelURL,
		      "returnURL": this.redirectURL,
		      "cancelURL": this.cancelURL,
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
		      "reference": "MiaSDK-iOS",
		      "currency": this.currency,
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
		            "reference": "MiaSDK-iOS"
		         }
		      ]
		   },
		}
	}

}