package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckNumberBetween
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.throwByUseCatch
import java.lang.reflect.Field
import java.math.BigDecimal

class CheckNumberBetweenHandler : AnnotationAndTypeHandler<CheckNumberBetween>(
    CheckNumberBetween::class.java, Number::class.java
) {
    override fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<CheckNumberBetween>) {
        val (left, right) = fieldInfo.targetAnnotation.getLeftAndRight()
        // 已经限定了 Number 类型，所以不用担心 toString 结果不是数字
        val value = BigDecimal(fieldInfo.fieldValue.toString())
        builder.between(value, left, right)
    }

    private fun CheckNumberBetween.getLeftAndRight() =
        BigDecimal(if (leftString != CheckNumberBetween.NONE) leftString else left.toString()) to
                BigDecimal(if (rightString != CheckNumberBetween.NONE) rightString else right.toString())
}
