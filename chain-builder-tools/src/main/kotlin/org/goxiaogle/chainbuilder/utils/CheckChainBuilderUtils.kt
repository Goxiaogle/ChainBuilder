package org.goxiaogle.chainbuilder.utils

import org.goxiaogle.chainbuilder.CheckChainBuilder
import java.util.concurrent.Callable

object CheckChainBuilderUtils {

    fun CheckChainBuilder<*>.throwByUseCatch(callable: Callable<*>) {
        try {
            callable.call()
        } catch (e: Exception) {
            autoThen { throw RuntimeException(e) }
        }
    }

}
