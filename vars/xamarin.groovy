package org.foo
xamarinEnv
def construct(String android_project_location, String ios_project_location, String version_name, String version_number, String input_apk, String final_apk, String final_ipa, String keystore_file, String keystore_alias, String keystore_password, String android_app_name, String android_distribution_groups, String android_owner_name, String android_application_path, String ios_app_name, String ios_distribution_groups, String ios_owner_name, String ios_application_path){
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
            keystore_password: keystore_password,
            android_app_name: android_app_name, 
            android_distribution_groups: android_distribution_groups,
            android_owner_name: android_owner_name,
            android_application_path: android_application_path,
            ios_app_name: ios_app_name, 
            ios_distribution_groups: ios_distribution_groups,
            ios_owner_name: ios_owner_name,
            ios_application_path: ios_application_path
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

def deploy_android_app(){
    withCredentials([string(credentialsId: 'acima_appcenter_api_token', variable: 'acima_appcenter_api_token')]){
        appCenter apiToken: "$acima_appcenter_api_token", appName: "${xamarinEnv.android_app_name}", branchName: '', buildVersion: '', commitHash: '', distributionGroups: "${xamarinEnv.android_distribution_groups}", mandatoryUpdate: false, notifyTesters: false, ownerName: "${xamarinEnv.android_owner_name}", pathToApp: "${xamarinEnv.android_application_path}", pathToDebugSymbols: '', pathToReleaseNotes: '', releaseNotes: ''
    }
}

def deploy_ios_app(){
    withCredentials([string(credentialsId: 'acima_appcenter_api_token', variable: 'acima_appcenter_api_token')]){
        appCenter apiToken: "$acima_appcenter_api_token", appName: "${xamarinEnv.ios_app_name}", branchName: '', buildVersion: '', commitHash: '', distributionGroups: "${xamarinEnv.ios_distribution_groups}", mandatoryUpdate: false, notifyTesters: false, ownerName: "${xamarinEnv.ios_owner_name}", pathToApp: "${xamarinEnv.ios_application_path}", pathToDebugSymbols: '', pathToReleaseNotes: '', releaseNotes: ''
    }
}
