# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\praveen_2\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
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
#-dontwarn org.codehaus.**, org.jdom2.**, nu.xom.**, org.jdom.**, com.bea.**, java.awt.**, sun.awt.**, javax.swing.**, javax.annotation.**, net.sf.**, rx.**, org.xmlpull.**, javax.**, java.beans.**, javax.xml.**, org.dom4j.**

-ignorewarnings

#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify

#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose

-keep class com.mobsandgeeks.saripaar.** {*;}
-keep @com.mobsandgeeks.saripaar.annotation.ValidateUsing class * {*;}