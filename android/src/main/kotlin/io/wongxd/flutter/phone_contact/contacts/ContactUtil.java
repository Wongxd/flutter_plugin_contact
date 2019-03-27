package io.wongxd.flutter.phone_contact.contacts;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * Created by wongxd on 2018/4/26.
 * https://github.com/wongxd
 * wxd1@live.com
 */
public class ContactUtil {

    private static final String TAG = "ContactUtil";


    public static class Contact {
        public int contact_id;
        public String contact_name;
        public String lookup_key;
        public List<String> telephoneNumbers = new ArrayList<>();
        public String sort_key_primary;
        public String location;


        //获得一个联系人名字的首字符。
        //比如一个人的名字叫“安卓”，那么这个人联系人的首字符是：A。
        public String firstLetterOfName() {
            String key = sort_key_primary.substring(0, 1).toUpperCase();
            //获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
            if (key.matches("[A-Z]")) {
                return key;
            } else {
                String py = PinyinUtils.getPinyinFirstLetter(sort_key_primary);
                if (key.matches("[A-Z]")) {
                    return py;
                }
                return "#";
            }
        }

        @Override
        public String toString() {
            return "Contact{" +
                    "contact_name='" + contact_name + '\'' +
                    ", contact_phoneNum='" + telephoneNumbers.get(0) + '\'' +
                    ", contact_id=" + contact_id +
                    ", lookup_key=" + lookup_key +
                    ", pinYin='" + sort_key_primary + '\'' +
                    ", key='" + firstLetterOfName() + '\'' +
                    ", location='" + location + '\'' +
                    '}';
        }

    }

    public static class ContactListBean {
        List<Contact> list;

        public List<Contact> getList() {
            return list;
        }

        public void setList(List<Contact> list) {
            this.list = list;
        }
    }

    /**
     * 跳转到系统的联系人编辑界面
     *
     * @param context
     * @param contact_id
     * @param lookup_key
     */
    public static void jumpToPeopleEdit(Context context, Long contact_id, String lookup_key) {
        //发送一个隐式意图，打开手机系统联系人界面编辑contact的信息,
        Intent intent = new Intent(Intent.ACTION_EDIT);
        //需要获取到数据库contacts表中lookup列中的key值，在上面遍历contacts集合时获取到
        Uri data = ContactsContract.Contacts.getLookupUri(contact_id, lookup_key);
        intent.setDataAndType(data, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
        intent.putExtra("finishActivityOnSaveCompleted", true);
        context.startActivity(intent);
    }

    /**
     * SDK>=23  RUNTIME PERMISSION
     * <uses-permission android:name="android.permission.READ_CONTACTS"/>
     * 读取联系人
     *
     * @param ctx
     */
    public static ArrayList<Contact> readContacts(Context ctx) {
        ArrayList<Contact> listMembers = new ArrayList<>();
        Cursor cursor = null;
        HashSet<Integer> contactIds = new HashSet<>();
        try {
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            // 这里是获取联系人表的电话里的信息  包括：名字，联系人id,电话号码；
            // 然后在根据"sort-key"排序
            cursor = ctx.getContentResolver().query(
                    uri,
                    null,
                    null, null, ContactsContract.Contacts.SORT_KEY_PRIMARY);

            if (cursor != null && cursor.moveToFirst()) {
                Log.e(TAG, "ContactCount:" + cursor.getCount());
                do {
                    Contact contact = new Contact();

                    // 联系人ID
                    int contact_id = cursor.getInt(cursor
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                    // 联系人姓名
                    String name = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    String lookup_key = cursor.getString(cursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));


                    // Sort Key，读取的联系人按照姓名从 A->Z 排序分组。
                    String sort_key_primary = cursor
                            .getString(cursor
                                    .getColumnIndex(ContactsContract.Contacts.SORT_KEY_PRIMARY));

                    contact.contact_id = contact_id;
                    contact.contact_name = name;
                    contact.lookup_key = lookup_key;
                    contact.sort_key_primary = PinyinUtils.ccs2Pinyin(sort_key_primary);

                    if (contactIds.contains(contact_id))
                        continue;


                    Cursor phone = null;
                    try {
                        // 获得联系人手机号码
                        phone = ctx.getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                                        + contact_id, null, null);

                        // 取得电话号码(可能存在多个号码)
                        // 因为同一个名字下，用户可能存有一个以上的号，
                        // 遍历。
                        ArrayList<String> phoneNumbers = new ArrayList<String>();
                        while (phone.moveToNext()) {
                            int phoneFieldColumnIndex = phone
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            //                                            contact.contact_phoneNum = contact_phoneNum.replaceAll("[^0-9]", "");
                            String phoneNumber = phone.getString(phoneFieldColumnIndex);
                            phoneNumber = phoneNumber.replace("+86", "");
                            phoneNumber = phoneNumber.replace(" ", "");
                            phoneNumber = phoneNumber.replace(" ", "");

                            phoneNumbers.add(phoneNumber);
                        }

                        contact.telephoneNumbers = phoneNumbers;
                        contact.location = "";

                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (phone != null) {
                            phone.close();
                        }
                    }

                    Log.e(TAG, "contact:" + contact.toString());
                    if (name != null) {
                        listMembers.add(contact);
                        contactIds.add(contact_id);
                    }
                } while (cursor.moveToNext());

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return listMembers;
    }


    /**
     * SDK>=23  RUNTIME PERMISSION
     * <uses-permission android:name="android.permission.WRITE_CONTACTS"/>
     * 写入联系人
     */
    public static boolean writeConstact(Context ctx, String name, String... phones) {

        boolean flag = false;

        try {
            ContentValues values = new ContentValues();
            Uri rawContactUri = ctx.getContentResolver().insert(
                    ContactsContract.RawContacts.CONTENT_URI, values);
            long rawContactId = ContentUris.parseId(rawContactUri);

            // 表插入姓名
            values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
            values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);// 内容类型
            values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
            ctx.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);

            for (String phone : phones) {
                // 表插入电话
                values.clear();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
                values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
                values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                Uri result = ctx.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
                flag = (result != null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;

    }


    public static class CallLogInfo {
        public String name;
        public String number;
        public String date;
        public int type;   // 来电:1，拨出:2,未接:3
        public String time;   //通话时长
        public String location;//归属地

        public CallLogInfo(String name, String number, String date, int type, String time, String location) {
            this.name = name;
            this.number = number;
            this.date = date;
            this.type = type;
            this.time = time;
            this.location = location;
        }

        @Override
        public String toString() {
            return name + "  " + number + "  " + date + "  " + type + "  " + time + "  " + location;
        }
    }

    public static class CallLogInfoListBean {
        List<CallLogInfo> list;

        public List<CallLogInfo> getList() {
            return list;
        }

        public void setList(List<CallLogInfo> list) {
            this.list = list;
        }
    }

    /**
     * SDK>=23  RUNTIME PERMISSION
     * <uses-permission android:name="android.permission.READ_CALL_LOG" />
     * <p>
     * 读取通话记录
     *
     * @param ctx
     * @return
     */
    public static ArrayList<CallLogInfo> readCallLog(Context ctx) {

        ArrayList<CallLogInfo> callLogList = new ArrayList<>();

        String[] projection = {CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE};
        try {
            Uri callLogUri = CallLog.Calls.CONTENT_URI;

            @SuppressLint("MissingPermission") Cursor cursor = ctx.getContentResolver()
                    .query(callLogUri, projection, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
            String callLogName;
            String callLogNumber;
            String callLogDate;
            int callLogType;
            int callLogTime;
            String type;
            String time;


            while (cursor.moveToNext()) {
                callLogName = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                if (callLogName == null) {
                    callLogName = "陌生号码";
                }
                callLogNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

                callLogDate = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DATE));
                @SuppressLint("SimpleDateFormat") SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d = new Date(Long.parseLong(callLogDate));
                callLogDate = DateFormat.format(d);

                callLogType = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
                if (callLogType == 1) {
                    type = "来电";
                } else if (callLogType == 2) {
                    type = "拨出";
                } else
                    type = "未接";

                callLogTime = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.DURATION));
                if (type.equals("未接")) {
                    time = "未接";
                } else {
                    time = timeChange(callLogTime);
                }

                if (callLogName.isEmpty())
                    callLogName = "陌生号码";

//                CallLogInfo callLogInfo = new CallLogInfo(callLogName, callLogNumber
//                        , callLogDate, callLogType, time, GeoUtil.getGeocodedLocationFor(ctx, callLogNumber));
                CallLogInfo callLogInfo = new CallLogInfo(callLogName, callLogNumber
                        , callLogDate, callLogType, time, "");
                callLogList.add(callLogInfo);
                Log.e(TAG + " getCallLog", callLogDate + callLogName + callLogNumber + callLogTime + callLogType);
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return callLogList;
    }

    /**
     * 把按秒显示的通话时长转化为友好显示的时长
     *
     * @param duration
     * @return
     */
    private static String timeChange(int duration) {
        int h = 0;
        int m = 0;
        int s = 0;
        int temp = duration % 3600;
        if (duration > 3600) {
            h = duration / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    m = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp / 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            m = duration / 60;
            if (duration % 60 != 0) {
                s = duration % 60;
            }
        }
        String result = "通话时长：" + h + "时" + m + "分" + s + "秒";
        if (h == 0) {
            result = "通话时长：" + m + "分" + s + "秒";
        } else if (m == 0) {
            result = "通话时长：" + s + "秒";
        }
        return result;
    }

    @SuppressLint("MissingPermission")
    private static void instertCallLog(Context context, String displayName, String number, String duration) {
        try {
            ContentValues values = new ContentValues();
            values.put(CallLog.Calls.CACHED_NAME, displayName);
            values.put(CallLog.Calls.NUMBER, number);
            values.put(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
            values.put(CallLog.Calls.DATE, System.currentTimeMillis());
            values.put(CallLog.Calls.DURATION, duration);
            context.getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
