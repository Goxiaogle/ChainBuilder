package org.goxiaogle.chainbuilder.utils

import org.goxiaogle.chainbuilder.CheckChainBuilder
import org.goxiaogle.chainbuilder.annotations.Reason
import org.goxiaogle.chainbuilder.pojo.FieldInfo
import org.goxiaogle.chainbuilder.setting.CheckChainBuilderSetting
import java.util.concurrent.Callable

object CheckChainBuilderUtils {

    @JvmStatic
    fun CheckChainBuilder<*>.throwByUseCatch(callable: Callable<*>) {
        try {
            callable.call()
        } catch (e: Exception) {
            autoThen { throw RuntimeException(e) }
        }
    }

    @JvmStatic
    fun CheckChainBuilder<*>.addReason(annotation: Reason, reason: String, fieldInfo: FieldInfo<out Annotation?>): CheckChainBuilder<*> = annotation.value.let {
        setFailResultCheckByFactory(
            decodeReason(
                if (it == Reason.USE_DEFAULT_REASON) reason else it, fieldInfo
            )
        )
    }

    @JvmStatic
    fun decodeReason(reason: String, fieldInfo: FieldInfo<out Annotation?>): String {
        var value = reason
        // 这里其实想换成效率更高的算法，但是由于懒就直接成为调用 API 工程师
        CheckChainBuilderSetting.reasonKeywordReplaceMap.forEach { (t, u) ->
            value = value.replace(t, u(fieldInfo))
        }
        return value
    }
}
