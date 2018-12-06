package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class SmartBathUse extends AppCompatActivity {

    Adapter_Bath adapter_bath;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_bath_use);

        viewPager = (ViewPager) findViewById(R.id.viewBath);
        adapter_bath = new Adapter_Bath(this);
        viewPager.setAdapter(adapter_bath);
    }
}
