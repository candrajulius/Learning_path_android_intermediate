package com.candra.latihanwebview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Inisasi webview
        findViewById<WebView>(R.id.webview).apply {
            settings.javaScriptEnabled = true

            // Tambahkan sebuah webviewChrome Client
            // Ambil webViewClient
            webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView, url: String) {
                    // Ketika Page selesai dijalankan maka muncul sebuah alert
                    super.onPageFinished(view, url)
                    // Alert dari javascript
                    view.loadUrl("javascript:alert('Web Dicoding berhasil dimuat')")
                }
            }
            // Menambahkan WebChromeClient
            webChromeClient = object: WebChromeClient(){

                // Ambil JSOn Alert
                override fun onJsAlert(
                    view: WebView?,
                    url: String?,
                    message: String?,
                    result: JsResult?
                ): Boolean {

                    // Menampilkan sebuah pesan ke android
                    message?.let { setToast(it) }
                    // Mengconvirmasi hasil dari pesan
                    result?.confirm()

                    return true
                }
            }

            // Meloadingkan url 
            loadUrl("https://www.dicoding.com")
        }
    }

    private fun setToast(message: String){
        Toast.makeText(this@MainActivity,message,Toast.LENGTH_SHORT).show()
    }
}