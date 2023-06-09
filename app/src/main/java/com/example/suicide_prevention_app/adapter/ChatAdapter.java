package com.example.suicide_prevention_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.suicide_prevention_app.R;
import com.example.suicide_prevention_app.infrastructure.Message;

import java.util.List;

/* 메시지 목록을 관리하고 채팅화면에 메시지를 표시하는 역할수행 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<Message> messageList; // 채팅 메시지 목록을 저장하는 리스트

    public ChatAdapter(List<Message> messageList) { // 외부에서 채팅 메시지 목록을 설정할 수 있다.
        this.messageList = messageList;
    }

    @NonNull
    @Override
    /* 사용자 메시지와 챗봇 메시지를 구분하는 각각의 뷰를 생성 */
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 0:
                View userMessageView = inflater.inflate(R.layout.item_user_message, parent, false);
                return new ViewHolder(userMessageView);
            case 1:
                View imageView = inflater.inflate(R.layout.item_bot_image_message, parent, false);
                return new ViewHolder(imageView);
            case 2:
                View messageView = inflater.inflate(R.layout.item_bot_message, parent, false);
                return new ViewHolder(messageView);
        }

        return null;
    }

    /* 해당 position에 있는 메시지를 가져와 viewHolder의 뷰에 데이터 설정 */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case 0:
            case 2:
                holder.bindMessage(message);
                break;
            case 1:
                holder.bindImageMessage(message);
                break;
        }

        // 채팅 메시지가 챗봇의 메시지일 경우 왼쪽으로 정렬
       /* if (!message.isUserMessage()) {
            holder.messageText.setGravity(Gravity.START); // 여기서 Gravity.START로 설정
            holder.messageText.setTypeface(Typeface.createFromAsset(holder.messageText.getContext().getAssets(), "font/pretty.ttf"));   // 폰트 (글씨체로 변경)

        } else {
            holder.messageText.setGravity(Gravity.END); // 여기서 Gravity.END로 설정
            holder.messageText.setTypeface(Typeface.createFromAsset(holder.messageText.getContext().getAssets(), "font/pretty.ttf"));   // 폰트 (글씨체로 변경)
        }*/

    }


    /* 메소드는 총 메시지의 개수를 반환 */
    @Override
    public int getItemCount() {
        return messageList.size();
    }

    /* 특정 position에 해당하는 메시지의 타입을 반환 */
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isUserMessage()) {
            return 0;
        } else if (messageList.get(position).getImageUrl() != null) {
            return 1;
        } else {
            return 2;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private ImageView messageImage;

        /* 각각의 채팅 메시지를 표시하는 뷰 홀더 */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageImage = itemView.findViewById(R.id.message_image);
        }

        /* 메시지를 받아와서 해당 뷰 홀더의 뷰에 데이터를 설정 */
        public void bindMessage(Message message) {
            messageText.setText(message.getContent());
        }

        public void bindImageMessage(Message message) {
            String imageUrl = message.getImageUrl();
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(messageImage);
        }
    }
}
