package com.rgoel.meddrone

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar

class ShoppingFragment : Fragment() {

    private lateinit var webView2: WebView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_shopping, container, false)

        webView2 = view.findViewById(R.id.webView1)
        progressBar = view.findViewById(R.id.progressBar)
        webView2.webViewClient = WebViewClient()
        webView2.loadUrl("https://raghav0529.wixsite.com/meddrone-medconfig")

        return view
    }

    inner class WebViewClient : android.webkit.WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return false
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            progressBar.visibility = View.GONE
        }
    }
}