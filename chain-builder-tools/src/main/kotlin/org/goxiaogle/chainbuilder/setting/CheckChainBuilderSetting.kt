package org.goxiaogle.chainbuilder.setting

import org.goxiaogle.chainbuilder.handler.*
import org.goxiaogle.chainbuilder.pojo.FieldInfo

object CheckChainBuilderSetting {
    /**
     * [org.goxiaogle.chainbuilder.factory.CheckChainBuilderFactory.create] 协助构造时要用的 handler
     *
     * 可二次修改，所以你可以自定义里面的内容，比如自己实现一个特殊的 [CheckHandler]
     */
    @JvmStatic
    val checkHandlers = mutableListOf(
        CheckNotNullHandler(),
        CheckRegexHandler(),
        CheckSizeHandler(),
        CheckNotBlankHandler(),
        CheckNumberBetweenHandler()
    )

    /**
     * 对于解析结果中关键词的的哈希表
     */
    @JvmStatic
    val reasonKeywordReplaceMap = mutableMapOf<String, (FieldInfo<out Annotation?>) -> String>(
        "{field}" to { it.toString() },
        "{object}" to { it.obj.toString() },
        "{fieldName}" to { it.fieldName.toString() },
        "{fieldValue}" to { it.fieldValue.toString() }
    )
}
