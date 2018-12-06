package senser.androidapp.smartbathroom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TabHost;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class WebBrowser extends AppCompatActivity {

    WebView noticeWeb, contactUsWeb, reviewWeb;
    WebSettings webSettings;
    String noticeUrl, contactUsUrl, reviewUrl;

    ImageButton returnBtn;

    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_browser);

        noticeUrl = "http://192.168.0.52:8080/smartBathroom/notice/notice.jsp";
        contactUsUrl = "http://192.168.0.52:8080/smartBathroom/contactUs/contactUs.jsp";
        reviewUrl = "http://192.168.0.52:8080/smartBathroom/review/review.jsp";

        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("1").setContent(R.id.tab1).setIndicator("공지사항");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("2").setContent(R.id.tab2).setIndicator("고객문의");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("3").setContent(R.id.tab3).setIndicator("후기");

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);

        for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
        {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#000000"));
        }

        init();

        noticeWeb = (WebView) findViewById(R.id.noticeWeb);
        noticeWeb.setWebViewClient(new WebViewClient());
        webSettings = noticeWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        noticeWeb.loadUrl(noticeUrl);

        tabHost.setCurrentTab(0);
    }

    void init(){

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                switch (tabId){
                    case "1":
                        noticeWeb = (WebView) findViewById(R.id.noticeWeb);
                        noticeWeb.setWebViewClient(new WebViewClient()); // 클릭 시 새창 안뜨게
                        webSettings = noticeWeb.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        noticeWeb.loadUrl(noticeUrl);
                        break;
                    case "2":
                        contactUsWeb = (WebView) findViewById(R.id.contactUsWeb);
                        contactUsWeb.setWebViewClient(new WebViewClient());
                        webSettings = contactUsWeb.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        contactUsWeb.loadUrl(contactUsUrl);
                        break;
                    case "3":
                        reviewWeb = (WebView) findViewById(R.id.reviewWeb);
                        reviewWeb.setWebViewClient(new WebViewClient());
                        webSettings = reviewWeb.getSettings();
                        webSettings.setJavaScriptEnabled(true);

                        reviewWeb.loadUrl(reviewUrl);
                        break;
                }

            }
        });

    }
}
