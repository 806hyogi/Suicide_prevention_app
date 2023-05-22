package com.example.suicide_prevention_app;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatbotService {
    // OkHttpClient 인스턴스 생성
    OkHttpClient client = new OkHttpClient();

    public static final MediaType TEXT = MediaType.get("text/plain; charset=utf-8");

    public String sendMessageToServer(String message) {
        RequestBody body = RequestBody.create(message, TEXT);
        // HTTP 요청 생성
        /*
        실제 디바이스는 http://127.0.0.1:8080/message
        안드로이드 에뮬레이터의 경우는 http://10.0.2.2:8080/message
         */
        Request request = new Request.Builder().url("192.168.0.2:8080").post(body).build();

        // 요청 보내고 응답 받기
        Response response;
        {
            try {
                response = client.newCall(request).execute();
                // 응답 처리
                if (response.isSuccessful()) {
                    // 응답 성공
                    // TODO: 응답 데이터 처리
                    return response.body().string();
                } else {
                    // 응답 실패
                    // TODO: 응답 실패 처리
                    throw new IOException("예외 발생: " + response);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
