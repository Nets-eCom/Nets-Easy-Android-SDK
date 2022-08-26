/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */


import React, {Component} from 'react';
import { Keyboard } from 'react-native'; 
import {TouchableWithoutFeedback, Image, StyleSheet, Text, TextInput, View, Button, TouchableOpacity, Alert} from 'react-native';
import EasyAPI from './EasyAPI';

import RNMia from 'react-native-rn-mia';

export default class App extends Component<Props> {

  constructor(props) {
    super(props);
    this.state = {price: "10.00"};
    this.api = new EasyAPI()
  }

  presentMiaCheckout(paymentID, paymentURL) {
    
    RNMia.checkoutWithPaymentID(paymentID, paymentURL, this.api.redirectURL, this.api.cancelURL,
    (error) => {
      alertTitle = error ? "Error" : "Payment Successful"
      Alert.alert(alertTitle, error["Error"], [{text:"Ok"}])
    }, 
    (cancellation) => {
      Alert.alert("Cancelled", "You have cancelled the payment", [{text:"Ok"}])
    });
    
  }

  checkoutWithMia(paymentRequestBody) {
    Keyboard.dismiss()

    this.api.createPaymentWithRequestBody(paymentRequestBody, (json) => {
      if (json.paymentId && json.hostedPaymentPageUrl) {
        this.presentMiaCheckout(json.paymentId, json.hostedPaymentPageUrl)
      } else {
        Alert.alert("Registration Error", JSON.stringify(json), [{text:"OK"}])
      }
    })
    
  }

  render() {
    return (
      <TouchableWithoutFeedback onPress={ () => { Keyboard.dismiss() } }>

        <View style={styles.container}>
              <Image
                style={styles.backgroundImage}
                source={require('./images/background.jpg')}
              />
              <Image
                style={styles.shopcardImage}
                source={require('./images/shopcard.png')}
              />
              <Text 
                style={{paddingTop: 20, color: 'purple'}}>
                Enter Price
              </Text>
              <TextInput
                style={{margin:20}}
                value={this.state.price}
                keyboardType={'numeric'}
                onChangeText={(price) => this.setState({price})}
              />
              <View style={{flex: 1, flexDirection: 'row'}}>
              <TouchableOpacity style={styles.button}
                onPress={
                  this.checkoutWithMia.bind(
                    this, 
                    this.api.makePaymentRequest(this.state.price)
                  )
                }>
                <Text style={{color: 'white', fontSize:20}}>Buy</Text>
              </TouchableOpacity>
              <TouchableOpacity style={styles.button}
                onPress={
                  this.checkoutWithMia.bind(
                    this, 
                    this.api.makeSubscriptionRequest(this.state.price)
                  )
                }>
                <Text style={{color: '#ffd700', fontSize:20}}>Subscribe</Text>
              </TouchableOpacity>
            </View>
        </View>

      </TouchableWithoutFeedback>

    );
  }
  

}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    backgroundColor: 'white'
  },
  backgroundImage: {
    width: '100%',
    height: undefined,
    aspectRatio: 1.5,
  },
  shopcardImage: {
    width: '80%',
    height: undefined,
    aspectRatio: 2,
  },
  button: {
    backgroundColor:"black", 
    justifyContent:'center', 
    alignItems:'center', 
    height:30, 
    marginLeft: 10, 
    width:"30%",
    borderRadius:6,
  },
});

