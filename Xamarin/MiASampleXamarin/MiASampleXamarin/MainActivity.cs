using System;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.Design.Widget;
using Android.Support.V7.App;
using Android.Views;
using Android.Widget;
using EU.Nets.Mia;
using EU.Nets.Mia.Data;
using MiASampleXamarin.http.response;

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

namespace MiASampleXamarin
{
    [Activity(Label = "@string/app_name", Theme = "@style/AppTheme.NoActionBar", MainLauncher = true)]
    public class MainActivity : AppCompatActivity
    {
        private String _paymentId = null;
        private String _hostedPaymentPageUrl = null;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);
            SetContentView(Resource.Layout.activity_main);

            Android.Support.V7.Widget.Toolbar toolbar = FindViewById<Android.Support.V7.Widget.Toolbar>(Resource.Id.toolbar);
            SetSupportActionBar(toolbar);

            Button btnPay = FindViewById<Button>(Resource.Id.btn_pay);
            btnPay.Click += OnPayClick;
        }

        public override bool OnCreateOptionsMenu(IMenu menu)
        {
            MenuInflater.Inflate(Resource.Menu.menu_main, menu);
            return true;
        }

        public override bool OnOptionsItemSelected(IMenuItem item)
        {
            int id = item.ItemId;
            if (id == Resource.Id.action_settings)
            {
                return true;
            }

            return base.OnOptionsItemSelected(item);
        }

        protected override void OnActivityResult(int requestCode, Result resultCode, Intent data)
        {
            base.OnActivityResult(requestCode, resultCode, data);

            if (requestCode == MiASDK.EasySdkRequestCode)
            {
                SampleLocalHost.getInstance().Stop(); //stop localhost
                if (resultCode == Result.Ok)
                {
                    MiAResult result = (MiAResult)data.GetParcelableExtra(MiASDK.BundleCompleteResult);
                    if (result.MiaResultCode == MiAResultCode.ResultPaymentCompleted)
                    {
                        _paymentId = null;
                        _hostedPaymentPageUrl = null;
                        Toast.MakeText(this, "Please check payment status to confirm if payment is successful or cancelled", ToastLength.Short).Show();
                    }
                    else
                    {
                        //clear payment id
                        _paymentId = null;
                        _hostedPaymentPageUrl = null;
                        Toast.MakeText(this, "ERROR", ToastLength.Short).Show();
                        //something went wrong
                    }
                }
                else
                {
                    //clear payment id
                    _paymentId = null;
                    _hostedPaymentPageUrl = null;
                    Toast.MakeText(this, "CANCELED", ToastLength.Short).Show();
                }
            }
        }

        private void OnPayClick(object sender, EventArgs eventArgs)
        {
            RegisterPayment();
        }

        private async void RegisterPayment()
        {
            //call register payment API
            RegisterPaymentResponse response = await APIService.getInstance().MakePostRequest<RegisterPaymentResponse>("v1/payments", GetRegisterPaymentBody());

            if (response != null && response.paymentId != null && response.hostedPaymentPageUrl != null)
            {
                //store locally the payment ID
                _paymentId = response.paymentId;
                _hostedPaymentPageUrl = response.hostedPaymentPageUrl;
                //start SDK
                MiASDK.Instance.StartSDK(this, new MiAPaymentInfo(response.paymentId, response.hostedPaymentPageUrl, "http://localhost:5500"));
            }
            else
            {
                //something went wrong
                Toast.MakeText(this, "Cannot create payment id", ToastLength.Short).Show();
            }
        }

        private async void ChargePayment()
        {
            String endpoint = "v1/payments/" + _paymentId + "/charges";
            ChargePaymentResponse response = await APIService.getInstance().MakePostRequest<ChargePaymentResponse>(endpoint, GetChargePaymentBody());
            //clear payment id
            _paymentId = null;
            if(response != null && response.chargeId != null)
            {
                Toast.MakeText(this, "Process is successful! (Authorization Charged)", ToastLength.Short).Show();
            }
            else
            {
                Toast.MakeText(this, "Error in charge payment!", ToastLength.Short).Show();
            }
        }

        private String GetRegisterPaymentBody()
        {
            return "{" +
                    "	\"checkout\": {" +
                    "		\"charge\": false," +
                    "		\"consumerType\": {" +
                    "			\"default\": \"B2C\"," +
                    "			\"supportedTypes\": [\"B2B\", \"B2C\"]" +
                    "		}," +
                    "		\"termsUrl\": \"http://localhost:6500\"," +
                    "		\"returnURL\": \"http://localhost:5500\"," +
                    "		\"integrationType\": \"HostedPaymentPage\"" +
                    "	}," +
                    "	\"order\": {" +
                    "		\"amount\": 1000," +
                    "		\"currency\": \"SEK\"," +
                    "		\"items\": [{" +
                    "			\"grossTotalAmount\": 1000," +
                    "			\"name\": \"Lightning Cable\"," +
                    "			\"quantity\": 1," +
                    "			\"reference\": \"MiASDK-Xamarin-Android\"," +
                    "			\"taxAmount\": 0," +
                    "			\"taxRate\": 0," +
                    "			\"unit\": \"pcs\"" +
                    "		}]," +
                    "		\"reference\": \"MiASDK-Xamarin-Android\"" +
                    "	}" +
                    "}";
        }

        private String GetChargePaymentBody()
        {
            return "{" +
                    "	\"amount\": 1000," +
                    "	\"orderItems\": [{" +
                    "		\"grossTotalAmount\": 1000," +
                    "		\"name\": \"Lightning Cable\"," +
                    "		\"quantity\": 1," +
                    "		\"reference\": \"MiASDK-Xamarin-Android\"," +
                    "		\"taxAmount\": 0," +
                    "		\"taxRate\": 0," +
                    "		\"unit\": \"pcs\"" +
                    "	}]" +
                    "}";
        }
	}




}

