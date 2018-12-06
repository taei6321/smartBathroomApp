package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class SmartMirUse extends AppCompatActivity {

    Adapter_Mir adapter_mir;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_mir_use);

        viewPager = (ViewPager) findViewById(R.id.viewMir);
        adapter_mir = new Adapter_Mir(this);
        viewPager.setAdapter(adapter_mir);
    }
}
