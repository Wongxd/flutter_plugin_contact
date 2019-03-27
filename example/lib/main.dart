import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:phone_contact/phone_contact.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String addContactStr = 'Unknown';
  String contactStr = 'empty';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await PhoneContact.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: SingleChildScrollView(
            child: Center(
          child: Column(
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              // Center(
              //   child: RaisedButton(
              //     child: Text("ADD CONTACT"),
              //     onPressed: () {
              //       ContactBean people = ContactBean();
              //       var time = DateTime.now().toIso8601String();
              //       people.contact_name = "添加$time";
              //       people.telephoneNumbers.add(time);
              //       var temp = PhoneContact.addContact(people);
              //       temp.then((it) {
              //         setState(() {
              //           addContactStr = it ? "添加$time" : "失败了";
              //         });
              //       });
              //     },
              //   ),
              // ),
              // Text('ADD CONTACT: $addContactStr\n'),
              Center(
                child: RaisedButton(
                  child: Text("GET CONTACT"),
                  onPressed: () {
                    var temp = PhoneContact.getContactList();
                    temp.then((it) {
                      if (it is ContactListBean) {
                        setState(() {
                          contactStr = jsonEncode(it);
                        });
                      }
                    });
                  },
                ),
              ),
              Text('contacts: $contactStr\n'),
            ],
          ),
        )),
      ),
    );
  }
}
