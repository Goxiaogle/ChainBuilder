package org.goxiaogle.chainbuilder.handler

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.utils.CheckChainBuilderUtils.throwByUseCatch
import java.lang.reflect.Field

/**
 * CheckChainBuilder 处理器
 */
interface CheckHandler {
    fun isSupported(field: Field): Boolean
    fun handle(builder: CheckChainBuilder<*>, obj: Any, field: Field)
}
