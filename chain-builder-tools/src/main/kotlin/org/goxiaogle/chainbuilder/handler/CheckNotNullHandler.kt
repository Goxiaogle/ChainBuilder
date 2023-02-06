package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckNotNull
import java.lang.reflect.Field

class CheckNotNullHandler : CheckHandler {
    override fun isSupported(field: Field) = field.isAnnotationPresent(CheckNotNull::class.java)

    /**
     * 不需要使用默认的 NULL 判断
     */
    override fun handle(builder: CheckChainBuilder<*>, obj: Any, field: Field) {
        builder.setFailResultCheckByFactory("[${field.name}] 字段值不应为 null").isNotNull(field[obj])
    }

}
