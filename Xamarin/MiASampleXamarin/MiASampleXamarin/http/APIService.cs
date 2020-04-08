using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using Newtonsoft.Json;

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

    public class APIService 
    {
        private HttpClient _httpClient;
        private static APIService _instance;
		public static String checkoutKey = "YOUR_CHECKOUT_KEY";
		public static String secretKey = "YOUR_SECRET_KEY";

        private APIService()
        {
            _httpClient = new HttpClient();
            _httpClient.BaseAddress = new Uri("https://test.api.dibspayment.eu/");
            _httpClient.DefaultRequestHeaders.Add("Authorization", secretKey);
        }

        public static APIService getInstance()
        {
            if(_instance == null)
            {
                _instance = new APIService();
            }

            return _instance;
        }

        public async Task<T> MakePostRequest<T>(String endpoint, String body)
        {
            try
            {
                var request = new StringContent(body, Encoding.UTF8, "application/json");

                var response = await _httpClient.PostAsync(endpoint,request);
                if (response.IsSuccessStatusCode)
                {
                    var responseString = await response.Content.ReadAsStringAsync();
                    var model = JsonConvert.DeserializeObject<T>(responseString);
                    return model;
                }
                else
                {
                    return default(T);
                }
            }
            catch (Exception e)
            {
                return default(T);
            }
        }

    }
}