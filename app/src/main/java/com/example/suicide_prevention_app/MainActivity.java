package com.example.suicide_prevention_app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;

    private EditText inputText;
    private Button sendButton;

    // 엑티비티 생성될 때 호출되는 메소드(레이아웃 설정, 초기화작업)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 메소드를 사용하여 xml 레이아웃 파일에서 ui 요소를 찾아 변수에 할당 */
        recyclerView = findViewById(R.id.chat_recycler_view);
        inputText = findViewById(R.id.input_text);
        sendButton = findViewById(R.id.send_button);

        messageList = new ArrayList<>();  // 메세지 객체 저장하는 리스트
        chatAdapter = new ChatAdapter(messageList); // 메시지 리스트 기반으로 채팅화면 구성

        /* 채팅 메시지가 위에서 아래로 세로로 스크롤 되도록 구성 */

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        /* 버튼 클릭할때마다 입력된 메시지를 처리하고 응답을 생성 */
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
                }
            }
        });
    }

    /* 사용자가 입력한 메시지 처리, 응답 메시지를 리스트에 추가 */
    private void sendMessage(String message) {
        messageList.add(new Message(message, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);

        String response = generateResponse(message);
        messageList.add(new Message(response, false));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    /* 사용자 메시지를 기반으로 응답을 생성하는 로직을 담당 */
    private String generateResponse(String message) {
        // 챗봇의 응답 생성 로직 구현
        return "안녕하세요!";
    }
}