package com.example.suicide_prevention_app;

import android.os.AsyncTask;
import android.os.Bundle;
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

        sendButton.setOnClickListener(v -> {
            String input = inputText.getText().toString().trim();
            if (!input.isEmpty()) {
                sendMessage(input);
                inputText.setText("");
                new SendMessageTask().execute(input);
            }
        });
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