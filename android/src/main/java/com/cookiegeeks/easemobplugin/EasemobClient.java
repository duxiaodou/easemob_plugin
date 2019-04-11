package com.cookiegeeks.easemobplugin;

import android.content.Context;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessage.ChatType;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.util.EMLog;

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
    static EMClient emClient;


    /*
        初始化
     */
    public boolean init(Context context, String appKey, boolean isDebug) {
        EMOptions options = new EMOptions();
        options.setAppKey(appKey);
        options.setAcceptInvitationAlways(false);
        options.setDeleteMessagesAsExitGroup(false);
        options.setRequireAck(true);
        emClient = EMClient.getInstance();
        emClient.init(context, options);
        emClient.setDebugMode(isDebug);
        return true;
    }

    /*
        登录
     */
    public void login(final String username, final String password, final Result result) {
        emClient.login(username, password, new EMCallBack() {//回调
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
        emClient.logout(isUnbundle, new EMCallBack() {

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
                        event.put("body", body.getMessage());
                        event.put("chatType", message.getChatType().toString());
                        event.put("timestamp", message.getMsgTime());
                        event.put("ext", new JSONObject(message.ext()).toString());
                        event.put("type", message.getType().toString());
                        eventSink.success(event);
                }

            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                // 收到已透传回执
                for (EMMessage message : messages) {

                    EMCmdMessageBody body = (EMCmdMessageBody) message.getBody();
                    Map<String, Object> event = new HashMap<>();
                    event.put("id", message.getMsgId());
                    event.put("from", message.getFrom());
                    event.put("to", message.getTo());
                    event.put("eventType", "onCmdMessageReceived");
                    event.put("body", body.action());
                    event.put("chatType", message.getChatType().toString());
                    event.put("timestamp", message.getMsgTime());
                    event.put("ext", new JSONObject(message.ext()).toString());
                    event.put("type", message.getType().toString());
                    eventSink.success(event);
                }
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
                // 收到已读回执
                for (EMMessage message : messages) {

                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    Map<String, Object> event = new HashMap<>();
                    event.put("id", message.getMsgId());
                    event.put("from", message.getFrom());
                    event.put("to", message.getTo());
                    event.put("eventType", "onMessageRead");
                    event.put("body", body.getMessage());
                    event.put("chatType", message.getChatType().toString());
                    event.put("timestamp", message.getMsgTime());
                    event.put("ext", new JSONObject(message.ext()).toString());
                    event.put("type", message.getType().toString());
                    eventSink.success(event);
                }
            }

            @Override
            public void onMessageDelivered(List<EMMessage> messages) {
                // 收到已送到回执
                for (EMMessage message : messages) {

                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    Map<String, Object> event = new HashMap<>();
                    event.put("id", message.getMsgId());
                    event.put("from", message.getFrom());
                    event.put("to", message.getTo());
                    event.put("eventType", "onMessageDelivered");
                    event.put("body", body.getMessage());
                    event.put("chatType", message.getChatType().toString());
                    event.put("timestamp", message.getMsgTime());
                    event.put("ext", new JSONObject(message.ext()).toString());
                    event.put("type", message.getType().toString());
                    eventSink.success(event);
                }
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                    EMTextMessageBody body = (EMTextMessageBody) message.getBody();
                    Map<String, Object> event = new HashMap<>();
                    event.put("id", message.getMsgId());
                    event.put("from", message.getFrom());
                    event.put("to", message.getTo());
                    event.put("eventType", "onMessageChanged");
                    event.put("body", body.getMessage());
                    event.put("chatType", message.getChatType().toString());
                    event.put("timestamp", message.getMsgTime());
                    event.put("ext", new JSONObject(message.ext()).toString());
                    event.put("type", message.getType().toString());
                    eventSink.success(event);

            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    /*
        获取会话未读消息数量
     */
    public void getConversationUnreadMessageCount(String conversation, final Result result) {
        EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(conversation);
        if (emConversation == null) {
            result.success(0);
        } else {
            result.success(emConversation.getUnreadMsgCount());
        }
    }

    /*
        获取会话消息数量
     */
    public void getConversationAllMessageCount(String conversation, final Result result) {
        EMConversation emConversation = emClient.chatManager().getConversation(conversation);
        if (emConversation == null) {
            result.success(0);
        } else {
            result.success(emConversation.getAllMsgCount());
        }

    }

    /*
        获取未读消息数量
     */
    public void getUnreadMessageCount(final Result result) {
        result.success(emClient.chatManager().getUnreadMsgsCount());
    }

    /*
        发送文本消息
     */
    public void sendTextMessage(String conversation, String content, boolean isGroupChat, final Result result) {
        EMMessage message = EMMessage.createTxtSendMessage(content, conversation);
        if (isGroupChat) {
            message.setChatType(ChatType.GroupChat);
        }
        EMClient.getInstance().chatManager().sendMessage(message);
        result.success(true);
    }

    /*
         获取会话消息
     */
    public  ArrayList<Map<String, String>> getConversationMessages(String conversation, String startMessageId, int pageSize) {
        EMConversation emConversation = emClient.chatManager().getConversation(conversation);
        if (emConversation == null) {
            return null;
        }

        List<EMMessage> unConvertedMessages = emConversation.getAllMessages();
        if ( unConvertedMessages.isEmpty() ) {
            unConvertedMessages = emConversation.loadMoreMsgFromDB(startMessageId, pageSize);
        }
        ArrayList<Map<String, String>> messages = new ArrayList<Map<String, String>>();
        for ( EMMessage message :  unConvertedMessages) {
            EMTextMessageBody body = (EMTextMessageBody) message.getBody();
            Map<String, String> event = new HashMap<>();
            event.put("id", message.getMsgId());
            event.put("from", message.getFrom());
            event.put("to", message.getTo());
            event.put("eventType", "onMessageReceived");
            event.put("body", body.getMessage());
            event.put("chatType", message.getChatType().toString());
            event.put("timestamp", Long.toString(message.getMsgTime()));
            event.put("ext", new JSONObject(message.ext()).toString());
            event.put("type", message.getType().toString());
            messages.add(event);
        }
        return messages;
    }

    /*
        将会话的所有消息设置为已读
     */
    public void markConversatioAllMessagesAsRead(String conversation) {
        emClient.chatManager().getConversation(conversation).markAllMessagesAsRead();
    }

    /*
        将所有消息设置为已读
     */
    public void markAllMessagesAsRead() {
        emClient.chatManager().markAllConversationsAsRead();
    }

    /*
        将会话某条消息设为已读
     */
    public void markConversationsMessageAsRead(String conversation, String messageId) {
        emClient.chatManager().getConversation(conversation).markMessageAsRead(messageId);
    }


}
