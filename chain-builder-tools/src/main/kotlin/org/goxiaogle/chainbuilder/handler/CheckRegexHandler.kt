package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckRegex

class CheckRegexHandler : AnnotationAndTypeHandler<CheckRegex>(CheckRegex::class.java, String::class.java) {
    override fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<CheckRegex>) {
        builder.matchRegex(fieldInfo.fieldValue as String, fieldInfo.targetAnnotation.value)

    }
}
