import 'dart:core';
import 'dart:convert';
import 'package:flutter/services.dart';

class PhoneContact {
  static const MethodChannel _channel =
      const MethodChannel('io.wongxd.flutter.phone_contact');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<ContactListBean> getContactList() async {
    ContactListBean contactList;
    try {
      String jsonStr = await _channel.invokeMethod('getContactList');
      // print("jsonStr--$jsonStr");
      contactList = ContactListBean.fromJson(jsonDecode(jsonStr));
    } on PlatformException catch (e) {
      print("Failed to get contactList: '${e.message}'.");
    }
    return contactList;
  }

  static Future<bool> addContact(ContactBean people) async {
    bool result;
    try {
      String argsStr = jsonEncode(people);
      result = await _channel.invokeMethod('addContact', argsStr);
    } on PlatformException catch (e) {
      print("Failed to addContact: '${e.message}'.");
    }
    return result;
  }

  static Future<CallLogListBean> getCallLogList() async {
    CallLogListBean callLogList;
    try {
      String jsonStr = await _channel.invokeMethod('getCallLogList');
//      print("jsonStr--$jsonStr");
      callLogList = CallLogListBean.fromJson(jsonDecode(jsonStr));
    } on PlatformException catch (e) {
      print("Failed to get callLogList: '${e.message}'.");
    }
    return callLogList;
  }

  // static Future<bool> writeCallLog(CallLogBean callLog) async {
  //   bool result;
  //   try {
  //     String argsStr = jsonEncode(callLog);
  //     result = await platform.invokeMethod('writeCallLog', argsStr);
  //   } on PlatformException catch (e) {
  //     print("Failed to  writeCallLog: '${e.message}'.");
  //   }
  //   return result;
  // }
}

class ContactBean {
  int contact_id;
  String contact_name;
  String lookup_key;
  List<String> telephoneNumbers = [];
  String sort_key_primary;
  String location;
  String tagIndex;
  String pinyin;

  ContactBean(
      {this.contact_id = 0,
      this.contact_name = "",
      this.lookup_key = "",
      this.telephoneNumbers,
      this.sort_key_primary = "",
      this.location = ""});

  String toString() {
    return "Contact{" +
        "contact_name='" +
        contact_name +
        '\'' +
        ", telephoneNumbers='" +
        '\'' +
        ", contact_id=" +
        contact_id.toString() +
        ", lookup_key=" +
        lookup_key +
        ", pinYin='" +
        sort_key_primary +
        '\'' +
        ", key='" +
        '\'' +
        ", location='" +
        location +
        '\'' +
        '}';
  }

  ContactBean.fromJson(Map<String, dynamic> json)
      : contact_id = json['contact_id'] == null ? 0 : json['contact_id'],
        contact_name = json['contact_name'] == null ? "" : json['contact_name'],
        lookup_key = json['lookup_key'] == null ? "" : json['lookup_key'],
        telephoneNumbers = json['telephoneNumbers'] == null
            ? List()
            : (json['telephoneNumbers'] as List)
                .map((i) => i.toString())
                .toList(),
        sort_key_primary =
            json['sort_key_primary'] == null ? "" : json['sort_key_primary'],
        location = json['location'] == null ? "" : json['location'];

  Map<String, dynamic> toJson() => {
        'contact_id': contact_id,
        "contact_name": contact_name,
        'lookup_key': lookup_key,
        'telephoneNumbers': this.telephoneNumbers != null
            ? this.telephoneNumbers.map((i) => i.toString()).toList()
            : null,
        'sort_key_primary': sort_key_primary,
        'location': location
      };
}

class CallLogBean {
  String name;
  String number;
  String date;
  int type; // 来电:1，拨出:2,未接:3
  String time; //通话时长
  String location; //归属地

  CallLogBean(
      {this.name = "",
      this.number = "",
      this.date = "",
      this.type = 1,
      this.time = "",
      this.location = ""});

  toString() {
    return name +
        "  " +
        number +
        "  " +
        date +
        "  " +
        type.toString() +
        "  " +
        time +
        "  " +
        location;
  }

  CallLogBean.fromJson(Map<String, dynamic> json)
      : name = json['name'] == null ? "" : json['name'],
        number = json['number'] == null ? "" : json['number'],
        date = json['date'] == null ? "" : json['date'],
        type = json['type'] == null ? 1 : json['type'],
        time = json['time'] == null ? "" : json['time'],
        location = json['location'] == null ? "" : json['location'];

  Map<String, dynamic> toJson() => {
        'name': name,
        "number": number,
        'date': date,
        'type': type,
        'time': time,
        'location': location
      };
}

class ContactListBean {
  List<ContactBean> list;

  ContactListBean(this.list);

  ContactListBean.fromJson(Map<String, dynamic> map)
      : list =
            (map['list'] as List).map((i) => ContactBean.fromJson(i)).toList();

  Map<String, dynamic> toJson() => {
        'list': this.list != null
            ? this.list.map((i) => i.toJson()).toList()
            : null,
      };
}

class CallLogListBean {
  List<CallLogBean> list;

  CallLogListBean(this.list);

  CallLogListBean.fromJson(Map<String, dynamic> map)
      : list =
            (map['list'] as List).map((i) => CallLogBean.fromJson(i)).toList();

  Map<String, dynamic> toJson() => {
        'list': this.list != null
            ? this.list.map((i) => i.toJson()).toList()
            : null,
      };
}
