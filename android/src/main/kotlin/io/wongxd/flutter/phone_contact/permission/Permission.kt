package io.wongxd.flutter.phone_contact.permission

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast


//
//
// 危险权限
//group:android.permission-group.CONTACTS
//permission:android.permission.WRITE_CONTACTS
//permission:android.permission.GET_ACCOUNTS
//permission:android.permission.READ_CONTACTS
//
//group:android.permission-group.PHONE
//permission:android.permission.READ_CALL_LOG
//permission:android.permission.READ_PHONE_STATE
//permission:android.permission.CALL_PHONE
//permission:android.permission.WRITE_CALL_LOG
//permission:android.permission.USE_SIP
//permission:android.permission.PROCESS_OUTGOING_CALLS
//permission:com.android.voicemail.permission.ADD_VOICEMAIL
//
//group:android.permission-group.CALENDAR
//permission:android.permission.READ_CALENDAR
//permission:android.permission.WRITE_CALENDAR
//
//group:android.permission-group.CAMERA
//permission:android.permission.CAMERA
//
//group:android.permission-group.SENSORS
//permission:android.permission.BODY_SENSORS
//
//group:android.permission-group.LOCATION
//permission:android.permission.ACCESS_FINE_LOCATION
//permission:android.permission.ACCESS_COARSE_LOCATION
//
//group:android.permission-group.STORAGE
//permission:android.permission.READ_EXTERNAL_STORAGE
//permission:android.permission.WRITE_EXTERNAL_STORAGE
//
//group:android.permission-group.MICROPHONE
//permission:android.permission.RECORD_AUDIO
//
//group:android.permission-group.SMS
//permission:android.permission.READ_SMS
//permission:android.permission.RECEIVE_WAP_PUSH
//permission:android.permission.RECEIVE_MMS
//permission:android.permission.RECEIVE_SMS
//permission:android.permission.SEND_SMS
//permission:android.permission.READ_CELL_BROADCASTS

/**
 * Created by wongxd on 2018/12/25.
 * https://github.com/wongxd
 * wxd1@live.com
 */
enum class PermissionType(val permission: String, val permissionName: String) {
    READ_CONTACTS(Manifest.permission.READ_CONTACTS, "读取联系人"),
    WRITE_CONTACTS(Manifest.permission.WRITE_CONTACTS, "写入联系人"),
    GET_ACCOUNTS(Manifest.permission.GET_ACCOUNTS, "读取账户"),


    READ_CALL_LOG(Manifest.permission.READ_CALL_LOG, "读取通话记录"),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE, "读取手机状态"),
    CALL_PHONE(Manifest.permission.CALL_PHONE, "拨打电话"),
    WRITE_CALL_LOG(Manifest.permission.WRITE_CALL_LOG, "写入通话记录"),
    USE_SIP(Manifest.permission.USE_SIP, "获得用户SIP"),
    PROCESS_OUTGOING_CALLS(Manifest.permission.PROCESS_OUTGOING_CALLS, "处理呼出电话"),
    ADD_VOICEMAIL(Manifest.permission.ADD_VOICEMAIL, "添加语音邮件"),


    READ_CALENDAR(Manifest.permission.READ_CALENDAR, "读取日历"),
    WRITE_CALENDAR(Manifest.permission.WRITE_CALENDAR, "写入日历"),


    CAMERA(Manifest.permission.CAMERA, "拍照"),



    BODY_SENSORS(Manifest.permission.BODY_SENSORS, "设备传感器"),


    FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION, "获取精确位置"),
    COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION, "获取大致位置"),


    READ_EXTERNAL_STORAGE(Manifest.permission.READ_EXTERNAL_STORAGE, "读取存储卡"),
    WRITE_EXTERNAL_STORAGE(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写入存储卡"),


    RECORD_AUDIO(Manifest.permission.RECORD_AUDIO, "录音"),


    READ_SMS(Manifest.permission.READ_SMS, "读取短信"),
    RECEIVE_WAP_PUSH(Manifest.permission.RECEIVE_WAP_PUSH, "接收 WAP_PUSH"),
    RECEIVE_MMS(Manifest.permission.RECEIVE_MMS, "接收MMS"),
    RECEIVE_SMS(Manifest.permission.RECEIVE_SMS, "接收SMS"),
    SEND_SMS(Manifest.permission.SEND_SMS, "发送SMS"),


    ACCESS_WIFI_STATE(Manifest.permission.ACCESS_WIFI_STATE, "访问WiFi网络信息"),
    ACCESS_NETWORK_STATE(Manifest.permission.ACCESS_NETWORK_STATE, "获取网络状态");
}


fun goSetting(ctx: Context) {
    val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS")
    intent.data = Uri.parse("package:${ctx.applicationContext.packageName}")
    ctx.startActivity(intent)
}


/**
 * 获取权限
 */
fun getPermissions(
        aty: Activity,
        vararg per: PermissionType,
        isGoSetting: Boolean = true,
        allGranted: () -> Unit = {},
        eachResult: (PermissionActivityResult.Permission) -> Unit = {}
) {


    val permissionReq = PermissionActivityResult.getOne(aty.fragmentManager)

    permissionReq.reqPermission(per.map { it.permission }.toTypedArray()) { isAllGranted, perList ->
        if (isAllGranted) {
            allGranted.invoke()
            return@reqPermission
        }

        perList.forEach { permission ->
            Log.e("permission", permission.perStr)
            if (permission.granted) {
                // `permission.name` is granted !
                eachResult.invoke(permission)
            } else if (permission.shouldShowRequestPermissionRationale) {
                // Denied permission without ask never again
                eachResult.invoke(permission)
               Toast.makeText(aty.applicationContext,permission.name + " 权限被禁止，无法进行操作",Toast.LENGTH_SHORT).show()
            } else {
                // Denied permission with ask never again
                // Need to go to the settings
                if (isGoSetting) {
                    Toast.makeText(aty.applicationContext,permission.name + " 权限被禁止，无法进行操作",Toast.LENGTH_SHORT).show()
                    goSetting(aty)
//                    val dlg: SweetAlertDialog = SweetAlertDialog(aty, SweetAlertDialog.WARNING_TYPE)
//                            .also {
//                                it.titleText = "有如下权限被禁止"
//                                val sb = StringBuilder()
//                                sb.append("${permission.name}\n")
//                                sb.append("(将会导致应用不能正常运行)")
//                                it.contentText = sb.toString()
//                                it.confirmText = "前往设置给予权限"
//                                it.setConfirmClickListener { _ ->
//                                    goSetting(aty)
//                                }
//                            }
//                    dlg.setCancelable(true)
//                    dlg.show()
                }
            }
        }
    }

}


//###########################具体实现###########################################

class PermissionActivityResult {

    private val TAG = "PermissionActivityResult"
    private var mPermissionActivityResultFragment: PermissionActivityResultFragment? = null


    companion object {

        fun getOne(fm: FragmentManager): PermissionActivityResult {
            val one = PermissionActivityResult()
            one.mPermissionActivityResultFragment = one.getOnResultFragment(fm)
            return one
        }

    }


    fun reqPermission(permissions: Array<String>, eachResult: PermissionResultCallback) {
        requestPermissionsFromFragment(permissions, eachResult)
    }


    @TargetApi(Build.VERSION_CODES.M)
    private fun requestPermissionsFromFragment(permissions: Array<String>, lis: PermissionResultCallback) {
        mPermissionActivityResultFragment?.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions))
        mPermissionActivityResultFragment?.requestPermissions(permissions, lis)
    }

    /**
     * Returns true if the permission is already granted.
     *
     *
     * Always true if SDK < 23.
     */
    fun isGranted(permission: String): Boolean {
        return !isMarshmallow() || mPermissionActivityResultFragment?.isGranted(permission)!!
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     *
     *
     * Always false if SDK < 23.
     */
    fun isRevoked(permission: String): Boolean {
        return isMarshmallow() && mPermissionActivityResultFragment?.isRevoked(permission)!!
    }

    internal fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }


    fun setLogging(logging: Boolean) {
        mPermissionActivityResultFragment?.setLogging(logging)
    }

    private fun getOnResultFragment(fragmentManager: FragmentManager): PermissionActivityResultFragment {
        var simpleOnResultFragment: PermissionActivityResultFragment? =
                findSimpleOnResultFragment(fragmentManager)
        if (simpleOnResultFragment == null) {
            simpleOnResultFragment = PermissionActivityResultFragment()
            fragmentManager
                    .beginTransaction()
                    .add(simpleOnResultFragment, TAG)
                    .commitAllowingStateLoss()
            fragmentManager.executePendingTransactions()
        }
        return simpleOnResultFragment
    }

    private fun findSimpleOnResultFragment(fragmentManager: FragmentManager): PermissionActivityResultFragment? {
        return fragmentManager.findFragmentByTag(TAG) as PermissionActivityResultFragment?
    }


    class Permission(
            val perStr: String,
            val name: String,
            val granted: Boolean,
            val shouldShowRequestPermissionRationale: Boolean = false
    ) {

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false

            val that = o as Permission?

            if (granted != that?.granted) return false
            return if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale) false else name == that.name
        }

        override fun hashCode(): Int {
            var result = name.hashCode()
            result = 31 * result + if (granted) 1 else 0
            result = 31 * result + if (shouldShowRequestPermissionRationale) 1 else 0
            return result
        }

        override fun toString(): String {
            return "Permission{" +
                    "name='" + name + '\''.toString() +
                    ", granted=" + granted +
                    ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                    '}'.toString()
        }

    }


    /**
     *
     * @Description: 真正调用 startActivity 和处理 onActivityResult 的类。
     */
    class PermissionActivityResultFragment : Fragment() {

        private val TAG = "Permission"
        private val PERMISSIONS_REQUEST_CODE = 42
        private var mLogging: Boolean = true

        private var listener: PermissionResultCallback? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            retainInstance = true
        }


        @TargetApi(Build.VERSION_CODES.M)
        internal fun requestPermissions(permissions: Array<String>, lis: PermissionResultCallback) {
            listener = lis
            log("请求权限  " + TextUtils.join(",", permissions))
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            if (requestCode != PERMISSIONS_REQUEST_CODE) return

            log("获取权限结果  " + TextUtils.join(",", permissions))

            val shouldShowRequestPermissionRationale = BooleanArray(permissions.size)

            for (i in permissions.indices) {
                shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i])
            }

            val isAllGranted = permissions.size == grantResults.size
            val permissionList: MutableList<Permission> = mutableListOf()

            listener?.let { lis ->

                permissions.forEachIndexed { index, per ->
                    val granted = grantResults[index] == PackageManager.PERMISSION_GRANTED
                    val rationale = shouldShowRequestPermissionRationale[index]
                    permissionList.add(Permission(per, getNameByPermissionStr(per), granted, rationale))
                }

                lis.invoke(isAllGranted, permissionList)
            }

        }

        private fun getNameByPermissionStr(perStr: String): String {
            return try {
                PermissionType.values().first { it.permission == perStr }.permissionName
            } catch (e: Exception) {
                e.printStackTrace()
                "Unknown Permission"
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        internal fun isGranted(permission: String): Boolean {
            return activity?.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        }

        @TargetApi(Build.VERSION_CODES.M)
        internal fun isRevoked(permission: String): Boolean? {
            return activity?.packageManager?.isPermissionRevokedByPolicy(permission, activity?.packageName)
        }


        fun setLogging(logging: Boolean) {
            mLogging = logging
        }

        internal fun log(message: String) {
            if (mLogging) {
                Log.e(TAG, message)
            }
        }
    }

}

/**
 *
 * 是否获得全部权限
 *
 * perList
 *
 */
private typealias  PermissionResultCallback = (Boolean, List<PermissionActivityResult.Permission>) -> Unit



