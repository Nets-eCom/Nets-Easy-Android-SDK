package eu.nets.miasample.utils

import fi.iki.elonen.NanoHTTPD


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
class SampleLocalHost private constructor(port: Int) : NanoHTTPD(port) {

    private var checkoutPage: String = ""

    companion object {
        private val checkoutInstance = SampleLocalHost(5500)
        private val termsPageInstance = TermsAndConditionsPage(6500)

        const val CHECKOUT_URL = "http://localhost:5500"
        const val TERMS_URL = "http://localhost:6500"

        @Synchronized
        fun getInstance(): SampleLocalHost {
            return checkoutInstance
        }
    }

    /**
     * Starts the localhost server to host the checkout and terms pages
     * It will use SocketServer on ports: 5500, 6500
     * The pages content will be HTML String from [SampleHtmlProvider] class
     */
    fun startServer(checkoutPage: String) {
        try {
            this.checkoutPage = checkoutPage
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
            termsPageInstance.startServer()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * When the payment process is finished, the servers will be closed, to minimize memory consumption
     */
    fun closeServer() {
        stop()
        termsPageInstance.closeServer()
    }

    /**
     * The html content will be loaded in the /index.html page of the server
     */
    override fun serve(session: IHTTPSession?): Response {
        return newFixedLengthResponse(checkoutPage)
    }
}

private class TermsAndConditionsPage constructor(port: Int) : NanoHTTPD(port) {

    private var termsPage: String = SampleHtmlProvider.getTermsHtml()

    /**
     * Starts the localhost server to host the checkout and terms pages
     * It will use SocketServer on ports: 5500, 6500
     * The pages content will be HTML String from [SampleHtmlProvider] class
     */
    fun startServer() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * When the payment process is finished, the servers will be closed, to minimize memory consumption
     */
    fun closeServer() {
        stop()
    }

    /**
     * When the payment process is finished, the servers will be closed, to minimize memory consumption
     */
    override fun serve(session: IHTTPSession?): Response {
        return newFixedLengthResponse(termsPage)
    }
}
