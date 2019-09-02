# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/mac/Desktop/mac_android/sdkmac/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


#-------------------------------------------定制化区域----------------------------------------------
#---------------------------------1.实体类---------------------------------

-keep class com.nevs.car.model.** { *; }

#-------------------------------------------------------------------------

 #############################################
 #
 # 对于一些基本指令的添加
 #
 #############################################
 # 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
 -optimizationpasses 5

 # 混合时不使用大小写混合，混合后的类名为小写
 -dontusemixedcaseclassnames

 # 指定不去忽略非公共库的类
 -dontskipnonpubliclibraryclasses

 # 这句话能够使我们的项目混淆后产生映射文件
 # 包含有类名->混淆后类名的映射关系
 -verbose

 # 指定不去忽略非公共库的类成员
 -dontskipnonpubliclibraryclassmembers

 # 不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
 -dontpreverify

 # 保留Annotation不混淆
 -keepattributes *Annotation*,InnerClasses

 # 避免混淆泛型
 -keepattributes Signature

 # 抛出异常时保留代码行号
 -keepattributes SourceFile,LineNumberTable

 # 指定混淆是采用的算法，后面的参数是一个过滤器
 # 这个过滤器是谷歌推荐的算法，一般不做更改
 -optimizations !code/simplification/cast,!field/*,!class/merging/*



 #############################################
 #
 # Android开发中一些需要保留的公共部分
 #
 #############################################

 # 保留我们使用的四大组件，自定义的Application等等这些类不被混淆
 # 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity   # 保留继承自Activity类不被混淆
-keep public class * extends android.app.Application    # 保留继承自Application类不被混淆
-keep public class * extends android.support.multidex.MultiDexApplication   # 保留继承自MultiDexApplication类不被混淆
-keep public class * extends android.app.Service    # 保留继承自Service类不被混淆
-keep public class * extends android.content.BroadcastReceiver  # 保留继承自BroadcastReceiver类不被混淆
-keep public class * extends android.content.ContentProvider    # 保留继承自ContentProvider类不被混淆
-keep public class * extends android.app.backup.BackupAgentHelper   # 保留继承自BackupAgentHelper类不被混淆
-keep public class * extends android.preference.Preference  # 保留继承自Preference类不被混淆
-keep public class com.google.vending.licensing.ILicensingService   # 保留Google包下ILicensingService类不被混淆
-keep public class com.android.vending.licensing.ILicensingService  # 保留Android包下ILicensingService类不被混淆

 # 保留support下的所有类及其内部类
 -keep class android.support.** {*;}

 # 保留继承的
 -keep public class * extends android.support.v4.**
 -keep public class * extends android.support.v7.**
 -keep public class * extends android.support.annotation.**

 # 保留R下面的资源
 -keep class **.R$* {*;}

 # 保留本地native方法不被混淆
 -keepclasseswithmembernames class * {
     native <methods>;
 }

 # 保留在Activity中的方法参数是view的方法，
 # 这样以来我们在layout中写的onClick就不会被影响
 -keepclassmembers class * extends android.app.Activity{
     public void *(android.view.View);
 }

 # 保留枚举类不被混淆
 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 # 保留我们自定义控件（继承自View）不被混淆
 -keep public class * extends android.view.View{
     *** get*();
     void set*(***);
     public <init>(android.content.Context);
     public <init>(android.content.Context, android.util.AttributeSet);
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 # 保留Parcelable序列化类不被混淆
 -keep class * implements android.os.Parcelable {
     public static final android.os.Parcelable$Creator *;
 }

 # 保留Serializable序列化的类不被混淆
 -keepclassmembers class * implements java.io.Serializable {
     static final long serialVersionUID;
     private static final java.io.ObjectStreamField[] serialPersistentFields;
     !static !transient <fields>;
     !private <fields>;
     !private <methods>;
     private void writeObject(java.io.ObjectOutputStream);
     private void readObject(java.io.ObjectInputStream);
     java.lang.Object writeReplace();
     java.lang.Object readResolve();
 }

 # 对于带有回调函数的onXXEvent、**On*Listener的，不能被混淆
 -keepclassmembers class * {
     void *(**On*Event);
     void *(**On*Listener);
 }

 # webView处理，项目中没有使用到webView忽略即可
 -keepclassmembers class fqcn.of.javascript.interface.for.webview {
     public *;
 }
 -keepclassmembers class * extends android.webkit.webViewClient {
     public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
     public boolean *(android.webkit.WebView, java.lang.String);
 }
 -keepclassmembers class * extends android.webkit.webViewClient {
     public void *(android.webkit.webView, jav.lang.String);
 }

#JS调用相关
#-keepattributes *JavascriptInterface*
#-keep class **.Webview2JsInterface { *; }  # 保持WebView对HTML页面的API不被混淆
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {  # 保留WebView
#   public *;
#}
#-keep class 你的类所在的包.** { *; }
#如果是内部类则使用如下方式
#-keepclasseswithmembers class 你的类所在的包.父类$子类 { <methods>; }

#不混淆第三方JAR
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment
-keep public class * extends java.lang.annotation.Annotation
-keep class * extends java.lang.annotation.Annotation {*;}
-ignorewarnings

 -dontwarn cn.jpush.android.**
 -keep class cn.jpush.android.** { *;}

 -dontwarn com.amap.api.**
 -keep class com.amap.api.** { *;}

 -dontwarn com.baidu.android.**
 -keep class com.baidu.android.** { *;}

 -dontwarn com.esotericsoftware.kryo.**
 -keep class com.esotericsoftware.kryo.** { *;}

 -dontwarn org.bouncycastle.jce.**
 -keep class org.bouncycastle.jce.** { *;}

 -dontwarn org.bouncycastle.util.**
 -keep class org.bouncycastle.util.** { *;}

  -dontwarn org.bouncycastle.x509.**
  -keep class org.bouncycastle.x509.** { *;}

  -dontwarn org.objenesis.instantiator.**
  -keep class org.objenesis.instantiator.** { *;}

  -dontwarn rx.internal.util.**
  -keep class rx.internal.util.** { *;}

  -dontwarn org.objenesis.instantiator.**
  -keep class org.objenesis.instantiator.** { *;}

  -dontwarn cn.jiguang.a.**
  -keep class cn.jiguang.a.** { *;}

 -dontwarn sun.misc.**
  -keep class sun.misc.** { *;}

 -dontwarn com.rt.**
  -keep class com.rt.** { *;}

-keep class com.baidu.ocr.sdk.**{*;}
-dontwarn com.baidu.ocr.**

-keep class com.yanzhenjie.permission.**{*;}
-dontwarn com.yanzhenjie.permission.**


-keep class com.tamic.novate.** {*;}

# glide 的混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}

#gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson 下面替换成自己的实体类
-keep class com.example.bean.** { *; }

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#Apollo
-dontwarn com.esotericsoftware.kryo.**
-dontwarn org.objenesis.instantiator.**
-dontwarn org.codehaus.**
-dontwarn java.nio.**
-dontwarn java.lang.invoke.**
-keep class com.lsxiao.apollo.generate.** { *; }

#EventBus 3.0
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

 #3D 高德地图 V5.0.0之后：
    -keep   class com.amap.api.maps.**{*;}
    -keep   class com.autonavi.**{*;}
    -keep   class com.amap.api.trace.**{*;}
    #定位
    -keep class com.amap.api.location.**{*;}
    -keep class com.amap.api.fence.**{*;}
    -keep class com.autonavi.aps.amapapi.model.**{*;}
    #搜索
    -keep   class com.amap.api.services.**{*;}
   # 导航
    -keep class com.amap.api.navi.**{*;}
    -keep class com.autonavi.**{*;}

 #微信分享
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*;}
-dontwarn com.tencent.mm.**
-keep class com.tencent.mm.**{*;}

#百度文字识别
-keep class com.baidu.ocr.sdk.**{*;}
-dontwarn com.baidu.ocr**

#极光推送
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#百度推送
 #-libraryjars src/main/jniLibs/pushservice-5.6.0.30.jar
 -dontwarn com.baidu.**
 -keep class com.baidu.**{*; }

 #友盟
 -keep class com.umeng.** {*;}
 -keepclassmembers class * {
    public <init> (org.json.JSONObject);
 }
 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }
 -keep public class [com.nevs.car].R$*{
 public static final int *;
 }

 #腾讯X5
# -keep class com.tencent.smtt.**{*;}
# -dontwarn com.tencent.smtt.**

 #新版PDFView
 -keep class com.shockwave.**

 #高德地图导航
 -keep class com.amap.api.navi.**{*;}
 -keep class com.autonavi.**{*;}

 -keep class com.alibaba.idst.nls.** {*;}
 -keep class com.google.**{*;}
 -keep class com.nlspeech.nlscodec.** {*;}


 #------tbs腾讯x5混淆规则-------

 #-optimizationpasses 7
 #-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
 -dontoptimize
 -dontusemixedcaseclassnames
 -verbose
 -dontskipnonpubliclibraryclasses
 -dontskipnonpubliclibraryclassmembers
 -dontwarn dalvik.**
 -dontwarn com.tencent.smtt.**
 #-overloadaggressively

 # ------------------ Keep LineNumbers and properties ---------------- #
 -keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
 # --------------------------------------------------------------------------

 # Addidional for x5.sdk classes for apps

 -keep class com.tencent.smtt.export.external.**{
     *;
 }

 -keep class com.tencent.tbs.video.interfaces.IUserStateChangedListener {
     *;
 }

 -keep class com.tencent.smtt.sdk.CacheManager {
     public *;
 }

 -keep class com.tencent.smtt.sdk.CookieManager {
     public *;
 }

 -keep class com.tencent.smtt.sdk.WebHistoryItem {
     public *;
 }

 -keep class com.tencent.smtt.sdk.WebViewDatabase {
     public *;
 }

 -keep class com.tencent.smtt.sdk.WebBackForwardList {
     public *;
 }

 -keep public class com.tencent.smtt.sdk.WebView {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebView$HitTestResult {
     public static final <fields>;
     public java.lang.String getExtra();
     public int getType();
 }

 -keep public class com.tencent.smtt.sdk.WebView$WebViewTransport {
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebView$PictureListener {
     public <fields>;
     public <methods>;
 }


 -keepattributes InnerClasses

 -keep public enum com.tencent.smtt.sdk.WebSettings$** {
     *;
 }

 -keep public enum com.tencent.smtt.sdk.QbSdk$** {
     *;
 }

 -keep public class com.tencent.smtt.sdk.WebSettings {
     public *;
 }


 -keepattributes Signature
 -keep public class com.tencent.smtt.sdk.ValueCallback {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebViewClient {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.DownloadListener {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebChromeClient {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebChromeClient$FileChooserParams {
     public <fields>;
     public <methods>;
 }

 -keep class com.tencent.smtt.sdk.SystemWebChromeClient{
     public *;
 }
 # 1. extension interfaces should be apparent
 -keep public class com.tencent.smtt.export.external.extension.interfaces.* {
     public protected *;
 }

 # 2. interfaces should be apparent
 -keep public class com.tencent.smtt.export.external.interfaces.* {
     public protected *;
 }

 -keep public class com.tencent.smtt.sdk.WebViewCallbackClient {
     public protected *;
 }

 -keep public class com.tencent.smtt.sdk.WebStorage$QuotaUpdater {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebIconDatabase {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.WebStorage {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.DownloadListener {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.QbSdk {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.QbSdk$PreInitCallback {
     public <fields>;
     public <methods>;
 }
 -keep public class com.tencent.smtt.sdk.CookieSyncManager {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.Tbs* {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.utils.LogFileUtils {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.utils.TbsLog {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.utils.TbsLogClient {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.CookieSyncManager {
     public <fields>;
     public <methods>;
 }

 # Added for game demos
 -keep public class com.tencent.smtt.sdk.TBSGamePlayer {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.TBSGamePlayerClient* {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.TBSGamePlayerClientExtension {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.TBSGamePlayerService* {
     public <fields>;
     public <methods>;
 }

 -keep public class com.tencent.smtt.utils.Apn {
     public <fields>;
     public <methods>;
 }
 -keep class com.tencent.smtt.** {
     *;
 }
 # end


 -keep public class com.tencent.smtt.export.external.extension.proxy.ProxyWebViewClientExtension {
     public <fields>;
     public <methods>;
 }

 -keep class MTT.ThirdAppInfoNew {
     *;
 }

 -keep class com.tencent.mtt.MttTraceEvent {
     *;
 }

 # Game related
 -keep public class com.tencent.smtt.gamesdk.* {
     public protected *;
 }

 -keep public class com.tencent.smtt.sdk.TBSGameBooter {
         public <fields>;
         public <methods>;
 }

 -keep public class com.tencent.smtt.sdk.TBSGameBaseActivity {
     public protected *;
 }

 -keep public class com.tencent.smtt.sdk.TBSGameBaseActivityProxy {
     public protected *;
 }

 -keep public class com.tencent.smtt.gamesdk.internal.TBSGameServiceClient {
     public *;
 }
 #---------------------------------------------------------------------------


 #------------------  下方是android平台自带的排除项，这里不要动         ----------------

 -keep public class * extends android.app.Activity{
     public <fields>;
     public <methods>;
 }
 -keep public class * extends android.app.Application{
     public <fields>;
     public <methods>;
 }
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 -keep public class * extends android.app.backup.BackupAgentHelper
 -keep public class * extends android.preference.Preference

 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 -keepattributes *Annotation*

 -keepclasseswithmembernames class *{
     native <methods>;
 }

 -keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
 }

 #------------------  下方是共性的排除项目         ----------------
 # 方法名中含有“JNI”字符的，认定是Java Native Interface方法，自动排除
 # 方法名中含有“JRI”字符的，认定是Java Reflection Interface方法，自动排除

 -keepclasseswithmembers class * {
     ... *JNI*(...);
 }

 -keepclasseswithmembernames class * {
     ... *JRI*(...);
 }

 -keep class **JNI* {*;}

 #状态栏
 -keep class com.gyf.immersionbar.* {*;}
  -dontwarn com.gyf.immersionbar.**