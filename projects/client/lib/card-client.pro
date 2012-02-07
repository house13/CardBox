#
# Proguard configuration file for Game Gardens client

-dontskipnonpubliclibraryclasses
-dontoptimize
-dontobfuscate

# we ignore a ton of stuff that we know not to be used
-dontwarn ca.beq.util.win32.registry.**
-dontwarn com.google.inject.**
-dontwarn com.jcraft.**
-dontwarn com.samskivert.depot.**
-dontwarn com.samskivert.mustache.**
-dontwarn com.samskivert.servlet.user.UserUtil
-dontwarn com.samskivert.velocity.**
-dontwarn com.samskivert.xml.**
-dontwarn com.threerings.**.server.**
-dontwarn com.threerings.getdown.tools.**
-dontwarn com.hextilla.**.server.**
-dontwarn com.hextilla.cardbox.xml.**
-dontwarn javax.annotation.**
-dontwarn javax.mail.**
-dontwarn javax.servlet.**
-dontwarn javazoom.jl.decoder.**
-dontwarn net.sf.ehcache.**
-dontwarn org.apache.commons.codec.**
-dontwarn org.apache.commons.collections.**
-dontwarn org.apache.commons.digester.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.mina.**
-dontwarn org.apache.tools.ant.**
-dontwarn org.apache.velocity.**
-dontwarn org.lwjgl.**
-dontwarn sun.misc.Unsafe

# we need whatever we keep of samskivert to be around in its entirety so
# that if a game uses the same classfile, the whole thing is there
-keep public class com.samskivert.Log {
    public protected *;
}
-keep public class com.samskivert.io.** {
    public protected *;
}
-keep public class com.samskivert.net.AttachableURLFactory {
    public protected *;
}
-keep public class com.samskivert.net.PathUtil {
    public protected *;
}
-keep public class com.samskivert.servlet.user.Password {
    public protected *;
}
-keep public class com.samskivert.servlet.user.UserUtil {
    public protected *;
}
-keep public class com.samskivert.swing.** {
    public protected *;
}
-keep public class com.samskivert.text.MessageUtil {
    public protected *;
}
-keep public class com.samskivert.util.** {
    public protected *;
}

# we're going to need our RestDB stuff client-side
-keep public class com.restfb.** {
    public protected *;
}

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

# keep our view test harness
-keep public class com.hextilla.cardbox.util.GameViewTest

# similarly for all of the narya, etc. libraries
-keep public class com.threerings.** {
    public protected *;
}

# similarly for all our own libraries 
-keep public class com.hextilla.** {
    public protected *;
}
