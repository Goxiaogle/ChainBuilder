[toc]

# 中文

## ChainBuilder（链式判断构造）

一个优雅且能快速构造链式判断语句的工具

### 使用示例

1. 需要多个语句执行结果都为 true

   ```java
   String name = "cat";
   int age = 12;
   String result = new CheckChainBuilder<>("unknown fail")
           .then("name error!", () -> name.equals("dog"))
           .then("age error!", () -> age == 12)
           .end("success");
   // name error!
   System.out.println(result);

   String result2 = new CheckChainBuilder<>("unknown fail")
           .then("name error!", () -> name.equals("cat"))
           .then("age error!", () -> age == 12)
           .end("success");
   // success
   System.out.println(result2);
   ```

2. 执行需要捕获异常的语句（异常不会终止整个程序）

   ```java
   String result = new CheckChainBuilder<>("unknown fail")
           .catchThen("throw", () -> {throw new Exception("error");})
           .catchThen("---", () -> true)
           .end("success");
   // throw
   System.out.println(result);
   ```

3. 动态抉择是否要捕获异常（遇到异常是直接终止程序还是返回失败）

   ```java
   String result = new CheckChainBuilder<>("unknown fail")
           .setUseCatch(true)
           .autoThen("throw", () -> {throw new Exception();})
           .end("success");
   // print exception and return throw
   System.out.println(result);
   
   String result2 = new CheckChainBuilder<>("unknown fail")
           .setUseCatch(false)
           .autoThen("throw", () -> {throw new Exception();})
           .end("success");
   // unreachable
   System.out.println(result2);
   ```

   上述代码中，第一个例子将会打印错误，并且 result 为 "throw"；第二个例子将会因为报错而停止运行，代码无法到达 `end("success")` 和 `System.out.println(result2);`，useCatch 的值默认为 true。

## CheckChainBuilder（快速校验数据）

### 使用示例

1. 简单校验数据

   ```java
   String result = new CheckChainBuilder<>("unknown fail")
           .isNotBlank("abc")
           .isNotNull(new Object())
           .matchRegex("Apple No.1", "Apple No\\.\\w")
           .between(3, 1, 5)
           .end("success");
   // success
   System.out.println(result);
   ```

2. 校验失败时指定失败结果

   ```java
   String apple = "Apple";
   String result = new CheckChainBuilder<>("unknown fail")
           .isNotBlank(apple)
           .setFailResultCheck("apple 字段应该符合 Apple No.? 的规则").matchRegex(apple, "Apple No\\.\\w")
           .end("success");
   // apple 字段应该符合 Apple No.? 的规则
   System.out.println(result);
   ```

   推荐使用  `setFailResultCheck`  方法设置失败的结果，而不是使用  `setFailResult`，后者无论如何都会更改失败的结果；而前者在前方校验失败时，不会更改失败的结果。

### setFailResult 各种方式及区别

1. setFailResult

   ```java
   String result = new CheckChainBuilder<>("unknown fail")
           .isNotNull(null)
           .setFailResult("not blank fail")
           .isNotBlank("abc")
           .end("success");
   // not blank fail
   System.out.println(result);
   ```

   `setFailResult` 在 `isNotNull(null)`（校验失败后），依然执行了，说明 `setFailResult` 是无论如何都会执行的。而我们本意应该是在下面 `isNotBlank("abc")` 校验失败后才返回 "not blank fail"，对于这种需求，应当使用下面的 `setFailResultCheck`。

2. setFailResultCheck

   ```java
   String result = new CheckChainBuilder<>("unknown fail")
           .isNotNull(null)
           .setFailResultCheck("not blank fail").isNotBlank("abc")
           .end("success");
   // unknown fail
   System.out.println(result);
   ```

   `setFailResultCheck` 还具有 skipNext 的传递性，如下：

   ```java
   String result = new CheckChainBuilder<>("unknown fail", true)  // 开启 nullSkip 功能
           .ifNullThenSkip(null)
           .setFailResultCheck("not blank fail").isNotBlank("")
           .then(() -> { System.out.println("run..."); return true; })
           .end("success");
   // run... | success
   System.out.println(result);
   ```

   `ifNullThenSkip(Object)` 的作用是：如果传入的对象为 null 时，就会跳过下一步操作。可以看到，`setFailResultCheck` 和 `isNotBlank` 都没有执行，跳过下一步的操作被 `setFailResultCheck` 传递了，更符合直觉。

3. setResultFactory、setFailResultByFactory、setFailResultCheckByFactory

   这是 Result 类

   ```java
   class Result<T> {
       private int code = 500;
       private T data;
       private String msg;
   
       public Result(int code) {
           this.code = code;
       }
       
       // 将会使用在 setResultFactory 的构造方法
       public Result(String msg) {
           this.msg = msg;
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
   ```

   示例代码

   ```java
   Result<Object> result = new CheckChainBuilder<>(new Result<>(500), true)  // 开启 nullSkip 功能
           .setResultFactory(Result::new)
           .setFailResultCheckByFactory("not blank")
           .isNotBlank("")
           .end(new Result<>(200));
   // Result{code=500, data=null, msg='not blank'}
   System.out.println(result);
   ```

   `setResultFactory(String -> T)` 设置了构造结果的工厂函数，而 `setFailResultCheckByFactory(String)` 与 `setFailResultByFactory(String)` 会通过调用该函数，以函数的返回值来设置结果，设置结果的方式分别对应 `setFailResultCheck` 与 `setFailResult`。