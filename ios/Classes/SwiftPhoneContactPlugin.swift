import Flutter
import UIKit
import Contacts

public class SwiftPhoneContactPlugin: NSObject, FlutterPlugin {

    public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "io.wongxd.flutter.phone_contact", binaryMessenger: registrar.messenger())
    let instance = SwiftPhoneContactPlugin()

    registrar.addMethodCallDelegate(instance, channel: channel)
 }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      print(call)
     if("getContactList" == call.method){
    //         print("ios--getContactList----")
            if #available(iOS 9.0, *) {
                CNContactStore().requestAccess(for: .contacts) { (isRight, error) in
                    if isRight {
                        //授权成功加载数据。
                        print("start-ios--getContactList----")
                        loadContactsData(result: result);
                    }
                }
            } else {
                // Fallback on earlier versions
            }
        }
        else if("addContact" == call.method){

           //申请权限
    //     CNContactStore().requestAccess(for: .contacts) { (isRight, error) in
    //         if isRight {
        
    //             let arg = call.arguments
                
    //             if  arg as String{
    //             let jsonString = String(data: arg, encoding: String.Encoding.utf8){
    //             print(jsonData);
    //             if let people = try? JSONDecoder().decode(ContactBean.self, from: jsonData) {
    //             //授权成功添加数据。
    //             addContact(result,people)
    //         }

    //             }
                
    //         }
    //     }
    // }

            result(FlutterMethodNotImplemented);
        }
        else if("getCallLogList" == call.method){
            result(FlutterMethodNotImplemented);
        }
        else if("getPlatformVersion" == call.method){
            result("iOS " + UIDevice.current.systemVersion)
        }
        else {
            result(FlutterMethodNotImplemented);
        }
    }
}



struct CallLogBean :Codable{
    
    var name = "";
    var number = "";
    var date = "";
    var type = 1; // 来电:1，拨出:2,未接:3
    var time = ""; //通话时长
    var location = ""; //归属地
}

struct CallLogListBean:Codable{
    var list :[CallLogBean] = [];
}





struct ContactBean :Codable{
    
    var contact_id:Int = 0;
    var contact_name:String = "";
    var  lookup_key:String = "";
    var telephoneNumbers:[String] = [];
    var sort_key_primary:String = "";
    var location = "";
    var tagIndex = "" ;
    var pinyin = "";
}

struct ContactListBean:Codable{
    var list :[ContactBean] = [];
}

    //新增一个联系人
func addContact(result: FlutterResult,contactBean: ContactBean) {
        //创建通讯录对象
        let store = CNContactStore()

        //创建CNMutableContact类型的实例
        let contactToAdd = CNMutableContact()
        //设置姓名
        contactToAdd.givenName = contactBean.contact_name

        // //设置昵称
        // contactToAdd.nickname = "x"

        // //设置头像
        // let image = UIImage(named: "x")!
        // contactToAdd.imageData = UIImagePNGRepresentation(image)

        //设置电话
        // var phones: = []
        // for phone in contactBean.telephoneNumbers {
        // let mobileNumber = CNPhoneNumber(stringValue: phone)
        // let mobileValue = CNLabeledValue(label: CNLabelPhoneNumberMobile,  value: mobileNumber)
        // phones.append(mobileValue);
        // }
        
        // contactToAdd.phoneNumbers = phones

        // //设置email
        // let email = CNLabeledValue(label: CNLabelHome, value: "xx@xx.com" as NSString)
        // contactToAdd.emailAddresses = [email]

        //添加联系人请求
        let saveRequest = CNSaveRequest()
        saveRequest.add(contactToAdd, toContainerWithIdentifier: nil)

        do {
            //写入联系人
            try store.execute(saveRequest)
            print("保存成功!")
            result(true)
        } catch {
            print(error)
            result(false)
        }
    }



func loadContactsData(result: FlutterResult) {
    if #available(iOS 9.0, *) {
    //获取授权状态
    let status = CNContactStore.authorizationStatus(for: .contacts)
    //判断当前授权状态
    guard status == .authorized else { return }
    
    //创建通讯录对象
    let store = CNContactStore()
    
    //获取Fetch,并且指定要获取联系人中的什么属性
    let keys = [CNContactFamilyNameKey,CNContactMiddleNameKey, CNContactGivenNameKey, CNContactNicknameKey,
                CNContactOrganizationNameKey, CNContactJobTitleKey,
                CNContactDepartmentNameKey, CNContactNoteKey, CNContactPhoneNumbersKey,
                CNContactEmailAddressesKey, CNContactPostalAddressesKey,
                CNContactDatesKey, CNContactInstantMessageAddressesKey
    ]
    
    //创建请求对象
    //需要传入一个(keysToFetch: [CNKeyDescriptor]) 包含CNKeyDescriptor类型的数组
    let request = CNContactFetchRequest(keysToFetch: keys as [CNKeyDescriptor])
    var contactList : [ContactBean] = [];
    
    //遍历所有联系人
    do {
        try store.enumerateContacts(with: request, usingBlock: {
            (contact : CNContact, stop : UnsafeMutablePointer<ObjCBool>) -> Void in
            var contactBean = ContactBean();
            
            //获取姓名
            let lastName = contact.familyName
            let middleName = contact.middleName
            let firstName = contact.givenName
//                            print("姓名：\(lastName)\(middleName)\(firstName)")
            contactBean.contact_name = lastName + middleName + firstName;
            
            //获取昵称
//            let nikeName = contact.nickname
//            print("昵称：\(nikeName)")
            
            
            //获取公司（组织）
//            let organization = contact.organizationName
//            print("公司（组织）：\(organization)")
            
            //获取职位
//            let jobTitle = contact.jobTitle
//            print("职位：\(jobTitle)")
            
            //获取部门
//            let department = contact.departmentName
//            print("部门：\(department)")
            
            //获取备注
//            let note = contact.note
//            print("备注：\(note)")
            
            var phones:[String] = []
            //获取电话号码
//            print("电话：")
            for phone in contact.phoneNumbers {
                //获得标签名（转为能看得懂的本地标签名，比如work、home）
                var label = "未知标签"
                if phone.label != nil {
                    label = CNLabeledValue<NSString>.localizedString(forLabel:
                        phone.label!)
                }
                
                //获取号码
                let value = phone.value.stringValue
//                print("\t\(label)：\(value)")
                phones.append(value)
            }
            contactBean.telephoneNumbers=phones
            
            //获取Email
//            print("Email：")
//            for email in contact.emailAddresses {
//                //获得标签名（转为能看得懂的本地标签名）
//                var label = "未知标签"
//                if email.label != nil {
//                    label = CNLabeledValue<NSString>.localizedString(forLabel:
//                        email.label!)
//                }
//
//                //获取值
//                let value = email.value
//                print("\t\(label)：\(value)")
//            }
            
            //获取地址
//            print("地址：")
            for address in contact.postalAddresses {
                //获得标签名（转为能看得懂的本地标签名）
                var label = "未知标签"
                if address.label != nil {
                    label = CNLabeledValue<NSString>.localizedString(forLabel:
                        address.label!)
                }
                
                //获取值
                let detail = address.value
                // let contry = detail.value(forKey: CNPostalAddressCountryKey) ?? ""
                // let state = detail.value(forKey: CNPostalAddressStateKey) ?? ""
                let city = detail.value(forKey: CNPostalAddressCityKey) ?? ""
                // let street = detail.value(forKey: CNPostalAddressStreetKey) ?? ""
                // let code = detail.value(forKey: CNPostalAddressPostalCodeKey) ?? ""
                // let str = "国家:\(contry) 省:\(state) 城市:\(city) 街道:\(street)"
//                    + " 邮编:\(code)"
//                print("\t\(label)：\(str)")
                let location = "\(city)"
                contactBean.location = location
            }
            
//           // 获取纪念日
//            print("纪念日：")
//            for date in contact.dates {
//                //获得标签名（转为能看得懂的本地标签名）
//                var label = "未知标签"
//                if date.label != nil {
//                    label = CNLabeledValue<NSString>.localizedString(forLabel:
//                        date.label!)
//                }
//
//                //获取值
//                let dateComponents = date.value as DateComponents
//                let value = NSCalendar.current.date(from: dateComponents)
//                let dateFormatter = DateFormatter()
//                dateFormatter.dateFormat = "yyyy年MM月dd日 HH:mm:ss"
//                print("\t\(label)：\(dateFormatter.string(from: value!))")
//            }
//
//            //获取即时通讯(IM)
//            print("即时通讯(IM)：")
//            for im in contact.instantMessageAddresses {
//                //获得标签名（转为能看得懂的本地标签名）
//                var label = "未知标签"
//                if im.label != nil {
//                    label = CNLabeledValue<NSString>.localizedString(forLabel:
//                        im.label!)
//                }
//
//                //获取值
//                let detail = im.value
//                let username = detail.value(forKey: CNInstantMessageAddressUsernameKey)
//                    ?? ""
//                let service = detail.value(forKey: CNInstantMessageAddressServiceKey)
//                    ?? ""
//                print("\t\(label)：\(username) 服务:\(service)")
//            }
            
//            print("----------------")
            contactList.append(contactBean)
        })
    } catch {
        print(error)
    }
    var bean = ContactListBean()
    bean.list = contactList
    
//    print(bean)
    let encoder = JSONEncoder()
    if  let jsonData = try? encoder.encode(bean){
        
        if let jsonString = String(data: jsonData, encoding: String.Encoding.utf8) {
            
            print("----------------开始编码通讯录信息成json--------------------")
            print(jsonString)
            result(String(jsonString))
        }
    }
    }
    else{
        
    }
}


