package com.cloudcanards.assets;

import java.lang.annotation.*;

/**
 * Marks assets that should be replaced
 *
 * @author creativitRy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Temporary
{
}
