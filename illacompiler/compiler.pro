-target 1.8

-dontoptimize
-optimizationpasses 10
-allowaccessmodification
-mergeinterfacesaggressively
-optimizations !code/allocation/variable

-dontobfuscate

-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable
-keepattributes *Annotation*

-allowaccessmodification

-keep public class illarion.compile.Compiler {
    public static void main(java.lang.String[]);
}

-keepclassmembers class * {
    @illarion.common.util.CalledByReflection *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep enum illarion.easynpc.data.*

-keepclassmembers enum illarion.easynpc.data.* {
    public static **[] values();
}

-keep class ch.qos.logback.core.ConsoleAppender { *; }
-keep class ch.qos.logback.classic.LoggerContext { *; }
-keep class ch.qos.logback.classic.encoder.PatternLayoutEncoder { *; }
-keep class ch.qos.logback.classic.filter.LevelFilter { *; }

-keep public class ch.qos.logback.core.spi.FilterReply {
    public static **[] values();
}
-keep public class ch.qos.logback.classic.Level {
    public static *;
}
-keep public class ch.qos.logback.classic.spi.** { *; }

-keep class org.xmlpull.mxp1.MXParserFactory

-dontwarn com.sun.**
-dontwarn javax.servlet.**
-dontwarn javax.mail.**
-dontwarn javax.jms.**
-dontwarn javax.xml.**
-dontwarn org.pushingpixels.**
-dontwarn org.apache.commons.**
-dontwarn org.apache.axis.**
-dontwarn org.mantisbt.**
-dontwarn org.osgi.service.**
-dontnote javax.xml.**
-dontwarn ch.qos.logback.classic.**
-dontwarn ch.qos.logback.core.joran.conditional.PropertyEvalScriptBuilder
-dontwarn org.codehaus.janino.**
-dontwarn org.apache.log4j.**