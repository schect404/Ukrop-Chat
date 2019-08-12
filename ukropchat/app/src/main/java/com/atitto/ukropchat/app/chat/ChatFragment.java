package com.atitto.ukropchat.app.chat;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.atitto.domain.entities.ChatListItem;
import com.atitto.domain.entities.Message;
import com.atitto.ukropchat.BR;
import com.atitto.ukropchat.R;
import com.github.nitrico.lastadapter.LastAdapter;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class ChatFragment extends Fragment {

    @Inject
    ChatViewModel viewModel;

    RecyclerView rvMessages;
    EditText etNewMessage;
    Button bSend;
    String id;
    RelativeLayout vNewMessage;

    Boolean isFromPersistance = false;

    ObservableList<Message> messages = new ObservableArrayList<>();

    public void setId(String id) {
        this.id = id;
    }

    public void setMessages(ChatListItem chatListItem) {
        messages.clear();
        messages.addAll(chatListItem.getMessages());
        isFromPersistance = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        rvMessages = view.findViewById(R.id.rvMessages);
        etNewMessage = view.findViewById(R.id.etNewMessage);
        bSend = view.findViewById(R.id.bSend);
        vNewMessage = view.findViewById(R.id.vNewMessageContainer);
        if (isFromPersistance) vNewMessage.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rvMessages.setAdapter(new LastAdapter(messages, BR.item).map(Message.class, R.layout.item_message));
        bSend.setOnClickListener(v -> {
            if (!etNewMessage.getText().toString().equals(""))
                viewModel.sendMessage(etNewMessage.getText().toString());
            etNewMessage.setText("");
        });
        viewModel.getNewMessageLiveData().observe(this, message -> {
            messages.add(0, message);
            rvMessages.scrollToPosition(0);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (!isFromPersistance) viewModel.storeChat(new ChatListItem(id, messages));
        if (id.contains("/")) viewModel.closeSocket(id);
        messages.clear();
        viewModel.dispose();
    }
}
