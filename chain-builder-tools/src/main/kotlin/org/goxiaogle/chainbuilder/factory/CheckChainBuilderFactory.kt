package org.goxiaogle.chainbuilder.factory

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.handler.*

class CheckChainBuilderFactory(private vararg val targets: Any) {

    private val ignoreHandlers: MutableList<Class<out CheckHandler>> = mutableListOf()

    companion object Handlers {
        /**
         * handlers 可二次修改的，所以你可以自定义里面的内容
         */
        val handlers = mutableListOf(
            CheckNotNullHandler(),
            CheckRegexHandler(),
            CheckSizeHandler(),
            CheckNotBlankHandler(),
            CheckNumberBetweenHandler()
        )
    }

    /**
     * 以 obj 的字段上的 @CheckXXX 注解为基础，构建 CheckChainBuilder
     *
     * 【注意】一个字段被多个 @CheckXXX 注解标记时，仅会生效在 [Handlers.handlers] 中顺序靠前的一个
     */
    fun create(builder: CheckChainBuilder<*>) {
        for (obj in targets) {
            handle(builder, obj)
        }
    }

    /**
     * 设定需要忽略的处理器类型，比如可以忽略 CheckNotNullHandler
     */
    fun addIgnoreHandler(vararg handlers: Class<out CheckHandler>): CheckChainBuilderFactory {
        ignoreHandlers.add(CheckNotNullHandler::class.java)
        ignoreHandlers.addAll(handlers)
        return this
    }

    /**
     * 过滤除需要忽略的处理器类型，返回可用的处理器
     */
    private fun filterIgnore(): List<CheckHandler> {
        var temp: List<CheckHandler> = handlers
        ignoreHandlers.forEach { clazz ->
            temp = temp.filter { clazz.isAssignableFrom(it.javaClass) }
        }
        return temp
    }

    private fun handle(builder: CheckChainBuilder<*>, obj: Any) {
        for (field in obj.javaClass.declaredFields) {
            for (handler in filterIgnore()) {
                // 要求该处理器不在忽略的列表中
                if (handler.isSupported(field)) {
                    field.trySetAccessible()
                    handler.handle(builder, obj, field)
                    break
                }
            }
        }
    }
}
