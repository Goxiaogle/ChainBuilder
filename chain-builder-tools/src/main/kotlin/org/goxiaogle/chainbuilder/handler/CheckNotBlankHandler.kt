package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckNotBlank
import org.goxiaogle.chainbuilder.annotations.CheckNotNull
import java.lang.reflect.Field

class CheckNotBlankHandler : AnnotationAndTypeHandler<CheckNotBlank>(CheckNotBlank::class.java, String::class.java) {
    override fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<CheckNotBlank>) {
        builder.isNotBlank(fieldInfo.fieldValue as String)
    }
}
