import org.goxiaogle.chainbuilder.CheckChainBuilder;
import org.goxiaogle.chainbuilder.annotations.CheckNotBlank;
import org.goxiaogle.chainbuilder.annotations.CheckNumberBetween;
import org.goxiaogle.chainbuilder.annotations.CheckRegex;
import org.goxiaogle.chainbuilder.annotations.Reason;
import org.goxiaogle.chainbuilder.factory.CheckChainBuilderFactory;

public class Main {
    public static void main(String[] args) {
        Result<Object> end = new CheckChainBuilder<>(new Result<>("未知错误"), false, true)
                .setResultFactory(Result::new)
                .buildBy(new CheckChainBuilderFactory(
                        new Apple("苹果 98 号", 120L)
                )::create)
                .end(new Result<>(200, null, "成功"));
        System.out.println(end);
    }

}

class Apple {
    @CheckRegex("苹果 \\d+ 号")
    String name;
    @CheckNumberBetween(left = 0, right = 100, reason = @Reason("[{fieldName}] 字段有问题: 苹果价格卖这么贵你不去死"))
    Long price;

    public Apple(String name, Long price) {
        this.name = name;
        this.price = price;
    }
}

class Result<T> {
    private int code = 500;
    private T data;
    private String msg;

    public Result(int code) {
        this.code = code;
    }

    public Result(String msg) {
        this.msg = msg;
    }

    public Result(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", data=" + data +
                ", msg='" + msg + '\'' +
                '}';
    }
}
