# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# Picasso
-dontwarn com.squareup.okhttp.**


-keep class com.javaprogramming.quiz.utilities.** { *; }
-keep class com.javaprogramming.quiz.model.** { *; }

-keep class com.loopj.android.** { *; }
-keep interface com.loopj.android.** { *; }


-keep class cn.pedant.SweetAlert.** { *; }

-keep class com.google.firebase.messaging.FirebaseMessaging


# OneSignal - General
-keep class com.onesignal.** {*;}  # Keep all OneSignal classes and members


-keep class com.onesignal.ActivityLifecycleListenerCompat** {*;}

# Observer backcall methods are called with reflection
-keep class com.onesignal.OSSubscriptionState {
    void changed(com.onesignal.OSPermissionState);
}


-keep class com.onesignal.OSPermissionChangedInternalObserver {
    void changed(com.onesignal.OSPermissionState);
}

-keep class com.onesignal.OSSubscriptionChangedInternalObserver {
    void changed(com.onesignal.OSSubscriptionState);
}

-keep class com.onesignal.OSEmailSubscriptionChangedInternalObserver {
    void changed(com.onesignal.OSEmailSubscriptionState);
}

-keep class com.onesignal.OSSMSSubscriptionChangedInternalObserver {
    void changed(com.onesignal.OSSMSSubscriptionState);
}

-keep class ** implements com.onesignal.OSPermissionObserver {
    void onOSPermissionChanged(com.onesignal.OSPermissionStateChanges);
}

-keep class ** implements com.onesignal.OSSubscriptionObserver {
    void onOSSubscriptionChanged(com.onesignal.OSSubscriptionStateChanges);
}


-keep class ** implements com.onesignal.OSEmailSubscriptionObserver {
    void onOSEmailSubscriptionChanged(com.onesignal.OSEmailSubscriptionStateChanges);
}

-keep class ** implements com.onesignal.OSSMSSubscriptionObserver {
    void onOSEmailSubscriptionChanged(com.onesignal.OSSMSSubscriptionStateChanges);
}


-keep class * implements com.onesignal.notifications.IPermissionObserver{
    void onNotificationPermissionChange(java.lang.Boolean);
}

-keep class * implements com.onesignal.user.subscriptions.IPushSubscriptionObserver {
    void onPushSubscriptionChange(com.onesignal.user.subscriptions.PushSubscriptionChangedState);
}

-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.AdwHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.ApexHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.AsusHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.DefaultBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.EverythingMeHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.HuaweiHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.LGHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.NewHtcHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.NovaHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.OPPOHomeBader { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.SamsungHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.SonyHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.VivoHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.XiaomiHomeBadger { <init>(...); }
-keep class com.onesignal.notifications.internal.badges.impl.shortcutbadger.impl.ZukHomeBadger { <init>(...); }



-dontwarn com.amazon.**

# Proguard ends up removing this class even if it is used in AndroidManifest.xml so force keeping it.
-keep public class com.onesignal.notifications.services.ADMMessageHandler {*;}

-keep class com.onesignal.JobIntentService$* {*;}

-keep class ** implements com.onesignal.notifications.INotificationServiceExtension{
   void onNotificationReceived(com.onesignal.notifications.INotificationReceivedEvent);
 }

#-keep class com.onesignal.OneSignalUnityProxy {*;}