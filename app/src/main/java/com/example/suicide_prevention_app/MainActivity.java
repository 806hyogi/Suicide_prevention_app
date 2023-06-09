package com.example.suicide_prevention_app;


import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.suicide_prevention_app.adapter.ChatAdapter;
import com.example.suicide_prevention_app.chatbot.ChatbotCommunication;
import com.example.suicide_prevention_app.infrastructure.Message;
import com.example.suicide_prevention_app.service.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 전화 거는 권한 요청 식별
    static final int PERMISSIONS_CALL_PHONE = 0;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private EditText inputText;
    private Button sendButton;
    private ChatbotCommunication chatbotCommunication;

    private Button callButton;
    private String telNum = "tel:01000000000";
    private Button musicButton;
    private String[] imageUrl = {
            "https://ibb.co/61BH3QS",
            "https://ibb.co/4fYSzGb",
            "https://ibb.co/XFHMGGs",
            "https://ibb.co/THc3jcb",
            "https://i.ibb.co/w47qN4f/fun6.jpg"
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.chat_recycler_view);
        inputText = findViewById(R.id.input_text);
        sendButton = findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatbotCommunication = new ChatbotCommunication();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        callButton = findViewById(R.id.call_button);
        musicButton = findViewById(R.id.music_button);
        final Intent musicServiceIntent = new Intent(this, MusicService.class);
        final boolean[] isMusicPlaying = {false};

        /* 움직이는 텍스트 */
        TextView textLabel = findViewById(R.id.text_label);

        // 가로 이동 애니메이션
        ObjectAnimator translationX = ObjectAnimator.ofFloat(textLabel, "translationX", 0f, 100f); // 이동 거리 및 방향을 조정하십시오
        translationX.setDuration(1000); // 애니메이션 지속 시간 (밀리초)

        // 애니메이션 반복 설정
        translationX.setRepeatCount(ObjectAnimator.INFINITE); // 무한 반복
        translationX.setRepeatMode(ObjectAnimator.REVERSE); // 앞뒤로 반복

        translationX.start(); // 애니메이션 시작

        /* 버튼 클릭했을때 확대 축소되는 애니메이션 (하트 버튼) */
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 버튼 크기를 키우는 애니메이션 설정
                ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(sendButton, "scaleX", 1.0f, 1.2f);
                ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(sendButton, "scaleY", 1.0f, 1.2f);
                scaleUpX.setDuration(200);
                scaleUpY.setDuration(200);

                // 버튼 크기를 원래대로 돌리는 애니메이션 설정
                ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(sendButton, "scaleX", 1.2f, 1.0f);
                ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(sendButton, "scaleY", 1.2f, 1.0f);
                scaleDownX.setDuration(200);
                scaleDownY.setDuration(200);

                // 애니메이션 순서 설정
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(scaleUpX).with(scaleUpY);
                animatorSet.play(scaleDownX).with(scaleDownY).after(scaleUpX);
                animatorSet.start();

                String input = inputText.getText().toString().trim();
                if (!input.isEmpty()) {
                    sendMessage(input, null);

                    if (input.equals("재밌는 사진 보여줘")) {
                        Random photoRandom = new Random();
                        int photoRandomIndex = photoRandom.nextInt(imageUrl.length);
                        String randomImageUrl = imageUrl[photoRandomIndex];

                        addResponseMessage("", randomImageUrl);
                        inputText.setText("");
                    } else {
                        new SendMessageTask().execute(input);
                        inputText.setText("");
                    }
                }
            }
        });

        // 전화 모양 아이콘 클릭 시, 전화 걸기 메소드 실행
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        // 음표 모양 아이콘 클릭 시, 배경음 ON/OFF 가능
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                animateButton(musicButton);

                if (isMusicPlaying[0]) {
                    stopService(musicServiceIntent);
                } else {
                    startService(musicServiceIntent);
                }
                isMusicPlaying[0] = !isMusicPlaying[0];
            }
        });

        // 배경음 인텐트 호출
        startService(new Intent(getApplicationContext(), MusicService.class));

        addResponseMessage("안녕하세요! 사용자님을 도와드릴 \n" +
                "AI 챗봇 그리미입니다.\n" +
                "아래처럼 입력하시면 도움을 받으실 수 있습니다.\n" +
                "예: 호흡이 힘들어\n" +
                "   지금 내가 뭘하면 좋을까? \n" +
                "   재밌는 사진 보여줘\n" +
                "   기타 증상 입력", null);

    }

    /* 버튼 클릭했을때 확대 축소되는 애니메이션 (통화 버튼, 음악 버튼) */
    private void animateButton(Button button) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(button, "scaleX", 1.0f, 1.2f);
        scaleUpX.setDuration(200);
        scaleUpX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(button, "scaleY", 1.0f, 1.2f);
        scaleUpY.setDuration(200);
        scaleUpY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(button, "scaleX", 1.2f, 1.0f);
        scaleDownX.setDuration(200);
        scaleDownX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1.2f, 1.0f);
        scaleDownY.setDuration(200);
        scaleDownY.setInterpolator(new AccelerateDecelerateInterpolator());

        animatorSet.play(scaleUpX).with(scaleUpY);
        animatorSet.play(scaleDownX).with(scaleDownY).after(scaleUpX);

        animatorSet.start();
    }

    private void sendMessage(String message, String imageUrl) {
        messageList.add(new Message(message, true, imageUrl));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void addResponseMessage(String message, String imageUrl) {
        messageList.add(new Message(message, false, imageUrl));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private class SendMessageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... message) {
            return chatbotCommunication.sendMessageToServer(message[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            addResponseMessage(result, null);
        }
    }

    // 전화 걸기 메소드
    private void makePhoneCall() {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(telNum));

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_CALL_PHONE);
        } else {
            startActivity(callIntent);
        }
    }

    // 전화 권한 확인
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_CALL_PHONE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            }
        }
    }

//  홈 화면에서 어플 종료 시, 음악이 꺼지게 만들기
    @Override
    protected void onUserLeaveHint() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onUserLeaveHint();
    }

//  음악 종료
    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onDestroy();
    }

//  뒤로 가기 버튼 눌렀을 때 배경음악 멈춤
    @Override
    public void onBackPressed() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onBackPressed();
    }
}