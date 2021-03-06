package com.cookiegeeks.easemobplugin;

import android.content.Context;
import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.*;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * EasemobPlugin
 */
public class EasemobPlugin implements MethodCallHandler, StreamHandler {
    private EasemobClient easemobClient = new EasemobClient();
    private static Context flutterContext;
    private static Context activeFlutterContext;


    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), "easemob_plugin/methods");
        methodChannel.setMethodCallHandler(new EasemobPlugin());
        
        final EventChannel eventChannel=new EventChannel(registrar.messenger(),"easemob_plugin/events");
        eventChannel.setStreamHandler(new EasemobPlugin());
        flutterContext = registrar.context();
        activeFlutterContext = registrar.activeContext();
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        String toChatUsername=call.argument("toChatUsername");
        Boolean isGroupChat= call.argument("isGroupChat")==null?false:(Boolean)call.argument("isGroupChat");
        String conversation = call.argument("conversation");
        String messageId = call.argument("messageId");
        switch (call.method) {
            case "init":
                String appKey = call.argument("appKey");
                Boolean isDebug = call.argument("isDebug");
                result.success(easemobClient.init(flutterContext, appKey, isDebug));
                break;

            case "login":
                String username = call.argument("username");
                String password = call.argument("password");
                easemobClient.login(username, password, result);
                break;

            case "logout":
                Boolean isUnbundle = call.argument("isUnbundle");
                easemobClient.logout(isUnbundle, result);
                break;

            case "getConversationUnreadMessageCount":
                easemobClient.getConversationUnreadMessageCount(conversation, result);
                break;

            case "getConversationAllMessageCount":
                easemobClient.getConversationAllMessageCount(conversation, result);
                break;

            case "getUnreadMessageCount":
                easemobClient.getUnreadMessageCount(result);
                break;

            case "sendTextMessage":
                String content = call.argument("content");
                easemobClient.sendTextMessage(conversation, content, isGroupChat, result);
                break;

            case "getConversationMessages":
                String startMessageId = call.argument("startMessageId");
                int pageSize = call.argument("pageSize");
                result.success(easemobClient.getConversationMessages(conversation, startMessageId, pageSize));
                break;

            case "markConversatioAllMessagesAsRead":
                easemobClient.markConversatioAllMessagesAsRead(conversation);
                break;

            case "markAllMessagesAsRead":
                easemobClient.markAllMessagesAsRead();
                break;

            case "markConversationsMessageAsRead":
                easemobClient.markConversationsMessageAsRead(conversation, messageId);
                break;

            default:
                result.notImplemented();
        }
    }

    @Override
    public void onListen(Object o, EventSink eventSink) {
        easemobClient.addMessageListener(eventSink);
    }

    @Override
    public void onCancel(Object o) {

    }

}
