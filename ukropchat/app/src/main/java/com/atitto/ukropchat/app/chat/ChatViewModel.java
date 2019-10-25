package com.atitto.ukropchat.app.chat;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.atitto.domain.chat.ChatUseCase;
import com.atitto.domain.entities.ChatListItem;
import com.atitto.domain.entities.Message;
import com.atitto.domain.socket.SocketUseCase;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public interface ChatViewModel {

    LiveData<Message> getNewMessageLiveData();

    void sendMessage(String text);

    void dispose();

    void storeChat(ChatListItem chat);

    void closeSocket(String ip);
}

class ChatViewModelImpl extends ViewModel implements ChatViewModel {

    private SocketUseCase socketUseCase;
    private ChatUseCase chatUseCase;
    private CompositeDisposable disposer = new CompositeDisposable();
    private MutableLiveData<Message> newMessageLiveData = new MutableLiveData<>();

    @Inject
    ChatViewModelImpl(SocketUseCase socketUseCase, ChatUseCase chatUseCase) {
        this.socketUseCase = socketUseCase;
        this.chatUseCase = chatUseCase;
        subscribeNewMessages();
    }

    @Override
    public LiveData<Message> getNewMessageLiveData() {
        return newMessageLiveData;
    }

    @Override
    public void sendMessage(String text) {
        socketUseCase.sendNewMessage(text);
        newMessageLiveData.postValue(new Message(text, true));
    }

    @Override
    public void dispose() {
        socketUseCase.closeSocket();
        disposer.dispose();
    }

    private void subscribeNewMessages() {
        disposer.add(socketUseCase.getOnMessageReceived()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> newMessageLiveData.postValue(new Message(s, false))));
    }

    @Override
    public void storeChat(ChatListItem chat) {
        chatUseCase.storeChat(chat);
    }

    @Override
    public void closeSocket(String ip) {
        socketUseCase.closeSocket(ip);
    }
}
