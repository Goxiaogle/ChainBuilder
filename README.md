[TOC]

# 中文

## ChainBuilder（链式判断构造）

一个优雅且能快速构造链式判断语句的类似 Optional 工具

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
   
## CheckChainBuilderFactory（通过给实体类附加注解快速校验数据）

### 使用示例

首先创建一个 Apple 类，用于代表平常的实体类：

```java
class Apple {
    @CheckRegex("Apple \\d+ Pro")
    String name;
    @CheckNumberBetween(left = 0, right = 100)
    Long price;
    // 自定义原因，并且可以使用替代词：{fileName} 指代字段的名字
    @CheckSize(right = 5, reason = @Reason("{fieldName} should not exceed 5 words!"))
    String description;

    public Apple(String name, Long price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
```

测试示例：

```java
Apple apple = new Apple("Apple x Pro", 30L, "test");
// Apple apple = new Apple("Apple 14 Pro", 200L, "test");
// Apple apple = new Apple("Apple 14 Pro", 30L, "test123");
Result<Object> result = new CheckChainBuilder<>(new Result<>(500), true)  // 开启 nullSkip 功能
        .setResultFactory(Result::new)
        .buildBy(new CheckChainBuilderFactory(apple)::create)
        .end(new Result<>(200));
// Result{code=500, data=null, msg='[name] 字段的值 >Apple x Pro< 不匹配正则: Apple \d+ Pro'}
// Result{code=500, data=null, msg='[price] 的值应当介于 0 与 100 之间，实际为 >200<'}
// Result{code=500, data=null, msg='description should not exceed 5 words!'}
System.out.println(result);
```

### 自定义处理方式（自定义注解+自定义校验）

1. 新建一个注解

   ```java
   @Retention(RetentionPolicy.RUNTIME)
   @Target({ElementType.FIELD})
   @interface CheckBoolean {
       boolean value();
       // 如果要自定义原因，推荐放在注解里
       Reason reason() default @Reason;
   }
   ```

   @Reason 推荐放在你的自定义注解里，这样如果有多注解需求可以针对不同注解有单独的原因说明。也可以不放，直接标记在字段上也行，这里不会影响什么，如何处理还是由下面一步的处理器决定的。

2. 新建一个处理器类

   ```java
   class CheckBooleanHandler extends AnnotationAndTypeHandler<CheckBoolean> {
   
       public CheckBooleanHandler() {
           // 第一个参数是自定义注解的 class，第二个参数是注解应当在哪种类型的字段上，可以是多个类型
           super(CheckBoolean.class, Boolean.class);
       }
   
       @Override
       public void handle(@NotNull CheckChainBuilder<?> builder, @NotNull FieldInfo<CheckBoolean> fieldInfo) {
           // targetAnnotation 就是上面 super() 中的第一个参数的实例
           CheckBoolean checkBoolean = fieldInfo.getTargetAnnotation();
           CheckChainBuilderUtils.addReason(
                   builder,
                   // 这里要传入一个 @Reason，由于我们在自定义注解里放入了，所以直接在注解上获取就行，如果你是放在字段上，则可以使用 fieldInfo.getField().getAnnotation(Reason.class)
                   checkBoolean.reason(),
                   // 如果未设置 @Reason 的值，会使用的默认内容
                   "[{fieldName}] 要求为 " + checkBoolean.value() + "，实际为 >{fieldValue}<",
                   fieldInfo
           ).setProceed(checkBoolean.value() == (boolean) fieldInfo.getFieldValue());
       }
   }
   ```

   你可以根据自己需求，选择实现接口 `CheckHandler`（最基础的接口）、`CheckWhenNotNullHandler`（做了字段值为 null 的处理），或者是继承 `AnnotationAndTypeHandler<A: Annotation>`（实现了上面的接口，主要面对处理注解）

3. 新建一个测试用的实体类 Test

   ```java
   class Test {
       @CheckBoolean(false)
       Boolean isDelete;
   
       public Test(Boolean isDelete) {
           this.isDelete = isDelete;
       }
   }
   ```

4. 运行测试

   ```java
   // put in global handlers
   CheckChainBuilderSetting.getCheckHandlers().add(0, new CheckBooleanHandler());
   
   Test test = new Test(true);
   Result<Object> result = new CheckChainBuilder<>(new Result<>(500), true)  // 开启 nullSkip 功能
           .setResultFactory(Result::new)
           .buildBy(new CheckChainBuilderFactory(test)::create)
           .end(new Result<>(200));
   // Result{code=500, data=null, msg='[isDelete] 要求为 false，实际为 >true<'}
   System.out.println(result);
   ```

   第一行代码是将我们自定义的处理器放入全局处理器中，为了避免可能产生的冲突，建议把自定义处理器放在最前面，防止被其它默认的处理器所处理。

   同样，我们可以尝试使用自定义原因，将 Test 的 isDelete 字段的注解更改为：`@CheckBoolean(value = false, reason = @Reason("[{fieldName}] 数据已被删除"))`，运行结果是：`Result{code=500, data=null, msg='[isDelete] 数据已被删除'}`。

### 自定义替换词

   既然可以自定义校验处理方式，chaiin-builder 当然也支持自定义替换词。当然，下面的方式是针对于在自定义处理器中使用 `CheckChainBuilderUtils.addReason` 来设置原因的。你如果不使用 `addReason`，而使用 `setFailResultCheckByFactory` 来设定原因，你可以用你自己的方式去实现替换词。

1. 将自定义替换词加入全局设置中：

   ```java
   CheckChainBuilderSetting.getReasonKeywordReplaceMap().put("{time}", (info) -> new Date().toString());
   ```

   Map 中，第一个参数是要替换的词语，第二个参数是 (FieldInfo) -> String 类型的函数，返回的值将会替换对应的替换词

2. 沿用上面的例子，将 Test 的注解改为 `@CheckBoolean(value = false, reason = @Reason("现在时间是 {time}"))`

3. 运行测试，返回的结果是：`Result{code=500, data=null, msg='现在时间是 Tue Feb 07 16:53:12 CST 2023'}`
