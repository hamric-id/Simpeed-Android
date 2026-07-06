# Simpeed App 🚀
Simpeed is a modern Android and Wear OS speedometer app built with **Kotlin** and **Jetpack Compose**, following **Clean Architecture** with **MVVM + UDF**

## 🚀 Getting Started
1. Clone the Repository
2. put your 'google-services.json' to module app and app-wear 

## 📋 How to get google-services.json
1. open your firebase project on firebase console https://console.firebase.google.com (create project first if you dont have)
2. make sure package name on firebase project same like on build.gradle.kts
3. configure Firebase Auth (Product Categories>Security>Authenctication) with Sign-in provider: Google
4. put SHA1 certificate fingerprints on Settings>General>Add fingerprint
5. download 'google-services.json' on general tab of settings

## 📋 How to get SHA1 certificate fingerprints
1. open terminal on your android studio project path
2. run this: ./gradlew signingReport
3. find the SHA1
