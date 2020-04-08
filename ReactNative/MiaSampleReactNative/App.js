/**
 * MIT License
 * <p>
 * Copyright (c) 2020 Nets Denmark A/S
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy  of this software
 * and associated documentation files (the "Software"), to deal  in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is  furnished to do so,
 * subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, Button, View, ToastAndroid} from 'react-native';
import StaticServer from 'react-native-static-server';
import RNFS from 'react-native-fs';
import { NativeModules } from 'react-native';

type Props = {};
export default class App extends Component<Props> {

  constructor(opts) {
    super();

    this.state = {
      origin: '' //origin will be the root url for localhost
    }
  }

  componentWillMount() {
    //create html file with proper contents
    this.port = this.props.port || 5000;
    this.root = this.props.root || "www/";
    this.file = this.props.file || 'index.html';
    this.terms = this.props.file || 'terms.html';

    let path = RNFS.DocumentDirectoryPath + "/" + this.root;
    this.checkoutPage = path + this.file;
    this.termsPage = path + this.terms;

    // Create a StaticServer at port
    this.server = new StaticServer(this.port, this.root,  {localOnly: true, keepAlive : true});

    // Add the directory
    RNFS.mkdir(path, { NSURLIsExcludedFromBackupKey: true });

    //prepare terms page
    RNFS.writeFile(this.termsPage, termsHtml, 'utf8')
    .then((success) => {
       console.log("Terms Page ready");
    })
    .catch((err) => {
      console.log(err.message);
    });

    //start server to have the URL to index
    this.server.start().then((origin) => {
      this.setState({origin});
    })
    .catch((err) => {
      console.log(err.message);
    });
  }

  /**
    Close the sever to release the port when the app is in background
   */
  componentWillUnmount() {
    if (this.server) {
      this.server.kill();
    }
  }

  render() {
    return (
      <View style={styles.container}>
        <Text style={styles.welcome}>Welcome to Mia Sample React Native!</Text>
        <Text style={styles.instructions}>Check our basic implementation here</Text>

        <View style={styles.button}>
          <Button style={styles.button} onPress={() => this.registerPayment(`${this.state.origin}/${this.file}`, `${this.state.origin}/${this.terms}`)} title="Pay 10.00 kr (TEST)" />
        </View>
      </View>
    );
  }

  /**
    Call Register payment to TEST Environment and get PAYMENT_ID
   */
  registerPayment (checkoutUrl, termsUrl) {
    fetch('https://test.api.dibspayment.eu/v1/payments', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': 'test-secret-key-f9b4a4d659e042e28f81bb187f990a39'
              },
            body: 	'{"order": {'+
                        '	"items": [{'+
                          '	"reference": "MiASDK-Android-RN",'+
                          '	"name": "Lightning Cable",'+
                          '	"quantity": 1,'+
                          '	"unit": "pcs",'+
                          '	"unitPrice": 1000,'+
                          '	"taxRate": 0,'+
                          '	"taxAmount": 0,'+
                          '	"grossTotalAmount": 1000,'+
                            '"netTotalAmount": 1000'+
                        '	}],'+
                        '	"amount": 1000,'+
                        '	"currency": "SEK",'+
                        '	"reference": "MiASDK-Android-RN"'+
                        '},'+
                        '"checkout": {'+
                        '	"url":"' + checkoutUrl + '",'+
                        '	"termsUrl": "' + termsUrl + '",'+
                        '	"consumerType": {'+
                        '		"supportedTypes": ["B2C", "B2B"],'+
                        '		"default": "B2C"'+
                        '	},'+
                        '	"charge": false'+
                        '}'+
                      '}'
          }).then((response) => response.json())
          .then((responseJson) => {
                if(responseJson.paymentId != null){
                  //is success
                  //call MiaSDK start from RB Bridge
                  this.startLocalHost(responseJson.paymentId, checkoutUrl);
                } else{
                  //is error
                  ToastAndroid.show("Error registering payment!", ToastAndroid.SHORT);
                }
              }).catch((error) => {
                console.error(error);
                //show error popup
                ToastAndroid.show('Canot register payment!' + error, ToastAndroid.SHORT);
              }).done();
  }

/**
  Save the content of the HTML page by updating the paymentId and checkout key
  Call MiaSDK start from Bridge
 */
  startLocalHost(paymentId, checkoutUrl) {
    const html = checkoutHTML.replace(/easy_payment_id/,paymentId);
    RNFS.writeFile(this.checkoutPage, html, 'utf8')
    .then((success) => {
        this.launchSDK(paymentId, checkoutUrl);
    })
    .catch((err) => {
      console.log(err.message);
      ToastAndroid.show(err.message, ToastAndroid.SHORT);
    });
  }

  /**
    Store the Promise to handle result, and launch SDK
    Call here your native modules from your bridge
   */
  launchSDK(paymentId, checkoutUrl){
    //set promise in bridge to handle result
    NativeModules.MiaSDK.handleSDKResult().then(()=>{
           this.cancelAuthorization(paymentId);
        }).catch((error) =>{
            ToastAndroid.show('Process canceled!', ToastAndroid.SHORT);
        });
    //launch SDK
     NativeModules.MiaSDK.startSDK(paymentId, checkoutUrl);
  }

  /**
    Call cancel payment to release the blocked amount
    For demo purposes, we won't charge the amount
  */
  cancelAuthorization(paymentId){
     fetch('https://test.api.dibspayment.eu/v1/payments/'+paymentId + '/cancels', {
              method: 'POST',
              headers: {
                'Content-Type': 'application/json',
                'Authorization': 'test-secret-key-f9b4a4d659e042e28f81bb187f990a39'
              },
            body: 	'{'+
                    '  "amount": 1000,'+
                    '  "orderItems": ['+
                    '    {'+
                    '      "reference": "MiASDK-Android-RN",'+
                    '      "name": "Lightning Cable",'+
                    '      "quantity": 1,'+
                    '      "unit": "pcs",'+
                    '      "unitPrice": 1000,'+
                    '      "taxRate": 0,'+
                    '      "taxAmount": 0,'+
                    '      "grossTotalAmount": 1000,'+
                    '      "netTotalAmount": 1000'+
                    '    }'+
                    '  ]'+
                    '}'
          }).then((response) => response.status)
          .then((status) => { 
                  if(status == 204){
                      ToastAndroid.show("Process is successful \n (Authorization canceled)", ToastAndroid.SHORT);
                  } else {
                      ToastAndroid.show('Error canceling payment!'+ error, ToastAndroid.LONG);
                  }
              }).catch((error) => {
                console.error(error);
                //show error popup
                ToastAndroid.show('Cannot cancel payment!' + error, ToastAndroid.LONG);
              }).done();
  }

}

const checkoutHTML = '<html>'+
                        '	<meta name="viewport" content="width=device-width, initial-scale=1">'+
                        '	<head>'+
                        '		<meta charset="UTF-8"> '+
                        '		<title>Webshop with Easy Payment</title> '+
                        '		<script src="https://test.checkout.dibspayment.eu/v1/checkout.js?v=1" type="text/javascript"></script> '+
                        '	</head> '+
                        '	<body>'+
                        '		<table><tr><td><img src="https://i.ibb.co/3ffKyw9/MiALogo.png" alt="MiALogo" border="0" width="60" height="45"></td><td> Demo app</td></tr></table>'+
                        '		<hr>'+
                        '		<div id="dibs-iframe">'+
                        '			<div id="dibs-complete-checkout"></div> '+ 
                        '				<script type="text/javascript">'+
                        '					var checkoutOptions = {'+
                        '						checkoutKey: "test-checkout-key-ae766b23a14044b09f0a56ae3c9a3ed4",'+
                        '						paymentId : "easy_payment_id", '+
                        '						language: "en-GB",'+
                        '						containerId : "dibs-complete-checkout", '+
                        '					};'+
                        '					var dibsCheckout = new Dibs.Checkout(checkoutOptions);'+
                        '					dibsCheckout.on(\'payment-completed\', function(response) {'+
                        '								  window.JSCallbackInterceptor.notifyPaymentCompleted();'+
                        '					});'+
                        '					console.log(checkoutOptions);'+
                        '				</script>'+
                        '		</div>'+
                        '	</body>'+
                      '</html>';

const termsHtml = '<html>'+
                  '	<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">'+
                  '	<body>'+
                  '		<div>'+
                  '			<h1>Terms and conditions:</h1>'+
                  '			<p>Here will be displayed your commercial terms and conditions: the terms and conditions you provided with the register request.</p>'+
                  '		</div>'+
                  '	</body>'+
                  '</html>'

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
   button: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 10,
    marginTop: 10
  }
});
