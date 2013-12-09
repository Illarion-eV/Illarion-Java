-libraryjars <java.home>/lib/jce.jar
-libraryjars <java.home>/lib/rt.jar
-libraryjars <java.home>/lib/jfxrt.jar

# Optimization settings
-optimizationpasses 2000
-allowaccessmodification
-mergeinterfacesaggressively

# Obfuscation settings
-dontobfuscate


-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keepattributes *Annotation*

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class org.apache.log4j.ConsoleAppender
-keep class org.apache.log4j.PatternLayout
-keep class javax.swing.plaf.ComponentUI

-keepclasseswithmembers class org.apache.log4j.PatternLayout {
    void setConversionPattern(java.lang.String);
    java.lang.String getConversionPattern();
}

-keepclassmembernames class * {
    @javafx.fxml.FXML *;
}

-keepclasseswithmembers class * {
    int getMnemonic();
    javax.swing.plaf.ComponentUI createUI(javax.swing.JComponent);
    org.jdesktop.swingx.painter.Painter getBackgroundPainter();
    void setBackgroundPainter(org.jdesktop.swingx.painter.Painter);
    java.lang.String displayPropertiesToCSS(java.awt.Font,java.awt.Color);
    void loadActionMap(org.jdesktop.swingx.plaf.basic.core.LazyActionMap);
}

-keepclasseswithmembers class * {
	org.apache.commons.logging.Log getLog(java.lang.String);
}

-dontwarn javax.servlet.**
-dontwarn javax.mail.**
-dontwarn javax.jms.**
-dontwarn javax.xml.**
-dontnote javax.xml.**
-dontwarn de.lessvoid.org.illarion.engine.nifty.**
-dontnote org.apache.log4j.jmx.Agent
-dontnote org.apache.log4j.net.ZeroConfSupport
-dontnote org.apache.log4j.spi.LocationInfo
-dontwarn org.apache.commons.**
-dontwarn org.apache.tools.ant.**
-dontwarn org.apache.axis.**
-dontwarn org.mantisbt.connect.ant.**
-dontwarn org.apache.maven.repository.internal.**
-dontwarn org.eclipse.aether.impl.**
-dontwarn org.eclipse.aether.internal.impl.**
-dontwarn org.eclipse.aether.connector.**
-dontwarn org.eclipse.aether.transport.**
-dontwarn org.osgi.service.**
-dontwarn org.slf4j.**