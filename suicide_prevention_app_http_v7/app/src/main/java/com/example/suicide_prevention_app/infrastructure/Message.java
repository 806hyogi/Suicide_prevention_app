package com.example.suicide_prevention_app.infrastructure;

/* 채팅 메시지를 나타내는 모델 클래스 */

public class Message {
    private String content; // 메시지 내용 저장
    private boolean isUserMessage; // 사용자 메시지 여부
    private String imageUrl; // 이미지 URL

    public Message(String content, boolean isUserMessage, String imageUrl) {
        this.content = content;
        this.isUserMessage = isUserMessage;
        this.imageUrl = imageUrl;
    }

    /* 메시지 내용 반환 */
    public String getContent() {
        return content;
    }

    /* 사용자 메시지 여부 반환 */
    public boolean isUserMessage() {
        return isUserMessage;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
