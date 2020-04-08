﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Views;
using Android.Widget;
using FI.Iki.Elonen;

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
    class SampleLocalHost : NanoHTTPD
    {
        private static SampleLocalHost instance;
        public static String checkoutUrl = "http://localhost:5500";
        private String checkoutHtml = "";

        private SampleLocalHost(int port) : base(port)
        {

        }

        public static SampleLocalHost getInstance() { 
            if(instance == null)
            {
                instance = new SampleLocalHost(5500);
            }
            return instance;
        }

        public void startServer(String htmlPage)
        {
            try
            {
                this.checkoutHtml = htmlPage;
                Start(NanoHTTPD.SocketReadTimeout, false);
            } catch (Exception e)
            {
                //do nothing
            }
        }

        public override Response Serve(IHTTPSession session)
        {
            return NewFixedLengthResponse(checkoutHtml); 
        }
    }


}