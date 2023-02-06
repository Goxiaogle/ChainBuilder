package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.pojo.FieldInfo
import java.lang.reflect.Field

/**
 * 复杂注解处理器基础实现
 *
 * 只有指定注解标记在该字段上，且此字段类型为特定的类型时才会处理
 * @property annotationClass 指定注解
 * @property types 特定类型，可以是零个（任意类型）或者多个
 */
abstract class AnnotationAndTypeHandler<A: Annotation>(
    private val annotationClass: Class<A>,
    private vararg val types: Class<*>
) : CheckWhenNotNullHandler {

    override fun handle(builder: CheckChainBuilder<*>, obj: Any, field: Field, fieldValue: Any) {
        handle(builder, FieldInfo(obj, field, field.getAnnotation(annotationClass), fieldValue, field.name))
    }

    abstract fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<A>)

    /**
     * 只有在指定注解在特定类型的字段上使用时
     */
    override fun isSupported(field: Field) = field.isAnnotationPresent(annotationClass) && types.any { it.isAssignableFrom(field.type) }

}
