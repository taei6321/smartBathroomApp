package senser.androidapp.smartbathroom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SmartBath extends AppCompatActivity {

    TextView tempText;
    ImageView waterHeight;
    ImageButton upTemp, downTemp, fullWater, normalWater, slightlyWater, lessWater, startWaterBtn, stopWaterBtn;
    LinearLayout.LayoutParams mLayoutParams;
    public static int TEMP = 0;
    int height = 0; // 욕조 물 높이 체크 몇을 보낼지 결정
    int startstop = 0; // 욕조 물 받기 시작, 취소
    SmartMir smartMir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_bath);

        smartMir = new SmartMir();

        // 설정 온도를 보여준다
        tempText = (TextView) findViewById(R.id.tempText);
        TEMP = 25;
        tempText.setText(Integer.toString(TEMP));

        // 설정 온도를 높인다
        upTemp = (ImageButton) findViewById(R.id.upTemp);
        upTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TEMP++;
                tempText.setText(Integer.toString(TEMP));
            }
        });

        // 설정 온도를 낮춘다
        downTemp = (ImageButton) findViewById(R.id.downTemp);
        downTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TEMP--;
                tempText.setText(Integer.toString(TEMP));
            }
        });


        // 욕조의 물의 높이를 지정해주면 물이 얼마나 차는지 이미지를 보여준다, layoutParam은 waterHeight의 margin값을 지정해주기 위해 설정
        waterHeight = (ImageView) findViewById(R.id.waterHeight);
        mLayoutParams = (LinearLayout.LayoutParams) waterHeight.getLayoutParams();

        // 욕조 물 가득받는 걸로 설정
        fullWater = (ImageButton) findViewById(R.id.fullWater);
        fullWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterHeight.setImageDrawable(getResources().getDrawable(R.drawable.fullwater));
                mLayoutParams.leftMargin = 3;
                mLayoutParams.bottomMargin = 48;
                mLayoutParams.topMargin = 0;
                waterHeight.getLayoutParams().width = 573;
                waterHeight.setLayoutParams(mLayoutParams);
                height = 4;
            }
        });

        // 욕조 물 중간으로 받는 걸로 설정
        normalWater = (ImageButton) findViewById(R.id.normalWater);
        normalWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterHeight.setImageDrawable(getResources().getDrawable(R.drawable.middlewater));
                mLayoutParams.leftMargin = 2;
                mLayoutParams.topMargin = 6;
                mLayoutParams.bottomMargin = 0;
                waterHeight.getLayoutParams().width = 573;
                waterHeight.setLayoutParams(mLayoutParams);
                height = 3;
            }
        });

        // 욕조 물 보통보다 적게 받는 걸로 설정
        slightlyWater = (ImageButton) findViewById(R.id.slightlyWater);
        slightlyWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterHeight.setImageDrawable(getResources().getDrawable(R.drawable.slightlywater));
                mLayoutParams.leftMargin = 9;
                mLayoutParams.topMargin = 60;
                mLayoutParams.bottomMargin = 0;
                waterHeight.getLayoutParams().width = 560;
                waterHeight.setLayoutParams(mLayoutParams);
                height = 2;
            }
        });

        // 욕조 물 적게 받는 걸로 설정
        lessWater = (ImageButton) findViewById(R.id.lessWater);
        lessWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterHeight.setImageDrawable(getResources().getDrawable(R.drawable.lesswater));
                mLayoutParams.leftMargin = 25;
                mLayoutParams.topMargin = 118;
                mLayoutParams.bottomMargin = 0;
                waterHeight.getLayoutParams().width = 528;
                waterHeight.setLayoutParams(mLayoutParams);
                height = 1;
            }
        });

/*        // 급수 시작 버튼
        startWaterBtn = (ImageButton) findViewById(R.id.startWaterBtn);
        startWaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "급수가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                startstop = 1;
                smartMir.bathwater(startstop, height, TEMP);
            }
        });*/

        // 급수 시작 버튼
        startWaterBtn = (ImageButton) findViewById(R.id.startWaterBtn);
        startWaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "급수가 시작되었습니다.", Toast.LENGTH_SHORT).show();

                startstop = 1;

                smartMir.bathwater(startstop, height, TEMP);
            }
        });

        // 급수 중지 버튼
        stopWaterBtn = (ImageButton) findViewById(R.id.stopWaterBtn);
        stopWaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterHeight.setImageDrawable(null);
                Toast.makeText(getBaseContext(), "급수가 중지되었습니다.", Toast.LENGTH_SHORT).show();
                startstop = 2;
                smartMir.bathwater(startstop, height, TEMP);
            }
        });

    }

}
