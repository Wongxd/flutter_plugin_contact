#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'phone_contact'
  s.version          = '0.0.1'
  s.summary          = 'Flutter plugin to get contacts.'
  s.description      = <<-DESC
A new Flutter plugin.
                       DESC
  s.homepage         = 'https://github.com/Wongxd/flutter_plugin_contact'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'wongxd' => 'wxd1@live.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'

  s.ios.deployment_target = '9.0'
end

