package com.example.suicide_prevention_app;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.suicide_prevention_app.adapter.ChatAdapter;
import com.example.suicide_prevention_app.chatbot.ChatbotCommunication;
import com.example.suicide_prevention_app.infrastructure.Message;
import com.example.suicide_prevention_app.infrastructure.CallPhoneActivity;
import com.example.suicide_prevention_app.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private EditText inputText;
    private Button sendButton;
    private ChatbotCommunication chatbotCommunication;
    private CallPhoneActivity callPhoneActivity;


    private Button callButton;
    private Button musicButton;

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

        /* 움직이는 텍스트 */
        TextView textLabel = findViewById(R.id.text_label);

        // 가로 이동 애니메이션
        ObjectAnimator translationX = ObjectAnimator.ofFloat(textLabel, "translationX", 0f, 100f); // 이동 거리 및 방향을 조정하십시오
        translationX.setDuration(1000); // 애니메이션 지속 시간 (밀리초)

        // 애니메이션 반복 설정
        translationX.setRepeatCount(ObjectAnimator.INFINITE); // 무한 반복
        translationX.setRepeatMode(ObjectAnimator.REVERSE); // 앞뒤로 반복

        translationX.start(); // 애니메이션 시작

        /* 전화 버튼 클릭 이벤트 */
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(callButton);
            }
        });

        /* 음악 버튼 클릭 이벤트 */
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(musicButton);
            }
        });

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
                    sendMessage(input);
                    inputText.setText("");
                    new SendMessageTask().execute(input);
                }
            }
        });
        // 배경음 인텐트 호출
        startService(new Intent(getApplicationContext(), MusicService.class));
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

    private void sendMessage(String message) {
        messageList.add(new Message(message, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void addResponseMessage(String message) {
        messageList.add(new Message(message, false));
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
            addResponseMessage(result);
        }
    }
    // 만들어둔 액티비티와 클래스를 연결해야 함
    public class CallPhone {
        public void callPhoneNumber(String phoneNumber) {
            // 전화 걸기 인텐트를 생성
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));

            // 전화 걸기 인텐트를 시작
            startActivity(intent);
        }
    }

    // 홈 화면에서 어플 종료 시, 음악이 꺼지게 만들기
    @Override
    protected void onUserLeaveHint() {
        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        startActivity(intent);

        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onUserLeaveHint();
    }

    // 음악 종료
    @Override
    protected void onDestroy() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onDestroy();
    }

    // 뒤로 가기 버튼 눌렀을 때 배경음악 멈춤
    @Override
    public void onBackPressed() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        super.onBackPressed();
    }
}