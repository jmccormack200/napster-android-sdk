# Napster Android SDK

## Current Version
The current version of the Napster Android SDK is 2.0.

## Introduction
The Napster Android SDK was designed to provide a very easy way to integrate streaming music into your Android application. The SDK itself handles playback and is used in conjunction with the [Napster Developer API](http://developer.napster.com) to give your users access to over millions of tracks. The SDK plays full-length tracks for authenticated Napster subscribers.

We have provided here the SDK ([napster-sdk-1.2.0.jar](https://github.com/Napster/napster-android-sdk/blob/master/NapsterSdk_1.2.jar)) and two sample applications inside the SampleProject which provide examples you can use to build your own app with Napster streaming music.

## Requirements
- AndroidStudio 1.0 or higher
- Android SDK 4.0 (API 14) or higher
- Android BuildTools 21.1.2 for the SampleProject (Recommend using latest available)
- android-support-v4.jar
- Picasso (SampleProject only)
- Retrofit
- gson

#### SDK Only
The Napster Android SDK is available as a jar. You can download the [napster-sdk-1.2.0.jar](https://github.com/Napster/napster-android-sdk/blob/master/NapsterSdk_1.2.jar) and add it to your Android Studio project gradle.build file:

```groovy
compile files('libs/napster-sdk-1.2.0.jar')
```

#### SampleProject

Using Import Project in Android Studio, select the SampleProject folder. After importing, run gradle sync, and you should be ready to build and deploy the sample apps to your device.

You might be asked to install additional components such as build tools. You may either install them or change the required version in the build.gradle to one you have installed already.


#### AndroidManifest information
The Napster Android SDK uses these permissions:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

Permissions explained

android.permission.INTERNET - We use this to access Internet to send and receive data.

android.permission.WAKE_LOCK - We use this permission to keep the device from turning off the network during playback if the app is backgrounded.

android.permission.READ_PHONE_STATE - We use this to properly handle audio during events such as phone calls.

android.permission.ACCESS_NETWORK_STATE - We use this to properly handle loss of connectivity.

Add the NapsterPlaybackService to the manifest:
```xml
<service android:name="com.napster.player.NapsterPlaybackService" />
```
Also add this receiver:
```xml
<receiver android:name="com.napster.cedar.NetworkConnectivityReceiver">
	<intent-filter>
		<action android:name="android.net.conn.CONNECTIVITY_CHANGE"/>
	</intent-filter>
</receiver>
```
In order for the SDK to receive key events (e.g. from lock screen controls, Bluetooth headsets, etc) add the com.napster.player.MediaButtonReceiver to the manifest.
```xml
<receiver android:name="com.napster.player.MediaButtonReceiver" >
	<intent-filter>
		<action android:name="android.intent.action.MEDIA_BUTTON" />
	</intent-filter>
</receiver>
```

#### Notifications
If you want to use transport controls in the notification:
- make your notification layouts (expanded, standard or both)
- implement AbstractNotificationProperties and set them:
```java
player.setNotificationProperties(myNotificationProperties)
```
- implement NotificationActionListener and set it:
```java
player.registerNotificationActionListener(myNotificationActionListener)
```

##### Notifications notes
- ExpandedNotification can only be used in SDK 16 and up
- Due to issues on certain devices, you may only see a standard notification even though you have defined a layout with transport controls.


#### ProGuard/DexGuard
If you use ProGuard or DexGuard, you should add these to your properties file:
```code
-keep class com.napster.cedar.** { *; }
-keep class com.napster.player.** { *; }
-keep interface com.napster.cedar.** { *; }
-keep interface com.napster.player.** { *; }
```
