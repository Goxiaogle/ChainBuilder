package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.CheckSize
import org.goxiaogle.chainbuilder.pojo.FieldInfo
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.addReason
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.throwByUseCatch
import java.lang.reflect.Field

class CheckSizeHandler : AnnotationAndTypeHandler<CheckSize>(CheckSize::class.java, String::class.java, Collection::class.java) {

    override fun handle(builder: CheckChainBuilder<*>, fieldInfo: FieldInfo<CheckSize>) {
        val (_, _, annotation, fieldValue, _) = fieldInfo
        val left = annotation.left
        val right = annotation.right
        require(left <= right) { "@CheckSize 的左边界大于右边界" }

        // instanceof 会确保 value != null，所以可以省去判空
        builder.addReason(
            annotation.reason,
            "[{fieldName}] 字段的长度/大小应当介于 $left 和 $right 之间，实际为 {fieldValue}",
            fieldInfo
        ).between(
            when (fieldValue) {
                is String -> fieldValue.length
                is Collection<*> -> fieldValue.size
                else -> throw IllegalArgumentException("@CheckSize 只能用于字符串或者集合类型")
            }, left, right
        )
    }
}
