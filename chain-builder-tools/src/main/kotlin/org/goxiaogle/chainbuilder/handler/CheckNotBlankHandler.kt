package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckNotBlank
import org.goxiaogle.chainbuilder.pojo.FieldInfo
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.addReason

class CheckNotBlankHandler : AnnotationAndTypeHandler<CheckNotBlank>(CheckNotBlank::class.java, String::class.java) {

    override fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<CheckNotBlank>) {
        builder.addReason(
            fieldInfo.targetAnnotation.reason,
            "{fieldName} 的值不应为空白",
            fieldInfo
        ).isNotBlank(fieldInfo.fieldValue as String)
    }
}
