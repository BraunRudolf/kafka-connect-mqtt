package be.jovacon.connect.mqtt.annotation;

import java.lang.annotation.*;

/**
 * Denotes that the annotated type is safe for concurrent access from multiple
 * threads.
 */
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadSafe {
}
