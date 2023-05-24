package com.example.suicide_prevention_app;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;


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
    private ChatbotService chatbotService;

    private Button callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.chat_recycler_view);
        inputText = findViewById(R.id.input_text);
        sendButton = findViewById(R.id.send_button);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatbotService = new ChatbotService();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(chatAdapter);

        callButton = findViewById(R.id.call_button);

        /* 전화 버튼 클릭 이벤트 */
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateButton(callButton);
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

    }
    /* 버튼 클릭했을때 확대 축소되는 애니메이션 (통화 버튼) */
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
            return chatbotService.sendMessageToServer(message[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            addResponseMessage(result);
        }
    }
}