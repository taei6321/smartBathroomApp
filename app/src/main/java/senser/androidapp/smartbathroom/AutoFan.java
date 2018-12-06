package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class AutoFan extends BaseActivity {

    Switch onoffSwitch;
    CheckBox autoUse;
    TextView onFan, offFan;
    ImageView fan;
    ImageButton onUpHum, onDownHum, offUpHum, offDownHum;
    public static int onHum = 0;
    public static int offHum = 0;

    int onoff = 0;
    int ischecked = 0;

    private static DeviceConnector connector;
    private static SmartMir smartMir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_fan);

        smartMir = new SmartMir();

        // 회전 시킬 이미지
        fan = (ImageView) findViewById(R.id.fan);

        // 환풍기 수동으로 껐다 켰다 할 수 있는 스위치
        onoffSwitch = (Switch) findViewById(R.id.onoffSwitch);
        onoffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(onoffSwitch.isChecked()){
                    // 환풍기를 켰을 때
                    Toast.makeText(getBaseContext(), "환풍기 ON", Toast.LENGTH_SHORT).show();
                    // 환풍기 켰을 때 환풍기 회전시키는 애니메이션
                    Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_anim);
                    fan.startAnimation(anim);

                    onoff = 1;
                    smartMir.fanOnOff(onoff);
                    /*byte[] bytes = Utils.toHex(onoff);
                    connector.write(bytes);*/

                }
                else{
                    // 환풍기를 껐을 때
                    Toast.makeText(getBaseContext(), "환풍기 OFF", Toast.LENGTH_SHORT).show();
                    // 환풍기 애니메이션 중지
                    fan.clearAnimation();
                    onoff = 2;
                    smartMir.fanOnOff(onoff);
            }
            }
        });

        // 환풍기 습도를 설정하면 자동으로 환풍기가 돌아가게 하는 체크박스
        autoUse = (CheckBox) findViewById(R.id.autoUse);
        autoUse.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (autoUse.isChecked()){
                    // 자동 설정 되있을 때
                    Toast.makeText(getBaseContext(), "자동 환풍 설정", Toast.LENGTH_SHORT).show();
                    ischecked = 1;
                    smartMir.fanAuto(ischecked, onHum, offHum);
                }
                else{
                    // 자동 설정 안되어 있을 때
                    Toast.makeText(getBaseContext(), "자동 환풍 해제", Toast.LENGTH_SHORT).show();
                    ischecked = 2;
                    smartMir.fanAuto(ischecked, onHum, offHum);
                }
            }
        });

        // 환풍기를 켤 습도량과 끌 습도량
        onFan = (TextView) findViewById(R.id.onFan);
        onHum = 50;
        onFan.setText(Integer.toString(onHum));
        offFan = (TextView) findViewById(R.id.offFan);
        offHum = 20;
        offFan.setText(Integer.toString(offHum));

        // 환풍기 켤 습도량 up,down 화살표로 조절
        onUpHum = (ImageButton) findViewById(R.id.onUpHum);
        onUpHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHum++;
                onFan.setText(Integer.toString(onHum));
            }
        });
        onDownHum = (ImageButton) findViewById(R.id.onDownHum);
        onDownHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHum--;
                onFan.setText(Integer.toString(onHum));
            }
        });

        // 환풍기 끌 습도량 up, down 화살표로 조절
        offUpHum = (ImageButton) findViewById(R.id.offUpHum);
        offUpHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offHum++;
                offFan.setText(Integer.toString(offHum));
            }
        });
        offDownHum = (ImageButton) findViewById(R.id.offDownHum);
        offDownHum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offHum--;
                offFan.setText(Integer.toString(offHum));
            }
        });

    }
}
