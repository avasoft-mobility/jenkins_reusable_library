package org.foo
xamarinEnv
toolsEnv

/*

Tools Used:
    1. MSBuild (To build the Application)
    2. PlistBuddy (To update values in Info.plist)
    3. ZipAlign (To compress the APK Generated)
    4. JarSigner (To sign the APK Generated)
*/


def constructXamarinBuild(String android_project_location, String ios_project_location, String version_name, String version_number){
    xamarinEnv = [
            android_project_location : android_project_location,
            ios_project_location: ios_project_location,
            version_name: version_name,
            version_number: version_number
    ]
}

def build_android_app(){
    sh "\"${tool 'xbuild'}\" ${xamarinEnv.android_project_location} /p:Configuration=Release /t:PackageForAndroid /p:MonoSymbolArchive=False /p:SetVersion=True /p:VersionNumber=${xamarinEnv.version_name} /p:BuildNumber=${xamarinEnv.version_number}"
}

def build_ios_app(){
    sh "/usr/libexec/PlistBuddy acima_mbl_app/acima_mbl_app.iOS/info.plist -c \"set :CFBundleShortVersionString $version_name\""
    sh "/usr/libexec/PlistBuddy acima_mbl_app/acima_mbl_app.iOS/info.plist -c \"set :CFBundleVersion $version_number\""
    sh "\"${tool 'xbuild'}\" ${xamarinEnv.ios_project} /p:BuildIpa=True /p:IpaPackageDir=$WORKSPACE /p:IpaPackageName=$FINAL_IPA /p:Platform=iPhone /p:Configuration=Release /t:Build"
}
