package org.goxiaogle.chainbuilder;

import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 可执行链构造器
 *
 * @param <Result>
 * @param <Child>
 */
@SuppressWarnings("unchecked")
public interface ChainBuilder<Result, Child extends ChainBuilder<Result, Child>> {

    boolean isProceed();

    /**
     * 设定后面的链操作是否会被执行
     * <p>
     * tips: 可以在设定 false 后，再设置 true 恢复链式执行
     *
     * @param proceed 是否执行
     */
    default Child setProceed(boolean proceed) {
        return setProceed(() -> proceed);
    }

    /**
     * 设定该操作后面的链操作是否会被执行
     *
     * @param proceed 一个返回值为布朗值的任意操作，其返回值决定后续链操作是否会执行
     */
    Child setProceed(BooleanSupplier proceed);

    /**
     * 执行一个操作，操作返回值会通过 {@link ChainBuilder#setProceed(BooleanSupplier)} 设定
     *
     * @param supplier 指定操作
     */
    default Child then(BooleanSupplier supplier) {
        // 可继续执行才执行 supplier 的内容
        if (isProceed() && !isSkipNext()) {
            return setProceed(supplier);
        }
        setSkipNext(false);
        return (Child) this;
    }

    /**
     * 在执行一个操作的之前设置失败会返回的结果
     *
     * @param failResult 失败返回的结果
     * @param supplier   指定操作
     */
    default Child then(Result failResult, BooleanSupplier supplier) {
        return setFailResultCheck(failResult).then(supplier);
    }

    /**
     * 遇到异常时，捕获并打印（不会终止程序），并且不执行后续链操作，返回失败的结果
     *
     * @param callable 可能出现异常的执行语句
     * @return 子构造器本身
     */
    default Child catchThen(Callable<Boolean> callable) {
        return then(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        });
    }

    /**
     * 遇到异常时，捕获并打印（不会终止程序），并且不执行后续链操作
     * 【先更改失败会返回的结果，再执行语句】
     *
     * @param failResult 失败返回的结果
     * @param callable   可能出现异常的执行语句
     */
    default Child catchThen(Result failResult, Callable<Boolean> callable) {
        return setFailResultCheck(failResult).catchThen(callable);
    }

    /**
     * 获取当前失败会返回的结果
     *
     * @return 失败返回的结果
     */
    Result getFailResult();

    /**
     * 设置失败会返回的结果
     *
     * @param failResult 失败会返回的结果
     */
    Child setFailResult(Result failResult);

    /**
     * 设置失败的结果，受 {@link ChainBuilder#isSkipNext()} 影响且会传递 skipNext
     *
     * @param failResult 失败的结果
     */
    default Child setFailResultCheck(Result failResult) {
        if (isProceed()) {
            return isSkipNext() ? skipNext() : setFailResult(failResult);
        }
        return setProceed(false);
    }

    /**
     * 下次操作是否会被跳过
     *
     * @return 是否跳过
     */
    boolean isSkipNext();

    /**
     * 跳过下一次的操作
     */
    default Child skipNext() {
        return setSkipNext(true);
    }

    /**
     * 设置是否跳过下次操作
     * <p>
     * 【注意】不会跳过 end、setFailResult
     * <p>
     * 想要跳过 setFailResult 以及其后面的其它操作，应当使用 {@link ChainBuilder#setFailResultCheck(Object)}
     *
     * @param skipNext 是否跳过下次操作
     */
    Child setSkipNext(boolean skipNext);

    /**
     * 协助链构造器构造，其中可以传入自己的工具类等，在 chain-builder-tools 中有实现可供参考
     *
     * @param consumer 消费者
     */
    Child buildBy(Consumer<Child> consumer);

    /**
     * 结束构造
     *
     * @param successResult 成功结果
     * @return 如果最近一次操作返回为 true，或者手动设定为 true，则使用成功的结果，否则使用最近一次设定的失败结果
     */
    default Result end(Result successResult) {
        return end(() -> successResult);
    }

    /**
     * 结束构造
     *
     * @param successResult 成功结果
     * @return 如果最近一次操作返回为 true，或者手动设定为 true，则使用成功的结果，否则使用最近一次设定的失败结果
     */
    default Result end(Supplier<Result> successResult) {
        return isProceed() ? successResult.get() : getFailResult();
    }
}
