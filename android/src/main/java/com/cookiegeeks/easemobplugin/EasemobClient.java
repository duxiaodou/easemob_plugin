package com.cookiegeeks.easemobplugin;

import android.content.Context;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * @author BadTudou
 */
public class EasemobClient {
    private final String TAG = "EasemobPlugin";


    /*
        初始化
     */
    public boolean init(Context context, String appKey, boolean isDebug) {
        EMOptions options = new EMOptions();
        options.setAppKey(appKey);
        options.setAcceptInvitationAlways(false);
        EMClient.getInstance().init(context, options);
        EMClient.getInstance().setDebugMode(isDebug);
        return true;
    }

    /*
        登录
     */
    public void login(String username, String password, final Result result) {
        EMClient.getInstance().login(username, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                result.success("login success");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                result.error(String.valueOf(code), message, null);
            }
        });
    }

    /*
        退出
     */
    public void logout(boolean isUnbundle, final Result result) {
        EMClient.getInstance().logout(isUnbundle, new EMCallBack() {

            @Override
            public void onSuccess() {
                result.success("logout success");
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                result.error(String.valueOf(code), message, null);
            }
        });
    }


    /*
        添加消息监听器
     */
    public void addMessageListener(final EventSink eventSink) {

        EMMessageListener msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (EMMessage message : messages) {

                        EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                        Map<String, Object> event = new HashMap<>();
                        event.put("id", message.getMsgId());
                        event.put("from", message.getFrom());
                        event.put("to", message.getTo());
                        event.put("eventType", "onMessageReceived");
                        event.put("body", body.toString());
                        event.put("chatType", message.getChatType().toString());
                        event.put("timestamp", message.getMsgTime());
                        event.put("ext", message.ext());
                        event.put("type", message.getType().toString());
                        eventSink.success(event);
                }

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {

            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {

            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


}
