package com.atitto.ukropchat.app.mainscreen;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.atitto.domain.entities.ChatListItem;
import com.atitto.ukropchat.BR;
import com.atitto.ukropchat.R;
import com.atitto.ukropchat.app.ToChatInterface;
import com.atitto.ukropchat.app.helpers.DialogHelper;
import com.atitto.ukropchat.databinding.ItemChatBinding;
import com.github.nitrico.lastadapter.Holder;
import com.github.nitrico.lastadapter.ItemType;
import com.github.nitrico.lastadapter.LastAdapter;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

public class MainScreenFragment extends Fragment {

    EditText vTcp;
    EditText vPort;
    RecyclerView rvChats;
    Button bConnect;
    Button bConnectUdp;
    TextView tvMyIP;
    ProgressBar pbWaiting;

    ObservableList<ChatListItem> chats = new ObservableArrayList<>();

    ToChatInterface toChatInterface;

    public void setToChatInterface(ToChatInterface toChatInterface) {
        this.toChatInterface = toChatInterface;
    }

    @Inject
    MainScreenViewModel viewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        AndroidSupportInjection.inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_screen_fragment, container, false);
        rvChats = view.findViewById(R.id.rvChats);
        vTcp = view.findViewById(R.id.etTCP);
        vPort = view.findViewById(R.id.etUPD);
        bConnect = view.findViewById(R.id.bConnect);
        tvMyIP = view.findViewById(R.id.tvIp);
        pbWaiting = view.findViewById(R.id.pbWaiting);
        bConnectUdp = view.findViewById(R.id.bConnectUdp);
        bindRecyclerView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindClicks();
        bindViewModelEvents();
        viewModel.getMyIP();
        viewModel.startServer();
    }

    private void bindRecyclerView() {
        rvChats.setAdapter(new LastAdapter(chats, BR.item)
                .map(ChatListItem.class, new ItemType<ItemChatBinding>(R.layout.item_chat) {
                    @Override
                    public void onBind(@NotNull Holder<ItemChatBinding> holder) {
                        super.onBind(holder);
                        holder.itemView.setOnClickListener(v -> toChatInterface.toChat(holder.getBinding().getItem().getTitle(), holder.getBinding().getItem()));
                    }
                })
        );
        DividerItemDecoration decoration = new DividerItemDecoration(rvChats.getContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.divider));
        rvChats.addItemDecoration(decoration);
    }

    private void bindViewModelEvents() {
        viewModel.getChatsLiveData().observe(this, chatListItems -> {
            if (chatListItems != null) {
                chats.clear();
                chats.addAll(chatListItems);
            }
        });
        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) DialogHelper.showErrorAlert(getContext(), error);
        });
        viewModel.getIpLiveData().observe(this, s -> {
            if (s != null) tvMyIP.setText(s);
        });
        viewModel.getOnConnectedLiveData().observe(this, s -> {
            if (s != null) Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
        });
        viewModel.getSocketRepliedLiveData().observe(this, s -> {
            if (s != null) {
                Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
                toChatInterface.toChat(s, null);
            }
        });
        viewModel.getConnectionStateLiveData().observe(this, state -> {
            if (state != null) {
                switch (state) {
                    case NOT_WAITING: {
                        pbWaiting.setVisibility(View.GONE);
                        break;
                    }
                    case WAITING_FOR_CONNECTION: {
                        pbWaiting.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });
    }

    private void bindClicks() {
        bConnect.setOnClickListener(v -> viewModel.connectBySocket(vTcp.getText().toString(), vPort.getText().toString()));
        bConnectUdp.setOnClickListener(v -> viewModel.connectByUdpSocket(vPort.getText().toString()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.dispose();
    }
}
