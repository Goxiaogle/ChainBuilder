package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckRegex
import org.goxiaogle.chainbuilder.pojo.FieldInfo
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.addReason

class CheckRegexHandler : AnnotationAndTypeHandler<CheckRegex>(CheckRegex::class.java, String::class.java) {
    override fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<CheckRegex>) {
        val annotation = fieldInfo.targetAnnotation
        builder.addReason(
            annotation.reason,
            "[{fieldName}] 字段的值 >{fieldValue}< 不匹配正则: ${annotation.value}",
            fieldInfo
        ).matchRegex(fieldInfo.fieldValue as String, annotation.value)
    }
}
