package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class AutoFanUse extends AppCompatActivity {

    Adapter_Fan adapter_fan;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_fan_use);

        viewPager = (ViewPager) findViewById(R.id.viewfan);
        adapter_fan = new Adapter_Fan(this);
        viewPager.setAdapter(adapter_fan);
    }
}
