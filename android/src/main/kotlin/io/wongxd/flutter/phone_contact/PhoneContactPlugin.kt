package io.wongxd.flutter.phone_contact

import android.app.Activity
import com.google.gson.Gson
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.wongxd.flutter.phone_contact.contacts.ContactUtil
import io.wongxd.flutter.phone_contact.permission.PermissionType
import io.wongxd.flutter.phone_contact.permission.getPermissions
import org.json.JSONObject

class PhoneContactPlugin(val aty: Activity) : MethodCallHandler {

    companion object {
        @JvmStatic
        fun registerWith(registrar: Registrar) {

            if (registrar.activity() == null) {
                // If a background flutter view tries to register the plugin, there will be no activity from the registrar,
                // we stop the registering process immediately because the ImagePicker requires an activity.
                return
            }

            val channel = MethodChannel(registrar.messenger(), "io.wongxd.flutter.phone_contact")
            channel.setMethodCallHandler(PhoneContactPlugin(registrar.activity()))
        }
    }


    override fun onMethodCall(call: MethodCall, result: Result) {

        when (call.method) {

            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            "getContactList" -> {
                getPermissions(aty, PermissionType.READ_CONTACTS, allGranted = {
                    val list: List<ContactUtil.Contact> = ContactUtil.readContacts(aty)
                    val bean = ContactUtil.ContactListBean().apply { this.list = list }
                    result.success(Gson().toJson(bean))
                })
            }

            "addContact" -> {
                getPermissions(aty, PermissionType.WRITE_CONTACTS, allGranted = {
                    val jsonStr = call.arguments as String
                    val obj = JSONObject(jsonStr)
                    val name = obj.optString("contact_name")
                    val telephoneNumbers = obj.optJSONArray("telephoneNumbers")
                    val phoneList: MutableList<String> = mutableListOf()
                    for (i in 0 until telephoneNumbers.length()) {
                        phoneList.add(telephoneNumbers.optString(i))
                    }
                    val resultFlag:Boolean = ContactUtil.writeConstact(aty, name, *phoneList.toTypedArray())

                    result.success(resultFlag)
                })
            }

            "getCallLogList" -> {
                getPermissions(aty, PermissionType.READ_CALL_LOG, allGranted = {
                    val list: List<ContactUtil.CallLogInfo> = ContactUtil.readCallLog(aty)
                    val bean = ContactUtil.CallLogInfoListBean().apply { this.list = list }
                    result.success(Gson().toJson(bean))
                })
            }

//            "writeCallLog" -> {
//                getPermissions(aty, PermissionType.WRITE_CALL_LOG, allGranted = {
//                    val jsonStr = call.arguments as String
//                    val info: ContactUtil.CallLogInfo = Gson().fromJson(jsonStr, ContactUtil.CallLogInfo::class.java)
//
//                })
//            }

            else -> {
                result.notImplemented()
            }

        }
    }
}
