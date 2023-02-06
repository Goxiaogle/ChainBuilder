package org.goxiaogle.chainbuilder.annotations;

import org.goxiaogle.chainbuilder.CheckChainBuilder;
import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于判断某个数字是否介于左右区间之间（包含边界）
 * <p>
 * * 可用于 Number 类型，如 int、long、BigInteger、BigDecimal 等等
 * <p>
 * * left/right 和 leftString/rightString 效果一致，left/right 仅支持 "byte、short、int、long" 以及其包装类型
 * 当 xxxString 的值不为 {@link CheckNumberBetween#NONE} 时，则会以 xxxString 的值优先，不考虑 left 与 right
 * <p>
 * * 受 nullSkip 设置的影响
 * @see CheckChainBuilder#isNullSkip()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface CheckNumberBetween {

    String NONE = "none";
    long left() default Long.MIN_VALUE;

    @Pattern("[+-]?(0|([1-9]\\d*))(\\.\\d+)?$") String leftString() default NONE;

    long right() default Long.MAX_VALUE;

    @Pattern("[+-]?(0|([1-9]\\d*))(\\.\\d+)?$") String rightString() default NONE;

    Reason reason() default @Reason;

}
