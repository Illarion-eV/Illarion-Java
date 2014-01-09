package illarion.common.util;

import java.lang.annotation.*;

/**
 * This annotation is used to mark functions that are called by reflections. This is mainly used to tell proguard that
 * those functions may not be removed.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(value = {ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CalledByReflection {
}
