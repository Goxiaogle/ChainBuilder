package org.goxiaogle.chainbuilder.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_PARAMETER})
public @interface CheckRegex {
    /**
     * 正则表达式
     */
    String value();
}
