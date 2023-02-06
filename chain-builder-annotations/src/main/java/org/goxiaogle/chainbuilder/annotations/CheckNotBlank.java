package org.goxiaogle.chainbuilder.annotations;

import org.goxiaogle.chainbuilder.CheckChainBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验字符串不为空白
 * <p>
 * * 受 nullSkip 设置的影响
 * @see CheckChainBuilder#isNullSkip()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface CheckNotBlank {
    Reason reason() default @Reason;
}
