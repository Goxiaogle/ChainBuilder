package org.goxiaogle.chainbuilder;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 校验链建造器，可以用于快捷且优雅的校验前端传入的数据
 *
 * @param <Result>
 */
final public class CheckChainBuilder<Result> extends DefaultChainBuilder<Result, CheckChainBuilder<Result>> {

    /**
     * 遇到 NULL 时是否跳过此次执行
     */
    private boolean nullSkip = false;

    public CheckChainBuilder(Result failResult) {
        super(failResult);
    }

    public CheckChainBuilder(Result failResult, boolean nullSkip) {
        super(failResult);
        this.nullSkip = nullSkip;
    }

    public CheckChainBuilder(Result failResult, boolean nullSkip, boolean useCatch) {
        super(failResult);
        this.nullSkip = nullSkip;
        this.useCatch = useCatch;
    }

    /**
     * 校验传入的参数是否匹配指定的正则表达式
     *
     * @param target 需要校验的参数
     * @param regex  指定的正则表达式
     * @return 校验链建造器本身
     */
    public CheckChainBuilder<Result> matchRegex(String target, @RegExp String regex) {
        return ifNullThenSkip(target).autoThen(() -> target.matches(regex));
    }

    /**
     * 校验传入参数是否为 null，如果为 null，则不执行后续链操作
     * <p>
     * 【注意】此方法和 isNullSkip 无关，不会只跳过下一步！
     * <p>
     * 如果想要检验为 null 时跳过下次操作请使用 {@link CheckChainBuilder#ifNullThenSkip(Object)}
     *
     * @param obj 要检验是否为 null 的参数
     * @return 校验链建造器本身
     */
    public CheckChainBuilder<Result> isNotNull(@Nullable Object obj) {
        return autoThen(() -> obj != null);
    }

    public CheckChainBuilder<Result> isNotBlank(String str) {
        return ifNullThenSkip(str).autoThen(() -> {
            for (char c : str.toCharArray()) {
                // 有任意一个不为空白的字符
                if (!Character.isWhitespace(c)) {
                    return true;
                }
            }
            return false;
        });
    }

    public <T extends Comparable<K>, K> CheckChainBuilder<Result> between(T target, K left, K right) {
        return ifNullThenSkip(target).autoThen(() -> target.compareTo(left) >= 0 && target.compareTo(right) <= 0);
    }

    public <T> CheckChainBuilder<Result> between(T target, T left, T right, Comparator<T> comparator) {
        return ifNullThenSkip(target).autoThen(() ->
                Objects.compare(target, left, comparator) >= 0 &&
                Objects.compare(target, right, comparator) <= 0
        );
    }

    /**
     * 当传入参数为 null 时，跳过下一次链操作（前提是 isNullSkip = true）
     *
     * @param obj 要判断的参数
     * @return 校验链建造器本身
     */
    public CheckChainBuilder<Result> ifNullThenSkip(@Nullable Object obj) {
        if (isNullSkip() && obj == null) {
            return skipNext();
        }
        return this;
    }

    public boolean isNullSkip() {
        return nullSkip;
    }

    public void setNullSkip(boolean nullSkip) {
        this.nullSkip = nullSkip;
    }

    public boolean isSkipNext() {
        return skipNext;
    }

}
