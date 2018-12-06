package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class DigitalShowerHeadUse extends AppCompatActivity {

    Adapter_Digital adapter_digital;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_shower_head_use);

        viewPager = (ViewPager) findViewById(R.id.viewDigital);
        adapter_digital = new Adapter_Digital(this);
        viewPager.setAdapter(adapter_digital);

    }
}
