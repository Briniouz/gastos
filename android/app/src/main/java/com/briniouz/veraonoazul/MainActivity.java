package com.briniouz.veraonoazul;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/** Abre o Verão no Azul (index.html dentro do APK) numa WebView com os dados salvos no aparelho. */
public class MainActivity extends Activity {
    private static final int ESCOLHER_ARQUIVO = 1;
    private WebView web;
    private ValueCallback<Uri[]> arquivoCb;

    @Override
    protected void onCreate(Bundle salvo) {
        super.onCreate(salvo);
        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(true);
        s.setTextZoom(100);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Uri u = r.getUrl();
                if ("file".equals(u.getScheme())) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (arquivoCb != null) arquivoCb.onReceiveValue(null);
                arquivoCb = cb;
                try {
                    startActivityForResult(p.createIntent(), ESCOLHER_ARQUIVO);
                } catch (Exception e) {
                    arquivoCb = null;
                    return false;
                }
                return true;
            }
        });

        if (salvo != null) web.restoreState(salvo);
        else web.loadUrl("file:///android_asset/index.html");
    }

    @Override
    protected void onActivityResult(int pedido, int resultado, Intent dados) {
        if (pedido == ESCOLHER_ARQUIVO && arquivoCb != null) {
            arquivoCb.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultado, dados));
            arquivoCb = null;
        } else super.onActivityResult(pedido, resultado, dados);
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
