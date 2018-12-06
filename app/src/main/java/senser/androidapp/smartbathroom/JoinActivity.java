package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class JoinActivity extends AppCompatActivity {

    WebView joinWeb;
    WebSettings webSettings;
    String joinUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        joinUrl = "http://192.168.0.52:8080/smartBathroom/join/join.jsp";

        joinWeb = (WebView) findViewById(R.id.joinWeb);
        joinWeb.setWebViewClient(new WebViewClient()); // 클릭 시 새창 안뜨게
        webSettings = joinWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);

        joinWeb.loadUrl(joinUrl);
    }
}
