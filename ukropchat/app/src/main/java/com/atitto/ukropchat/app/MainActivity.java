package com.atitto.ukropchat.app;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.atitto.domain.entities.ChatListItem;
import com.atitto.domain.socket.SocketUseCase;
import com.atitto.ukropchat.R;
import com.atitto.ukropchat.app.chat.ChatFragment;
import com.atitto.ukropchat.app.mainscreen.MainScreenFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends DaggerAppCompatActivity implements ToChatInterface, FragmentManager.OnBackStackChangedListener {

    private static final String REPLIED_SUFFIX = "replied ";
    private static final String SLASH_SUFFIX = "/";

    CompositeDisposable disposer = new CompositeDisposable();

    @Inject
    MainScreenFragment mainScreenFragment;

    @Inject
    ChatFragment chatFragment;

    @Inject
    SocketUseCase socketUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidInjection.inject(this);

        WifiManager wm = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.MulticastLock multicastLock = wm.createMulticastLock("RavnApplication");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();

        mainScreenFragment.setToChatInterface(this);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        shouldDisplayHomeUp();

        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        tr.add(R.id.vActivityView, mainScreenFragment);
        tr.commit();

        disposer.add(socketUseCase.getOnSocketDisconnected()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ip -> socketUseCase.removeSocket(ip)));

        disposer.add(socketUseCase.getOnConnected()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(socketPair -> {
                    socketUseCase.storeConnectedSocket(socketPair);
                }));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposer.dispose();
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
    }

    public void shouldDisplayHomeUp() {
        boolean canGoBack = getSupportFragmentManager().getBackStackEntryCount() > 0;
        if (!canGoBack) setTitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(canGoBack);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void toChat(String id, ChatListItem chatListItem) {
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
        chatFragment.setId(id.replace(REPLIED_SUFFIX, SLASH_SUFFIX));
        if (chatListItem != null) {
            chatFragment.setId(chatListItem.getTitle());
            chatFragment.setMessages(chatListItem);
        }
        setTitle(id.replace(REPLIED_SUFFIX, ""));

        tr.replace(R.id.vActivityView, chatFragment);
        tr.addToBackStack(null);
        tr.commit();
    }
}
