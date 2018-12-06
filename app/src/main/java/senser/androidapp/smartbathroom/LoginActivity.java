package senser.androidapp.smartbathroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    EditText userID, userPassword;
    Button loginBtn, joinBtn;
    CheckBox autologinCheck;

    SharedPreferences setting;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userID = (EditText) findViewById(R.id.userID);
        userPassword = (EditText) findViewById(R.id.userPassword);

        autologinCheck = (CheckBox) findViewById(R.id.autologinCheck);

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();

        if(setting.getBoolean("autologinCheck", false)){
            userID.setText(setting.getString("id",""));
            userPassword.setText(setting.getString("pwd",""));
            autologinCheck.setChecked(true);
        }

        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String id = userID.getText().toString();
                    String pwd = userPassword.getText().toString();

                    CustomTask task = new CustomTask();
                    String result = task.execute(id, pwd).get();

                    if (result.equals("login    ")) {
                        if(autologinCheck.isChecked()){
                            editor.putString("id", id);
                            editor.putString("pwd", pwd);
                            editor.putBoolean("autologinCheck", true);
                            editor.commit();

                            Toast.makeText(getBaseContext(), id + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            editor.clear();
                            editor.commit();

                            Toast.makeText(getBaseContext(), id + "님 환영합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else if (result.equals("difPwd    ")) {
                        Toast.makeText(getBaseContext(), "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                        userID.setText("");
                        userPassword.setText("");

                    } else if (result.equals("noId    ")) {
                        Toast.makeText(getBaseContext(), "회원가입이 필요합니다.", Toast.LENGTH_SHORT).show();
                        userID.setText("");
                        userPassword.setText("");
                    } else {
                        Log.d("ddd", "ddd");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        joinBtn = (Button) findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    class CustomTask extends AsyncTask<String, Void, String>{
        String sendMsg, receiveMsg;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String str;
                URL url = new URL("http://192.168.0.52:8080/sendDataToAndroid/data.jsp");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestMethod("POST");
                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
                sendMsg = "userID=" + strings[0] + "&userPassword=" + strings[1];
                osw.write(sendMsg);
                osw.flush();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return receiveMsg;
        }
    }
}
