package org.goxiaogle.chainbuilder.annotations;

import org.goxiaogle.chainbuilder.CheckChainBuilder;
import org.intellij.lang.annotations.RegExp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验是否符合正则表达式
 * <p>
 * * 受 nullSkip 设置的影响
 * @see CheckChainBuilder#isNullSkip()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE_PARAMETER})
public @interface CheckRegex {
    /**
     * 正则表达式
     */
    @RegExp String value();

    Reason reason() default @Reason;
}
