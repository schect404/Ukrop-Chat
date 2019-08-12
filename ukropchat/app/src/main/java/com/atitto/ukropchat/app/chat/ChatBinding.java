package com.atitto.ukropchat.app.chat;

import android.databinding.BindingAdapter;
import android.widget.RelativeLayout;

import com.atitto.ukropchat.R;

import kotlin.jvm.JvmStatic;

public class ChatBinding {

    @JvmStatic
    @BindingAdapter(value = "app:isMy")
    public static void bindMessageContainer(RelativeLayout layout, Boolean isMy) {
        layout.setBackgroundResource(isMy ? R.drawable.message_item_my : R.drawable.message_item_not_my);
    }

}
