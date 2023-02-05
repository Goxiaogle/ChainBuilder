package org.goxiaogle.chainbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验字符串的长度、集合的大小是否在指定区间内【包含边界】
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface CheckSize {
    int left() default 0;
    int right() default Integer.MAX_VALUE;
}
