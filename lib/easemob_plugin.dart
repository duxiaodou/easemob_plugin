import 'dart:async';

import 'package:flutter/services.dart';

class EasemobPlugin {
  static EasemobPlugin get instance => _getInstance();
  static EasemobPlugin _instance;

  EasemobPlugin._internal() {
    _eventSubscription =
        _eventChannel.receiveBroadcastStream().listen(_listener);
  }

  /*
    单例模式
   */
  static EasemobPlugin _getInstance() {
    if (_instance == null) {
      _instance = new EasemobPlugin._internal();
    }
    return _instance;
  }

  static const MethodChannel _methodChannel =
      const MethodChannel('easemob_plugin/methods');

  static const EventChannel _eventChannel =
      const EventChannel('easemob_plugin/events');

  // 函数：收到消息
  Function _onMessageReceived;

  StreamSubscription<dynamic> _eventSubscription;


  /*
    初始化
   */
  static Future<bool> init(String appKey, bool isDebug) async {
    return await _methodChannel.invokeMethod("init", {"appKey":appKey, "isDebug":isDebug});
  }

  /*
    登录
   */
  static Future<bool> login(String username, String password) async {
    try {
      return await (_methodChannel
          .invokeMethod("login", {"username": username, "password": password}) != null);
    } on PlatformException catch (e) {
      return false;
    }

  }

  /*
    退出
   */
  static Future<bool> logout(bool isUnbundle) async {
    try {
      return await (_methodChannel
          .invokeMethod("logout", {"isUnbundle": isUnbundle}) != null);
    } on PlatformException catch (e) {
      return false;
    }

  }

//
  void setOnMessageReceived(onMessageReceived) {
    this._onMessageReceived = onMessageReceived;
  }
//
  void _listener(dynamic event) {
    final Map<dynamic, dynamic> map = event;
    this._onMessageReceived(event);
  }
//    final Map<dynamic, dynamic> map = event;
//    var eventType=map['eventType'];
//    switch(eventType){
//      case 'onMessageReceived':
//        var msg=map['message'];
//        var type=map['type'];
//        this._onMessageReceived(msg,type);
//        break;
//    }
//  }
}