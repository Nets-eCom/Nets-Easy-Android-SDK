package eu.nets.mia.webview.base

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.FrameLayout
import junit.framework.TestCase.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*


/**
 *****Copyright (c) 2020 Nets Denmark A/S*****
 *
 * NETS DENMARK A/S, ("NETS"), FOR AND ON BEHALF OF ITSELF AND ITS SUBSIDIARIES AND AFFILIATES UNDER COMMON CONTROL,
 * IS WILLING TO LICENSE THE SOFTWARE TO YOU ONLY UPON THE CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED
 * IN THIS LICENSE AGREEMENT.
 * BY USING THE SOFTWARE YOU ACKNOWLEDGE THAT YOU HAVE READ THE TERMS AND AGREE TO THEM.
 * IF YOU ARE AGREEING TO THESE TERMS ON BEHALF OF A COMPANY OR OTHER LEGAL ENTITY,
 * YOU REPRESENT THAT YOU HAVE THE LEGAL AUTHORITY TO BIND THE LEGAL ENTITY TO THESE TERMS. IF YOU DO NOT HAVE SUCH AUTHORITY,
 * OR IF YOU DO NOT WISH TO BE BOUND BY THE TERMS, YOU MUST NOT USE THE SOFTWARE ON THIS SITE OR ANY OTHER MEDIA ON WHICH THE SOFTWARE IS CONTAINED.
 *
 * Software is copyrighted. Title to Software and all associated intellectual property rights is retained by NETS and/or its licensors.
 * Unless enforcement is prohibited by applicable law, you may not modify, decompile, or reverse engineer Software.
 *
 * No right, title or interest in or to any trademark, service mark, logo or trade name of NETS or its licensors is granted under this Agreement.
 *
 * Permission is hereby granted, to any person obtaining a copy of this software and associated documentation files (the Software"),
 * to deal in the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * Software may only be used for commercial or production purpose together with
 * Easy services (as per https://tech.dibspayment.com/easy) provided from NETS, its subsidiaries or affiliates under common control.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
class BaseWebViewPresenterTest {

    private lateinit var mView: BaseWebView
    private lateinit var mPresenter: BaseWebViewPresenterImplWrapper

    @Before
    fun before() {
        mView = mock(BaseWebView::class.java)
        mPresenter = spy(BaseWebViewPresenterImplWrapper(mView))
    }

    @Test
    fun testOnCreate() {
        val expected = false

        val bundle = mock(Bundle::class.java)

        mPresenter.baseView = mView

        mPresenter.onCreate(bundle)

        verify(mView).init(bundle)
        verify(mPresenter).enableBackNavigation(expected)
        verify(mPresenter).enableForwardNavigation(expected)
    }

    @Test
    fun testOnCreate_NullView() {
        val expected = false

        val bundle = mock(Bundle::class.java)
        val nullView: BaseWebView? = null

        mPresenter.baseView = nullView

        mPresenter.onCreate(bundle)

        //if view is null, null value is returned; otherwise Unit is returned
        assertNull(nullView?.init(bundle))
        verify(mPresenter).enableBackNavigation(expected)
        verify(mPresenter).enableForwardNavigation(expected)
    }

    @Test
    fun testConfigureWebView() {
        mPresenter.baseView = mView

        val parentWebView = mock(WebView::class.java)

        mPresenter.configureWebView(parentWebView)

        verify(mPresenter).applyWebViewSettings(parentWebView)
    }

    @Test
    fun testParentWebViewClient() {
        mPresenter.baseView = mView

        val childList = spy(ArrayList<WebView>())

        mPresenter.childList = childList

        val url = "someUrl"
        val popup = mock(WebView::class.java)
        val request = mock(WebResourceRequest::class.java)
        val uri = mock(Uri::class.java)
        `when`(request.url).thenReturn(uri)
        `when`(uri.toString()).thenReturn(url)

        mPresenter.parentWebClient.shouldOverrideUrlLoading(popup, request)

        assertEquals(url, request?.url.toString())

        mPresenter.parentWebClient.onPageStarted(popup, url, mock(Bitmap::class.java))

        verify(mView).showProgressView(true)

        mPresenter.parentWebClient.onPageFinished(popup, url)
        verify(mView).showProgressView(false)
        verify(popup).loadUrl(any(String::class.java))

    }

    @Test
    fun testParentWebViewClient_EasyHostedPage_PaymentSuccess() {
        mPresenter.baseView = mView

        val returnUrl = "http://returnUrl.com/success.php"
        val paymentId = "somePaymentIdString"

        val url = "$returnUrl?paymentId=$paymentId"


        `when`(mView.getRedirectUrl()).thenReturn(returnUrl)
        `when`(mView.getPaymentIdentifier()).thenReturn(paymentId)

        val shouldOverride = mPresenter.handleShouldOverrideUrlLoading(url)

        assertTrue(shouldOverride)
        verify(mView).sendOKResult()
    }

    @Test
    fun testParentWebViewClient_EasyHostedPage_NullRedirectUrl() {
        mPresenter.baseView = mView

        val returnUrl = "http://returnUrl.com/success.php"
        val paymentId = "somePaymentIdString"

        val url = "$returnUrl?paymentId=$paymentId"


        `when`(mView.getRedirectUrl()).thenReturn(null)
        `when`(mView.getPaymentIdentifier()).thenReturn(paymentId)

        val shouldOverride = mPresenter.handleShouldOverrideUrlLoading(url)

        assertFalse(shouldOverride)
        verify(mView, times(0)).sendOKResult()
    }

    @Test
    fun testParentWebViewClient_EasyHostedPage_NullUrl() {
        mPresenter.baseView = mView

        val shouldOverride = mPresenter.handleShouldOverrideUrlLoading(null)

        assertFalse(shouldOverride)
        verify(mView, times(0)).sendOKResult()
    }

    @Test
    fun testParentWebViewClient_EasyHostedPage_RedirectToOtherPage() {
        mPresenter.baseView = mView

        val returnUrl = "http://returnUrl.com/success.php"
        val paymentId = "somePaymentIdString"

        val url = "http://someUrlThatLoadsAutomatically.com"


        `when`(mView.getRedirectUrl()).thenReturn(returnUrl)
        `when`(mView.getPaymentIdentifier()).thenReturn(paymentId)

        val shouldOverride = mPresenter.handleShouldOverrideUrlLoading(url)

        assertFalse(shouldOverride)
        verify(mView, times(0)).sendOKResult()
    }


    @Test
    fun testParentWebViewClient_NullValues() {
        val nullView: BaseWebView? = null
        mPresenter.baseView = nullView

        val childList = spy(ArrayList<WebView>())

        mPresenter.childList = childList

        val url = "someUrl"
        val popup: WebView? = null
        val request: WebResourceRequest? = null

        mPresenter.parentWebClient.shouldOverrideUrlLoading(popup, request)

        assertEquals("null", request?.url.toString())

        mPresenter.parentWebClient.onPageStarted(popup, url, mock(Bitmap::class.java))

        assertNull(nullView?.showProgressView(true))

        mPresenter.parentWebClient.onPageFinished(popup, url)

        assertNull(nullView?.showProgressView(false))
        assertNull(popup?.loadUrl(any(String::class.java)))
    }

    @Test
    fun testChildWebViewClient_ShouldOverrideUrl_Valid_RandomPage() {
        mPresenter.baseView = mView

        val url = "someUrl"
        val popup = mock(WebView::class.java)
        val request = mock(WebResourceRequest::class.java)
        val uri = mock(Uri::class.java)
        `when`(request.url).thenReturn(uri)
        `when`(uri.toString()).thenReturn(url)

        assertFalse(mPresenter.childWebClient.shouldOverrideUrlLoading(popup, request))
        assertEquals(url, request?.url.toString())
    }
//  Commenting out the below test cases since we are not using shouldOverrideUrlLoading()
   /* @Test
    fun testChildWebViewClient_ShouldOverrideUrl_Valid_PDF() {
        mPresenter.baseView = mView

        val url = "someUrl.pdf"
        val popup = mock(WebView::class.java)
        val request = mock(WebResourceRequest::class.java)
        val uri = mock(Uri::class.java)
        `when`(request.url).thenReturn(uri)
        `when`(uri.toString()).thenReturn(url)

        assertTrue(mPresenter.childWebClient.shouldOverrideUrlLoading(popup, request))
        verify(popup).loadUrl("${mPresenter.onlinePDFView}${request?.url?.toString()}")
    }

    @Test
    fun testChildWebViewClient_ShouldOverrideUrl_Invalid() {
        mPresenter.baseView = mView

        val url: String? = null
        val popup = mock(WebView::class.java)
        val request = mock(WebResourceRequest::class.java)
        val uri = mock(Uri::class.java)
        `when`(request.url).thenReturn(uri)
        `when`(uri.toString()).thenReturn(url)

        assertFalse(mPresenter.childWebClient.shouldOverrideUrlLoading(popup, request))
    }

    @Test
    fun testChildWebViewClient_ShouldOverrideUrl_NullValues_PDF() {
        mPresenter.baseView = mView

        val popup: WebView? = null
        val request: WebResourceRequest? = null

        assertFalse(mPresenter.childWebClient.shouldOverrideUrlLoading(popup, request))
    }

    @Test
    fun testChildWebViewClient_ShouldOverrideUrl_NullWebView_PDF() {
        mPresenter.baseView = mView

        val url = "someUrl.pdf"
        val popup: WebView? = null
        val request = mock(WebResourceRequest::class.java)
        val uri = mock(Uri::class.java)
        `when`(request.url).thenReturn(uri)
        `when`(uri.toString()).thenReturn(url)

        assertTrue(mPresenter.childWebClient.shouldOverrideUrlLoading(popup, request))
        assertNull(popup?.loadUrl("${mPresenter.onlinePDFView}${request?.url?.toString()}"))
    }
*/
    @Test
    fun testChildWebViewClient_OnPageStarted() {
        mPresenter.baseView = mView

        val url = "someUrl"
        val popup = mock(WebView::class.java)

        mPresenter.childWebClient.onPageStarted(popup, url, mock(Bitmap::class.java))

        verify(mView).showProgressView(true)
    }

    @Test
    fun testChildWebViewClient_OnPageStarted_NullWebView() {
        val nullView: BaseWebView? = null
        mPresenter.baseView = nullView

        val url = "someUrl"
        val popup = mock(WebView::class.java)

        mPresenter.childWebClient.onPageStarted(popup, url, mock(Bitmap::class.java))

        assertNull(nullView?.showProgressView(true))
    }

    @Test
    fun testChildWebViewClient_OnPageFinished() {
        mPresenter.baseView = mView

        val url = "someUrl"
        val popup = mock(WebView::class.java)

        mPresenter.childWebClient.onPageFinished(popup, url)

        verify(mView).showProgressView(false)
        verify(popup).loadUrl(any(String::class.java))
    }


    @Test
    fun testChildWebViewClient_OnPageFinished_NullWebView() {
        mPresenter.baseView = mView

        val url = "someUrl"
        val popup: WebView? = null

        mPresenter.childWebClient.onPageFinished(popup, url)

        assertNull(popup?.loadUrl(any(String::class.java)))
    }

    @Test
    fun testChildWebViewClient_OnPageFinished_NullView() {
        val nullView: BaseWebView? = null
        mPresenter.baseView = nullView

        val url = "someUrl"
        val popup = mock(WebView::class.java)

        mPresenter.childWebClient.onPageFinished(popup, url)

        assertNull(nullView?.showProgressView(false))
    }

    @Test
    fun testConfigureWebView_NullWebView() {
        mPresenter.baseView = mView

        mPresenter.configureWebView(null)

        verify(mPresenter).applyWebViewSettings(null)
    }

    @Test
    fun testCreateChildWebView_NullRoot() {
        `when`(mView.getRootFrame()).thenReturn(null)
        mPresenter.mView = mView

        assertNull(mPresenter.createChildWebView())
    }

    @Test
    fun testCreateChildWebView_NullView() {
        val nullView: BaseWebView? = null
        mPresenter.mView = null

        `when`(mPresenter.applyWebViewSettings(null)).thenReturn(mock(WebView::class.java))
        assertNull(mPresenter.createChildWebView())
        assertNull(nullView?.getRootFrame())
    }

    @Test
    fun testOnBackPressed_ParentWebView() {
        mPresenter.mView = mView

        val parentWebView = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())

        mPresenter.childList = childList
        mPresenter.mParentWebview = parentWebView

        mPresenter.onBackPressed()

        verify(mPresenter).removeLatestWebView()
        verify(mView).showProgressView(false)
        verify(mPresenter).handleNavBarActionIcon(ArgumentMatchers.any(Int::class.java))
        verify(mPresenter).notifyNavigationEnabled()
    }

    @Test
    fun testOnBackPressed_ParentWebView_NullView() {
        val nullView: BaseWebView? = null
        mPresenter.mView = nullView

        val parentWebView = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())

        mPresenter.childList = childList
        mPresenter.mParentWebview = parentWebView

        mPresenter.onBackPressed()

        verify(mPresenter).removeLatestWebView()
        //if view is null, null value is returned; otherwise Unit is returned
        assertNull(nullView?.showProgressView(false))
        verify(mPresenter).handleNavBarActionIcon(ArgumentMatchers.any(Int::class.java))
        verify(mPresenter).notifyNavigationEnabled()
    }

    @Test
    fun testOnBackPressed_ChildWebView() {
        mPresenter.mView = mView

        val parentWebView = mock(WebView::class.java)
        val childWebView = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())
        childList.add(childWebView)

        val rootFrame = mock(FrameLayout::class.java)
        `when`(mView.getRootFrame()).thenReturn(rootFrame)

        mPresenter.childList = childList
        mPresenter.mParentWebview = parentWebView

        mPresenter.onBackPressed()

        verify(mPresenter).removeLatestWebView()
        verify(mView).showProgressView(false)
        verify(rootFrame).removeView(childWebView)
        verify(childWebView).destroy()
        verify(childList).remove(childWebView)
        verify(mPresenter).handleNavBarActionIcon(ArgumentMatchers.any(Int::class.java))
        verify(mPresenter).notifyNavigationEnabled()
    }

    @Test
    fun testOnBackPressed_ChildWebView_NullView() {
        val nullView: BaseWebView? = null
        mPresenter.mView = nullView

        val parentWebView = mock(WebView::class.java)
        val childWebView = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())
        childList.add(childWebView)


        mPresenter.childList = childList
        mPresenter.mParentWebview = parentWebView

        mPresenter.onBackPressed()

        verify(mPresenter).removeLatestWebView()
        //if view is null, null value is returned; otherwise Unit is returned
        assertNull(nullView?.showProgressView(false))
        assertNull(nullView?.getRootFrame()?.removeView(childWebView))
        verify(childWebView).destroy()
        verify(childList).remove(childWebView)
        verify(mPresenter).handleNavBarActionIcon(ArgumentMatchers.any(Int::class.java))
        verify(mPresenter).notifyNavigationEnabled()
    }

    @Test
    fun testOnNavigateBack_ParentWebView_CanNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val parentWebView = mock(WebView::class.java)

        `when`(parentWebView.canGoBack()).thenReturn(true)

        mPresenter.mParentWebview = parentWebView
        mPresenter.childList = childList

        mPresenter.onNavigateBack()

        verify(parentWebView).goBack()
    }

    @Test
    fun testOnNavigateBack_ParentWebView_CannotNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val parentWebView = mock(WebView::class.java)

        `when`(parentWebView.canGoBack()).thenReturn(false)

        mPresenter.mParentWebview = parentWebView
        mPresenter.childList = childList

        mPresenter.onNavigateBack()

        verify(parentWebView, times(0)).goBack()
    }

    @Test
    fun testOnNavigateBack_ChildWebView_CanNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val childWebView = mock(WebView::class.java)

        `when`(childWebView.canGoBack()).thenReturn(true)

        childList.add(childWebView)
        mPresenter.childList = childList

        mPresenter.onNavigateBack()

        verify(childWebView).goBack()
    }

    @Test
    fun testOnNavigateBack_ChildWebView_CannotNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val childWebView = mock(WebView::class.java)

        `when`(childWebView.canGoBack()).thenReturn(false)

        childList.add(childWebView)
        mPresenter.childList = childList

        mPresenter.onNavigateBack()

        verify(childWebView, times(0)).goBack()
    }

    @Test
    fun testOnNavigateForward_ParentWebView_CanNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val parentWebView = mock(WebView::class.java)

        `when`(parentWebView.canGoForward()).thenReturn(true)

        mPresenter.mParentWebview = parentWebView
        mPresenter.childList = childList

        mPresenter.onNavigateForward()

        verify(parentWebView).goForward()
    }

    @Test
    fun testOnNavigateForward_ParentWebView_CannotNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val parentWebView = mock(WebView::class.java)

        `when`(parentWebView.canGoForward()).thenReturn(false)

        mPresenter.mParentWebview = parentWebView
        mPresenter.childList = childList

        mPresenter.onNavigateForward()

        verify(parentWebView, times(0)).goForward()
    }

    @Test
    fun testOnNavigateForward_NullParentWebView() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val parentWebView: WebView? = null

        mPresenter.mParentWebview = parentWebView
        mPresenter.childList = childList

        mPresenter.onNavigateForward()

        assertFalse(parentWebView?.canGoForward() == true)
    }

    @Test
    fun testOnNavigateForward_ChildWebView_CanNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val childWebView = mock(WebView::class.java)

        `when`(childWebView.canGoForward()).thenReturn(true)

        childList.add(childWebView)
        mPresenter.childList = childList

        mPresenter.onNavigateForward()

        verify(childWebView).goForward()
    }

    @Test
    fun testOnNavigateForward_ChildWebView_CannotNavigate() {
        mPresenter.mView = mView

        val childList = spy(ArrayList<WebView>())
        val childWebView = mock(WebView::class.java)

        `when`(childWebView.canGoForward()).thenReturn(false)

        childList.add(childWebView)
        mPresenter.childList = childList

        mPresenter.onNavigateForward()

        verify(childWebView, times(0)).goForward()
    }

    @Test
    fun testEnableBackNavigation_Enable() {
        val expected = true

        mPresenter.mView = mView

        mPresenter.enableBackNavigation(expected)

        verify(mView).enableBackNavigation(expected)
    }

    @Test
    fun testEnableBackNavigation_Enable_NullView() {
        val expected = false

        val baseView: BaseWebView? = null
        mPresenter.mView = baseView

        mPresenter.enableBackNavigation(expected)

        verify(mView, times(0)).enableBackNavigation(expected)
    }

    @Test
    fun testEnableBackNavigation_Disable() {
        val expected = false

        mPresenter.mView = mView

        mPresenter.enableBackNavigation(expected)

        verify(mView).enableBackNavigation(expected)
    }

    @Test
    fun testEnableBackNavigation_Disable_NullView() {
        val expected = false

        val baseView: BaseWebView? = null
        mPresenter.mView = baseView

        mPresenter.enableBackNavigation(expected)

        verify(mView, times(0)).enableBackNavigation(expected)
    }

    @Test
    fun testEnableForwardNavigation_Enable() {
        val expected = true

        mPresenter.mView = mView

        mPresenter.enableForwardNavigation(expected)

        verify(mView).enableForwardNavigation(expected)
    }

    @Test
    fun testEnableForwardNavigation_Enable_NullView() {
        val expected = true

        val baseView: BaseWebView? = null
        mPresenter.mView = baseView

        mPresenter.enableForwardNavigation(expected)

        verify(mView, times(0)).enableForwardNavigation(expected)
    }

    @Test
    fun testEnableForwardNavigation_Disable() {
        val expected = false

        mPresenter.mView = mView

        mPresenter.enableForwardNavigation(expected)

        verify(mView).enableForwardNavigation(expected)
    }

    @Test
    fun testEnableForwardNavigation_Disable_NullView() {
        val expected = false

        val baseView: BaseWebView? = null
        mPresenter.mView = baseView

        mPresenter.enableForwardNavigation(expected)

        verify(mView, times(0)).enableForwardNavigation(expected)
    }

    @Test
    fun testGetLatestWebView_NullChild() {
        val childList = spy(ArrayList<WebView>())
        mPresenter.childList = childList

        assertNull(mPresenter.getLatestWebView())
    }

    @Test
    fun testGetLatestWebView_ValidChild() {
        val childList = spy(ArrayList<WebView>())
        val childWebView = mock(WebView::class.java)
        childList.add(childWebView)
        mPresenter.childList = childList

        assertEquals(childWebView, mPresenter.getLatestWebView())
    }

    @Test
    fun testPauseTimers() {
        val parentWebView = mock(WebView::class.java)
        mPresenter.mParentWebview = parentWebView

        mPresenter.pauseTimers()

        verify(parentWebView).pauseTimers()
    }

    @Test
    fun testPauseTimers_NullParent() {
        val parentWebView = mock(WebView::class.java)
        mPresenter.mParentWebview = null

        mPresenter.pauseTimers()

        verify(parentWebView, times(0)).pauseTimers()
    }

    @Test
    fun testResumeTimers() {
        val parentWebView = mock(WebView::class.java)
        mPresenter.mParentWebview = parentWebView

        mPresenter.resumeTimers()

        verify(parentWebView).resumeTimers()
    }

    @Test
    fun testResumeTimers_NullParent() {
        val parentWebView = mock(WebView::class.java)
        mPresenter.mParentWebview = null

        mPresenter.resumeTimers()

        verify(parentWebView, times(0)).resumeTimers()
    }


    @Test
    fun testNotifyNavigationEnabled_ChildWebView_CanNavigate() {

        val expected = true

        mPresenter.baseView = mView

        val childWebView = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())
        childList.add(childWebView)
        `when`(childWebView.canGoForward()).thenReturn(expected)
        `when`(childWebView.canGoBack()).thenReturn(expected)
        mPresenter.childList = childList

        mPresenter.notifyNavigationEnabled()

        verify(mPresenter).enableBackNavigation(expected)
        verify(mPresenter).enableForwardNavigation(expected)
    }

    @Test
    fun testNotifyNavigationEnabled_ChildWebView_CannotNavigate() {

        val expected = false

        mPresenter.baseView = mView

        val childWebView = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())
        childList.add(childWebView)
        `when`(childWebView.canGoForward()).thenReturn(expected)
        `when`(childWebView.canGoBack()).thenReturn(expected)
        mPresenter.childList = childList

        mPresenter.notifyNavigationEnabled()

        verify(mPresenter).enableBackNavigation(expected)
        verify(mPresenter).enableForwardNavigation(expected)
    }

    @Test
    fun testNotifyNavigationEnabled_ParentWebView() {

        val expected = false

        mPresenter.baseView = mView

        val childList = spy(ArrayList<WebView>())
        mPresenter.childList = childList

        mPresenter.notifyNavigationEnabled()

        verify(mPresenter).enableBackNavigation(expected)
        verify(mPresenter).enableForwardNavigation(expected)
    }

    @Test
    fun testRemoveLatestWebView_MultipleChilds() {
        `when`(mView.getRootFrame()).thenReturn(null)
        mPresenter.baseView = mView

        val child1 = mock(WebView::class.java)
        val child2 = mock(WebView::class.java)
        val childList = spy(ArrayList<WebView>())
        childList.add(child1)
        childList.add(child2)
        mPresenter.childList = childList

        mPresenter.removeLatestWebView()

        assertNull(mView.getRootFrame()?.removeView(child2))
        verify(mPresenter, times(0)).handleNavBarActionIcon(any(Int::class.java))
    }

    @Test
    fun testOnPageFinished_NoError() {
        mPresenter.baseView = mView

        val urlLoaded = "http://someUrl.com"

        mPresenter.handlePageFinished(urlLoaded)

        verify(mView, times(0)).sendCancelResult()
    }

    @Test
    fun testOnPageFinished_UserNotAuthenticated() {
        mPresenter.baseView = mView

        val urlLoaded = "http://someUrl.com?authenticated=false"

        mPresenter.handlePageFinished(urlLoaded)

        verify(mView, times(0)).sendCancelResult()

        mPresenter.handlePageFinished(urlLoaded)

        verify(mView).sendCancelResult()
    }

    @Test
    fun testOnPageFinished_NullView() {
        val nullView: BaseWebView? = null
        mPresenter.baseView = nullView

        val urlLoaded = "http://someUrl.com?authenticated=false"

        mPresenter.handlePageFinished(urlLoaded)

        verify(mView, times(0)).sendCancelResult()

        mPresenter.handlePageFinished(urlLoaded)

        assertNull(nullView?.sendCancelResult())
    }

    @Test
    fun testOnPageFinished_NullUrl() {
        mPresenter.baseView = mView

        var urlLoaded: String? = null

        mPresenter.handlePageFinished(urlLoaded)

        verify(mView, times(0)).sendCancelResult()

        urlLoaded = "http://someUrl.com?authenticated=false"

        mPresenter.handlePageFinished(urlLoaded)

        verify(mView, times(0)).sendCancelResult()
    }

    @Test
    fun testHandleNavBarActionText() {
        mPresenter.baseView = mView
        val resId = 100
        mPresenter.handleNavBarActionIcon(resId)

        verify(mView).handleNavBarActionText(resId)
    }

    @Test
    fun testOnResume() {
        mPresenter.baseView = mView

        val newView = mock(BaseWebView::class.java)

        mPresenter.onResume(newView)
        assertEquals(newView, mPresenter.baseView)
        verify(mPresenter).resumeTimers()
    }

    @Test
    fun testOnStop() {
        mPresenter.baseView = mView

        mPresenter.onStop()

        assertNull(mPresenter.baseView)
    }

    @Test
    fun testOnPause() {
        mPresenter.baseView = mView

        mPresenter.onPause()

        verify(mPresenter).pauseTimers()
    }

    @Test
    fun testOnPaymentCompleted() {
        mPresenter.baseView = mView

        mPresenter.onPaymentCompleted()

        verify(mView).sendOKResult()
    }

    @Test
    fun testOnPaymentCompleted_NullView() {
        val view: BaseWebView? = null
        mPresenter.baseView = view

        mPresenter.onPaymentCompleted()

        assertNull(view?.sendOKResult())
    }
}