package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.throwByUseCatch
import java.lang.reflect.Field

interface CheckWhenNotNullHandler : CheckHandler {

    override fun handle(builder: CheckChainBuilder<*>, obj: Any, field: Field) {
        builder.throwByUseCatch {
            field[obj]?.let { handle(builder, obj, field, it) } ?: require(builder.isNullSkip) {
                "$obj 的 $field 字段的值为 null，但是并未设置 NullSkip，不执行后续链操作"
            }
        }
    }

    fun handle(builder: CheckChainBuilder<*>, obj: Any, field: Field, fieldValue: Any)

}
