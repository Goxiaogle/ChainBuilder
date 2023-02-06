package org.goxiaogle.chainbuilder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 假如校验失败时，应当返回的原因
 * <p>
 * 原因是可被二次处理解析的，请查看 tools 中的 CheckChainBuilderSetting.REASON_KEYWORD_REPLACE_MAP
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface Reason {
    String USE_DEFAULT_REASON = "_default";

    /**
     * _default 代表使用处理器默认生成的原因
     */
    String value() default USE_DEFAULT_REASON;
}
