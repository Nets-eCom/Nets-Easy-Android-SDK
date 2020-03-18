package eu.nets.miasample.utils

import eu.nets.miasample.network.APIManager


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
class SampleHtmlProvider {
    companion object {
        private const val CHECKOUT_KEY = "[easy_checkout_key]"
        private const val PAYMENT_ID_KEY = "[easy_payment_id]"
        private const val CHECKOUT_JS_KEY = "[checkout_js_url]"

        private const val checkoutHtml = "<html>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "            <title>Webshop with Easy Payment</title>\n" +
                "            <script src=\"$CHECKOUT_JS_KEY\" type=\"text/javascript\"></script>\n" +
                "    </head>\n " +
                "    <body>" +
                "        \n" +
                "        <h1>MiA Demo Webshop</h1>\n" +
                "        <hr>\n" +
                "        \n" +
                "        <div id=\"dibs-iframe\">\n" +
                "            <div id=\"dibs-complete-checkout\"></div>  <script type=\"text/javascript\">\n" +
                "                \n" +
                "                var checkoutOptions = {\n" +
                "                    checkoutKey: \"$CHECKOUT_KEY\",\n" +
                "                    paymentId : \"$PAYMENT_ID_KEY\", \n" +
                "                    language: \"en-GB\",\n" +
                "                    containerId : \"dibs-complete-checkout\",\n" +
                "                };\n" +
                "            \n" +
                "            var dibsCheckout = new Dibs.Checkout(checkoutOptions);\n" +
                "            dibsCheckout.on('payment-completed', function(response) {\n" +
                "               window.JSCallbackInterceptor.notifyPaymentCompleted();\n" +
                "             });\n" +
                "            console.log(checkoutOptions);\n" +
                "                </script>\n" +
                "        </div>\n" +
                "   </body>" +
                "   </html>"

        fun getCheckoutHtml(paymentId: String?): String {
            if (paymentId == null) return ""
            return checkoutHtml.replace(PAYMENT_ID_KEY, paymentId)
                    .replace(CHECKOUT_KEY, APIManager.checkoutKey)
                    .replace(CHECKOUT_JS_KEY, APIManager.checkoutJS)
        }

        fun getTermsHtml(): String {
            return "<html>\n" +
                    "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "\t<body>\n" +
                    "\t\t<div>\n" +
                    "\t\t\t<h1>Terms and conditions:</h1>\n" +
                    "\t\t\t<p>Here will be displayed your commercial terms and conditions: the terms and conditions you provided with the register request.</p>\n"+
                    "\t\t</div>\n" +
                    "\t</body>\n" +
                    "</html>"
        }
    }
}