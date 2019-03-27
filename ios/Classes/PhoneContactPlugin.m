#import "PhoneContactPlugin.h"
#import <phone_contact/phone_contact-Swift.h>

@implementation PhoneContactPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPhoneContactPlugin registerWithRegistrar:registrar];
}
@end
