package org.goxiaogle.chainbuilder;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;

/**
 * 校验链建造器，可以用于快捷且优雅的校验前端传入的数据
 *
 * @param <Result>
 */
final public class CheckChainBuilder<Result> extends DefaultChainBuilder<Result, CheckChainBuilder<Result>> {

    /**
     * 遇到 null 时是否跳过此次执行。若不跳过，将会不执行后续操作
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
     * 使用失败结果构造工厂设定失败结果
     * @param reason 原因
     */
    public CheckChainBuilder<Result> setFailResultByFactory(String reason) {
        if (resultFactory != null) {
            return setFailResult(resultFactory.create(reason));
        }
        return this;
    }

    /**
     * 使用失败结果构造工厂设定失败的结果，使用 {@link ChainBuilder#setFailResultCheck(Object)}，受 skipNext 影响
     * @param reason 原因
     */
    public CheckChainBuilder<Result> setFailResultCheckByFactory(String reason) {
        if (resultFactory != null) {
            return setFailResultCheck(resultFactory.create(reason));
        }
        return this;
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

    /**
     * 校验指定对象是否介于左右边界之间
     * <p>
     * * 此处不会检查左边界是否大于右边界，所以不会导致左边界大于右边界时使得最终结果为失败
     *
     * @param target 要检验的对象
     * @param left   左边界
     * @param right  有边界
     * @param <T>    实现了 Comparable 的类型，如 String、Number
     */
    public <T extends Comparable<K>, K> CheckChainBuilder<Result> between(T target, K left, K right) {
        return ifNullThenSkip(target).autoThen(() -> target.compareTo(left) >= 0 && target.compareTo(right) <= 0);
    }

    /**
     * 校验指定对象是否介于左右边界之间，使用指定的比较器
     * <p>
     * * 此处不会检查左边界是否大于右边界，所以不会导致左边界大于右边界时使得最终结果为失败
     *
     * @param target 要检验的对象
     * @param left   左边界
     * @param right  有边界
     * @param comparator 指定的比较器
     * @param <T>    实现了 Comparable 的类型，如 String、Number
     */
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
