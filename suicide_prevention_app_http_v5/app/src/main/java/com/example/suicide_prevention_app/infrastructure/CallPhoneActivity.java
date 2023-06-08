package com.example.suicide_prevention_app.infrastructure;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.suicide_prevention_app.R;

public class CallPhoneActivity extends AppCompatActivity implements View.OnClickListener{

    // 변수 선언
    EditText editNum;
    Button btnCall, btnDial;
    String telNum = "";

    static final int PERMISSIONS_CALL_PHONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_phone);

        // xml 과 연결
        editNum = findViewById(R.id.editNum);
        btnCall = findViewById(R.id.btnCall);
        btnDial = findViewById(R.id.btnDial);

        // 클릭 이벤트 설정
        btnCall.setOnClickListener(this);
        btnDial.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        // 입력번호 저장
        telNum = editNum.getText().toString();
        switch (view.getId()) {
            // 전화 바로 걸기
            // :: 전화 바로 걸기는 시용자에게 권한이 필요하기때문에 권한여부를 먼저 확인한다.
            case R.id.btnCall:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_CALL_PHONE);
                } else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + telNum));
                    startActivity(callIntent);
                }
                break;

            // 다이얼로 가기
            case R.id.btnDial:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + telNum));
                startActivity(dialIntent);
                break;
        }
    }
}
