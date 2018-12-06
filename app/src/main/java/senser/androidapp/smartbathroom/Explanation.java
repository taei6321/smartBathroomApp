package senser.androidapp.smartbathroom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class Explanation extends AppCompatActivity {

    // 사용법 클릭시 4가지의 제품 버튼
    ImageButton smartMirImgBtn, smartbathImgBtn, digitalshowerheadImgBtn, autofanImgBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explanation);

        smartMirImgBtn = (ImageButton) findViewById(R.id.smartMirImgBtn);
        smartMirImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Explanation.this, SmartMirUse.class);
                startActivity(intent);
            }
        });

        smartbathImgBtn = (ImageButton) findViewById(R.id.smartbathImgBtn);
        smartbathImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Explanation.this, SmartBathUse.class);
                startActivity(intent);
            }
        });

        digitalshowerheadImgBtn = (ImageButton) findViewById(R.id.digitalshowerheadImgBtn);
        digitalshowerheadImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Explanation.this, DigitalShowerHeadUse.class);
                startActivity(intent);
            }
        });

        autofanImgBtn = (ImageButton) findViewById(R.id.autofanImgBtn);
        autofanImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Explanation.this, AutoFanUse.class);
                startActivity(intent);
            }
        });
    }
}
