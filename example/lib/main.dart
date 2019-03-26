import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:easemob_plugin/easemob_plugin.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    print('SDK初始化');
    EasemobPlugin.init("1106181122010765#oncetest", false).then((result) {
      print(result);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
            child: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Center(
              child: FlatButton(
                  onPressed: () {
                    print('登录用户');
                    EasemobPlugin.login('easemob', 'easemob').then((loginResult) {
                      print(loginResult);
                      EasemobPlugin.instance.setOnMessageReceived(_onMessageReceived);
                    });
                  },
                  child: Text("login")),
            ),
            Center(
              child: FlatButton(
                  onPressed: () {
                    print('退出用户');
                    EasemobPlugin.logout(true).then((logoutResult) {
                      print(logoutResult);
                    });
                  }, child: Text("logout")),
            ),
            Center(
              child: FlatButton(onPressed: ()=>null, child: Text("sendMsg")),

            )
          ],
        )),
      ),
    );
  }


   void _onMessageReceived(event){
     print('收到消息');
     print(event);
   }
}


