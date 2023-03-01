package org.foo
xamarinEnv
def construct(String android_project_location, String ios_project_location, String version_name, String version_number){
    xamarinEnv = [
            android_project_location : android_project_location,
            ios_project_location: ios_project_location,
            version_name: version_name,
            version_number: version_number
    ]
}

def build_android_app(){
    sh "\"${tool 'xbuild'}\" ${xamarinEnv.android_project_location} /p:Configuration=Release /t:PackageForAndroid /p:MonoSymbolArchive=False /p:SetVersion=True /p:VersionNumber=${xamarinEnv.version_name} /p:BuildNumber=${xamarinEnv.version_number}"
    sh "zipalign -f -v 4 $INPUT_APK $FINAL_APK"
    sh "apksigner sign --ks $KEYSTORE_FILE --ks-key-alias $KEYSTORE_ALIAS --ks-pass pass:$STORE_PASS $FINAL_APK"
}
