Pod::Spec.new do |spec|
    spec.name                     = 'SharedAnalytics'
    spec.version                  = '0.5.0'
    spec.homepage                 = 'manychat.com'
    spec.source                   = { :git => 'https://github.com/aleksiosdev/SharedAnalyticsExample.git', :tag => "v#{spec.version}" }
    spec.authors                  = 'Lavrinenko Aleksandr'
    spec.license                  = ''
    spec.summary                  = 'Framework with events and send analytics logic'

    spec.static_framework         = true
    spec.vendored_frameworks      = "build/cocoapods/SharedAnalytics.framework"
    spec.libraries                = "c++"
    spec.preserve_paths           = "**/*.*"
    spec.module_name              = "#{spec.name}_umbrella"

            

    spec.pod_target_xcconfig = {
        'KOTLIN_TARGET[sdk=iphonesimulator*]' => 'ios_x64',
        'KOTLIN_TARGET[sdk=iphoneos*]' => 'ios_arm',
    }

    spec.prepare_command = <<-SCRIPT
       set -ev
       ./gradlew --no-daemon 'releaseFatFramework' --stacktrace --info
    SCRIPT
end