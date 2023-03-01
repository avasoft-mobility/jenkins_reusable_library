package org.foo
xamarinEnv
def construct(String android_project_location, String ios_project_location, String version_name, String version_number, String input_apk, String final_apk, String final_ipa, String keystore_file, String keystore_alias, String keystore_password){
    xamarinEnv = [
            android_project_location : android_project_location,
            ios_project_location: ios_project_location,
            version_name: version_name,
            version_number: version_number,
            input_apk: input_apk,
            final_apk: final_apk,
            final_ipa: final_ipa,
            keystore_file: keystore_file,
            keystore_alias: keystore_alias,
            keystore_password: keystore_password
    ]
}

def build_android_app(){
    sh "\"${tool 'xbuild'}\" ${xamarinEnv.android_project_location} /p:Configuration=Release /t:PackageForAndroid /p:MonoSymbolArchive=False /p:SetVersion=True /p:VersionNumber=${xamarinEnv.version_name} /p:BuildNumber=${xamarinEnv.version_number}"
    sh "zipalign -f -v 4 ${xamarinEnv.input_apk} ${xamarinEnv.final_apk}"
    sh "apksigner sign --ks ${xamarinEnv.keystore_file} --ks-key-alias ${xamarinEnv.keystore_alias} --ks-pass pass:${xamarinEnv.keystore_password} ${xamarinEnv.final_apk}"
}

def build_ios_app(){
    sh "PlistBuddy acima_mbl_app/acima_mbl_app.iOS/info.plist -c \"set :CFBundleShortVersionString ${xamarinEnv.version_name}\""
    sh "PlistBuddy acima_mbl_app/acima_mbl_app.iOS/info.plist -c \"set :CFBundleVersion ${xamarinEnv.version_number}\""
    sh "\"${tool 'xbuild'}\" ${xamarinEnv.ios_project_location} /p:BuildIpa=True /p:IpaPackageDir=$WORKSPACE /p:IpaPackageName=${xamarinEnv.final_ipa} /p:Platform=iPhone /p:Configuration=Release /t:Build"
}
