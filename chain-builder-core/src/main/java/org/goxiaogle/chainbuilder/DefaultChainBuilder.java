package org.goxiaogle.chainbuilder;

import java.util.concurrent.Callable;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
abstract public class DefaultChainBuilder<Result, Child extends DefaultChainBuilder<Result, Child>> implements ChainBuilder<Result, Child> {

    protected boolean proceed = true;
    protected boolean useCatch = false;
    protected boolean skipNext = false;
    protected Result failResult;

    public DefaultChainBuilder(Result failResult) {
        this.failResult = failResult;
    }

    @Override
    public boolean isProceed() {
        return proceed;
    }

    @Override
    public Child setProceed(BooleanSupplier proceed) {
        this.proceed = proceed.getAsBoolean();
        return (Child) this;
    }

    @Override
    public Result getFailResult() {
        return failResult;
    }

    @Override
    public Child setFailResult(Result failResult) {
        this.failResult = failResult;
        return (Child) this;
    }

    /**
     * 根据 useCatch 的值决定对 Callable 中可能出现的异常的 then 操作
     * @param callable 需要执行的语句
     * @return 子构造器本身
     */
    public Child autoThen(Callable<Boolean> callable) {
        if (useCatch) {
            return catchThen(callable);
        } else {
            // 此处和 catchThen 的区别在于，此处若遇到抛出异常，会直接终止程序（如果未被更外层的 catch 捕获）
            return then(() -> {
                try {
                    return callable.call();
                } catch (Exception e) {
                    // 以 RuntimeException 替代 Exception，不强制使用者 try - catch
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public Child autoThen(Result failResult, Callable<Boolean> callable) {
        return setFailResultCheck(failResult).autoThen(callable);
    }

    /**
     * 代表 {@link DefaultChainBuilder#autoThen(Callable)} 对可能产生的异常的处理方式
     * <p>
     * isUseCatch 的值不会影响 {@link ChainBuilder#then(Object, BooleanSupplier)} 或者 {@link ChainBuilder#catchThen(Callable)}
     * @return 如果为 true，则调用 {@link ChainBuilder#catchThen(Callable)}；如果为 false，发送错误时则会抛出 {@link RuntimeException}
     */
    public boolean isUseCatch() {
        return useCatch;
    }

    /**
     * 设置 {@link DefaultChainBuilder#autoThen(Callable)} 对异常的处理方式
     * @param useCatch 是否要捕获异常
     * @see DefaultChainBuilder#isUseCatch()
     */
    public void setUseCatch(boolean useCatch) {
        this.useCatch = useCatch;
    }

    @Override
    public Child buildBy(Consumer<Child> consumer) {
        consumer.accept((Child) this);
        return (Child) this;
    }

    @Override
    public Child setSkipNext(boolean skipNext) {
        this.skipNext = skipNext;
        return (Child) this;
    }

    @Override
    public boolean isSkipNext() {
        return skipNext;
    }
}
