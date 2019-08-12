package com.atitto.ukropchat.app.mainscreen;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.atitto.domain.chat.ChatUseCase;
import com.atitto.domain.entities.ChatListItem;
import com.atitto.domain.socket.SocketUseCase;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

interface MainScreenViewModel {

    LiveData<String> getErrorLiveData();
    LiveData<String> getOnConnectedLiveData();
    LiveData<List<ChatListItem>> getChatsLiveData();
    LiveData<String> getIpLiveData();
    LiveData<ConnectionState> getConnectionStateLiveData();
    LiveData<String> getSocketRepliedLiveData();

    void getMyIP();
    void startServer();
    void dispose();
    void connectBySocket(String ip, String port);
    void connectByUdpSocket(String port);
}

class MainScreenViewModelImpl extends ViewModel implements MainScreenViewModel {

    private SocketUseCase socketUseCase;
    private ChatUseCase chatUseCase;

    private CompositeDisposable disposer = new CompositeDisposable();

    @Inject
    MainScreenViewModelImpl(SocketUseCase socketUseCase, ChatUseCase chatUseCase) {
        this.socketUseCase = socketUseCase;
        this.chatUseCase = chatUseCase;
    }

    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<String> onConnectedLiveData = new MutableLiveData<>();
    private MutableLiveData<List<ChatListItem>> chatsLiveData = new MutableLiveData<>();
    private MutableLiveData<String> ipLiveData = new MutableLiveData<>();
    private MutableLiveData<ConnectionState> connectionStateLiveData = new MutableLiveData<>();
    private MutableLiveData<String> socketRepliedLiveData = new MutableLiveData<>();

    @Override
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    @Override
    public LiveData<String> getOnConnectedLiveData() {
        return onConnectedLiveData;
    }

    @Override
    public LiveData<List<ChatListItem>> getChatsLiveData() {
        return chatsLiveData;
    }

    @Override
    public LiveData<String> getIpLiveData() {
        return ipLiveData;
    }

    @Override
    public LiveData<ConnectionState> getConnectionStateLiveData() {
        return connectionStateLiveData;
    }

    @Override
    public LiveData<String> getSocketRepliedLiveData() {
        return socketRepliedLiveData;
    }

    @Override
    public void getMyIP() {
        ipLiveData.postValue(socketUseCase.getIpAddress());
        List<ChatListItem> list = chatUseCase.getChats();
        chatsLiveData.postValue(list);
    }

    @Override
    public void startServer() {
        startSocket();
    }

    @Override
    public void dispose() {
        socketRepliedLiveData = new MutableLiveData<>();
        disposer.clear();
    }

    @Override
    public void connectBySocket(String ip, String port) {
        connectionStateLiveData.postValue(ConnectionState.WAITING_FOR_CONNECTION);
        socketUseCase.connectSocket(ip, port);
    }

    @Override
    public void connectByUdpSocket(String port) {
        socketUseCase.connectUdpSocket(port);
    }

    private void startSocket() {
        disposer.add(socketUseCase.getOnError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    connectionStateLiveData.postValue(ConnectionState.NOT_WAITING);
                    errorLiveData.postValue(s);
                }));
        disposer.add(socketUseCase.getOnConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketPair -> onConnectedLiveData.postValue(socketPair.first)));
        disposer.add(socketUseCase.getOnSocketError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    connectionStateLiveData.postValue(ConnectionState.NOT_WAITING);
                    errorLiveData.postValue(s);
                }));
        disposer.add(socketUseCase.getOnSocketReplied()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    connectionStateLiveData.postValue(ConnectionState.NOT_WAITING);
                    socketRepliedLiveData.postValue(s);
                }));

        this.socketUseCase.startSocketServer();
    }

}
