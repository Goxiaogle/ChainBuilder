package org.goxiaogle.chainbuilder.pojo

import java.lang.reflect.Field

data class FieldInfo<A>(
    val obj: Any,
    val field: Field,
    val targetAnnotation: A,
    val fieldValue: Any,
    val fieldName: String?,
)
