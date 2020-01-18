package com.std.y.jdk8;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
/**
 * jdk8新特性   CompletableFutrue
 * <p>
 * 1. Async结尾同时将会使用ForkJoinPool.commonPool()
 */
public class CompletableFutrueY {
    /**
     * 1. 与jdk1.7中的 future是一个道理，一个东西，get() 方法，在没有执行完结果，都是阻塞的
     * 2. complete () 可以中断线程执行，直接返回 相当于调用interrupt 然后返回给个制定值，自己理解
     * 3. 增加了个Supplier 接口，相当于future中增加了CallAble 接口差不多
     * 4. Void 类，表示无返回值
     * 5. fockJoin 默认使用的线程是守护线程,普通线程池 则不然
     * 6. thenApply 传的function 调用 apply回调方法
     * 7. thenAccept 传的是consumer 调用 apply回调方法 没有返回值的
     * 8 .结果执行完成合并结果，请参考 com.std.y.jdk8.CountTask（fockJoin  执行结果拆分时候的join部分）
     * 9. excetption -handler 传入BiFunction函数
     * 10 .completeExceptionally 方法传入一个异常，让执行任务抛出异常
     * 11 .applyToEither 可以搞多个future任务跑，那个先执行完用那个，然后传入一个函数，对返回结果进行处理
     * 12. runAfterBoth 这个是当两个future都执行完才出发future完成，调用runnable 接口方法回调
     * 13 .anyOf 一堆future 有执行完的就立即返回
     */
    @Test
    public void blockGet() throws ExecutionException, InterruptedException {
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier() {
            @Override
            public String get() {
               /* try {
                    Thread.sleep(Integer.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
                int count = 0;
                while (Boolean.TRUE) {
                    if (count % 1000000 == 0) {
                        /*System.out.println("--------------");*/
                    }
                    count++;
                }
                return "Supplier get";
            }
        });
        Thread.sleep(3000);
        future.complete("提前完成！唤醒阻塞线程");
        System.out.println("result is :" + future.get());
        // 不让jvm 退出，分守护线程不要结束
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 处理有返回值的
     * <p>
     * suppliter 和lambdas 表达式都是支持的
     */
    @Test
    public void supplyAsync() throws ExecutionException, InterruptedException {
        // excemple 1
        Executor executor = Executors.newSingleThreadExecutor();
        final CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "result";
        }, executor);

        // excemple 2
        final CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "result2";
            }
        }, executor);
        System.out.println(future.get());
        System.out.println(future2.get());
    }

    /***
     * 没有返回值的处理，就是future 中没有返回值，但是还是会返回future对象的
     */
    @Test
    public void runAsync() throws InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("run....");
            }
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("lambda 表达式玩法");
        });
        Thread.sleep(Integer.MAX_VALUE);

    }

    /**
     * completedFuture 作用  就是构造一个固定值的 CompletableFuture
     * 直接返回一个CompletableFuture对象
     */
    @Test
    public void completedFuture() throws ExecutionException, InterruptedException {
        // 直接返回结果创建了个future对象
        CompletableFuture<String> cf = CompletableFuture.completedFuture("test");
        System.out.println(cf.get());
    }

    /**
     * 同步执行
     * thenApply 方法是同步执行的，就是掉用普通方法没有什么区别
     * <p>
     * String::toUpperCase labda 表达式，调用string 中的静态方法，把“message”转换成大写
     */
    @Test
    public void thenApply() throws ExecutionException, InterruptedException {
        // 这个message 默认就是会穿过去，典型的流逝处理思想
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApply(String::toUpperCase);

        CompletableFuture<String> cf2 = CompletableFuture.completedFuture("message").thenApply(new Function<String, String>() {
            @Override
            public String apply(String s) {
                for (int i = 0; i < 10; i++) {
                    System.out.println("同步执行:thenApply: " + i);
                }
                sleepMillis(3000);
                return "over";
            }
        });
        // jvm 不会退出，因为thenApply是同步的，所以必须要等待方法调用完成才会继续往下执行 main线程的代码
        System.out.println(cf.get());
        System.out.println(cf2.get());
        System.out.println("main is over jvm exit....");

    }

    /**
     * 异步执行
     * <p>
     * 1.概念不要混淆，想让那个线程执行完，自己在去执行，就让需要执行完的那个线程执行join
     * cf.join 的意思就是先让futre中异步执行完成，main线程在结束
     * 2.如果不加入join的话，会看不到thenApplyAsync的执行结果，main线程就执行完了，原因就是如下：
     * CompletableFuture 默认启动的守护线程，所以 jvm会退出
     */
    @Test
    public void thenApplyAsync() throws InterruptedException {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            System.out.println(getCid() + Thread.currentThread().isDaemon());
            sleepMillis(3000);
            return s.toUpperCase();
        });
        System.out.println(getCid() + cf.getNow(null));
        //  cf.join() 意思就是 main线程必须要等子线程运行完，自己才能结束
        System.out.println(getCid() + cf.join());
        Thread.currentThread().join();
    }

    /**
     * 异步执行用用户自己指定的线程池
     */
    @Test
    public void useUserThread() {
        Executor executor = Executors.newCachedThreadPool();
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            System.out.println(getCid() + Thread.currentThread().isDaemon());
            sleepMillis(3000);
            return s.toUpperCase();
        }, executor);

        System.out.println(getCid() + cf.getNow(null));
        //  cf.join() 意思就是 main线程必须要等子线程运行完，自己才能结束
        System.out.println(getCid() + cf.join());

    }

    /**
     * 同步执行 消费者模式，也就是说不需要返回值，只需要前一次的执行结果就可以了，不需要返回本次的结果
     * <p>
     * 生产者消费者模式,只需要前一次的执行结果，而不需要返回本次执行结果
     * 不带 Async的方法都是同步执行的，不会往线程池里面放任务，异步去执行
     * thenAccept 每次返回的都是 new的新future对象
     */

    @Test
    public void thenAccept() {
        StringBuilder builder = new StringBuilder();
        CompletableFuture.completedFuture("thenAccept message").thenAccept(new Consumer<String>() {
            @Override
            public void accept(String s) {
                builder.append(s + "-");
            }
        }).thenAccept(s -> {
            builder.append(s);
        });
        // 消费者模式，不需要返回值
        System.out.println(builder.toString());
    }

    /**
     * 异步执行 消费者模式
     */
    @Test
    public void thenAcceptAsync() {
        StringBuilder builder = new StringBuilder();
        CompletableFuture future = CompletableFuture.completedFuture("thenAcceptAsync mssage").thenAcceptAsync(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(getCid() + "线程异步执行-thenAcceptAsync s is :" + s);
                sleepMillis(3000);
                builder.append(s);
            }
        });
        // 这里面需要join 否则拿不到结果，main线程就结束了，jvm退出，fockJoin 使用的守护线程，what a fuck
        future.join();
        System.out.println(builder.toString());
        System.out.println("尼玛?");
    }

    /**
     * 计算中异常处理
     */
    @Test
    public void exception() throws ExecutionException, InterruptedException {
        //thenApplyAsync 入参，function 和一个线程池
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                System.out.println(getCid() + "ready execute...");
                sleepMillis(5000);
                return s.toUpperCase();
            }
        });
        CompletableFuture<String> exceptionHandler = cf.handle((s, th) -> {
            return (th != null) ? "message upon cancel" : "";
        });
        Thread.sleep(1000);
        System.out.println(getCid() + "ready make exception...");
        cf.completeExceptionally(new RuntimeException("completeExceptionally exception test"));
        System.out.println("是否异常：" + cf.isCompletedExceptionally());
        // 抛出异常的话，调用get会直接抛出异常来
        System.out.println(cf.get());
    }

    /**
     * 计算中异常处理   程序中运行时异常
     */
    @Test
    public void exception2() throws ExecutionException, InterruptedException {
        //thenApplyAsync 入参，function 和一个线程池
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                System.out.println(getCid() + "exception2 Async apply ready execute..");
                sleepMillis(1000);
                System.out.println(getCid() + "exception2 Async apply ready execute over..");
                // 程序这里执行出现异常
                System.out.println(1 / 0);
                return s.toUpperCase();
            }
        });
        /***
         * 相当于注册异常回调
         * */

        Thread.sleep(3000);
        System.out.println(getCid() + "准备注册异常回调函数");
        CompletableFuture<String> exceptionHandler = cf.handle((s, th) -> {
            return (th != null) ? "message upon cancel" : "";
        });
        // cf.completeExceptionally(new RuntimeException("completeExceptionally exception test"));
        System.out.println("是否异常：" + cf.isCompletedExceptionally()); // 异步执行，这里面可能会返回false，因为异步程序还没有执行完

        System.out.println("exceptionHandler 返回降级结果: " + exceptionHandler.get());
        // 抛出异常的话，调用get会直接抛出异常来
        System.out.println(cf.get());  // 这里面get 会一直阻塞住
    }


    /**
     * 取消计算任务
     * <p>
     * 取消完，在get 就会抛出异常滴
     */

    @Test
    public void cancelTask() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.completedFuture("message").thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(3000);
                return "计算耗时3000s,进行了前所未有的超复杂运算";
            }
        });
        CompletableFuture future2 = future.exceptionally(throwable -> "啥尼玛用法啊这个是，真恶心");
        System.out.println(getCid() + "future" + future);
        System.out.println(getCid() + "future2" + future2);
        future.cancel(true);
        future2.join();
        /**
         *
         * 如果已经取消了，则get会抛出异常
         * */
        System.out.println(getCid() + "ready get result for future");
        System.out.println(future.get());
    }

    /**
     * 一个CompletableFuture 接受两个异步计算结果,那个future结果先计算完，就返回那个future
     * 重点来了, 思想要转变，异步不代表着顺序完全随机，或者混乱，fox join 也是有等待future执行完成在进行结果合并的思想的
     * <p>
     * eg： 两个future都自己异步计算，但是当前线程等待两个异步计算结果完成，然后再汇总两个异步计算的结果
     * <p>
     * future1.join
     * future2.join
     * return future1.get()+future2.get()
     */
    @Test
    public void appyToEither() {
        String original = "message Either";
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(100);
                System.out.println(getCid() + "message Either execute over");
                return s.toUpperCase();
            }
        });
        // applyToEither 任何api 调用，返回的都是新的future对象
        /***
         *
         * 1.cf1因为之前已经thenApplyAsync一个了任务了
         * 2.cf1现在又applyToEither一个新的任务
         * 3.cf1现在就是有选择了，就相当于面试拿了很多offer第一个给offer的是哪家公司就去那家公司，没毛病，虽然现实中不是这样，也没有更好的例子了
         *
         * */
        CompletableFuture<String> cf2 = cf1.applyToEither(CompletableFuture.completedFuture("test-yang").thenApplyAsync(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        sleepMillis(5000);
                        System.out.println(getCid() + "future-test-yang execute over");
                        return s.toUpperCase();
                    }
                }),
                s -> "我去你妈的，这种思想真的很不适应" + s
        );
        // 阻塞住
        String result = cf2.join();
        // join 完成，打印结果
        System.out.println(result);
    }

    /**
     * 运行两个阶段之后执行
     * <p>
     * 下面的测试 case 全是同步执行的，没有带Async的都是同步执行的
     */
    @Test
    public void runAfterBoth() {
        String original = "message";
        StringBuilder builder = new StringBuilder();
        CompletableFuture.completedFuture(original).thenApply(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(1000);
                System.out.println(getCid() + "执行完成...");
                return "f1";

            }
        }).runAfterBoth(
                CompletableFuture.completedFuture(original).thenApply(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        sleepMillis(10000);
                        System.out.println(getCid() + "执行完成...");
                        return "f2";
                    }
                }), new Runnable() {
                    @Override
                    public void run() {
                        // sleepMillis(3000);
                        System.out.println(getCid() + "both 回调 runnable....");
                        builder.append("ok done");

                    }
                }
        );
        System.out.println(builder.toString() + "original is " + original);

    }

    /**
     * 运行两个阶段之后执行
     * <p>
     * 下面的测试 case 全是同步执行的，没有带Async的都是同步执行的
     * <p>
     * 重点：两个future都执行完成，才能执行runnable 回调方法，否则，等待两个future执行完成
     */
    @Test
    public void runAfterBoth2() {
        String original = "message";
        StringBuilder builder = new StringBuilder();
        CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(5000);
                System.out.println(getCid() + "f1执行完成...");
                return "f1";

            }
        }).runAfterBoth(
                CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        sleepMillis(100);
                        System.out.println(getCid() + "f2执行完成...");
                        return "f2";
                    }
                }), new Runnable() {
                    @Override
                    public void run() {
                        // sleepMillis(3000);
                        System.out.println(getCid() + "both 回调 runnable....");
                        builder.append("ok done");

                    }
                }
        ).join();//.join()   不能让主线程退出
        // 不能让主线程退出
        System.out.println(builder.toString() + "original is " + original);
    }

    /***
     * **同步的方式**
     * 整合两个计算结果 Combine(联合)
     * thenApply 同步执行的，可以看源码，没有向线程池中放任务
     */
    @Test
    public void thenCombine() throws ExecutionException, InterruptedException {
        String original = "Message";
        CompletableFuture cf = CompletableFuture.completedFuture(original).thenApply(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(3000);
                System.out.println(getCid() + "任务1执行完成....");
                return s.toUpperCase();
            }
        }).thenCombine(CompletableFuture.completedFuture(original).thenApply(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(1000);
                System.out.println(getCid() + "任务2执行完成....");
                return s.toLowerCase();
            }
        }), (s1, s2) -> s1 + s2);
        // 从打印来看，上面代码全都在同步执行，所以下面这个打印只有当上面代码全都执行完成，才会打印出来
        System.out.println(getCid() + "main线程代码全都跑完了，准备获取结果");
        System.out.println(cf.get());
    }


    /***
     * **异步方式**
     * 整合两个计算结果 Combine(联合)
     * thenApplyAsync 异步执行，把任务提交到工作队列中，由线程并行执行，可以看源码，没有向线程池中放任务
     */
    @Test
    public void thenCombine2() throws ExecutionException, InterruptedException {
        String original = "Message";
        CompletableFuture cf = CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(5000);
                System.out.println(getCid() + "咋地，想合并结果，不好意思，我最近状态不好，刚睡了一会，现在我已经完成任务了，哈哈");
                return s.toUpperCase();
            }
        }).thenCombine(CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(1000);
                System.out.println(getCid() + "睡了1秒，开始出任务...");
                return s.toLowerCase();
            }
        }), (s1, s2) -> s1 + s2).whenComplete((s, th) -> {
            System.out.println(getCid() + "s is " + s);
        });
        // 从打印来看，上面代码全都在同步执行，所以下面这个打印只有当上面代码全都执行完成，才会打印出来
        System.out.println(getCid() + "main线程代码全都跑完了，准备获取结果");
        // 上面执行结果没有完成，会阻塞 main线程，没毛病
        System.out.println(cf.get());
    }

    /***
     * thenCompose 和thenCombine差不多
     * **异步方式**
     * 整合两个计算结果 Combine(联合)
     * thenApplyAsync 异步执行，把任务提交到工作队列中，由线程并行执行，可以看源码，没有向线程池中放任务
     */
    @Test
    public void thenCompose() throws ExecutionException, InterruptedException {
        String original = "Message-thenCompose";
        CompletableFuture cf = CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(3000);
                System.out.println(getCid() + "咋地，想合并结果，不好意思，我最近状态不好，刚睡了一会，现在我已经完成任务了，哈哈");
                return s.toUpperCase();
            }
        }).thenCombine(CompletableFuture.completedFuture(original).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(1000);
                System.out.println(getCid() + "睡了1秒，开始出任务...");
                return s.toLowerCase();
            }
        }), (s1, s2) -> s1 + s2);
        // 从打印来看，上面代码全都在同步执行，所以下面这个打印只有当上面代码全都执行完成，才会打印出来
        System.out.println(getCid() + "main线程代码全都跑完了，准备获取结果");
        // 上面执行结果没有完成，会阻塞 main线程，没毛病
        System.out.println(cf.get());
    }

    /**
     * anyOf 方法
     * <p>
     * 就是一大堆future中获取最先执行完成的future
     * <p>
     * 下面代码采用的同步的执行方式
     */
    @Test
    public void anyOf() throws ExecutionException, InterruptedException {
        List<CompletableFuture> futures = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("b", "a", "c");
        for (String msg : messages) {
            CompletableFuture future = CompletableFuture.completedFuture(msg).thenApply(new Function<String, String>() {
                @Override
                public String apply(String s) {
                    System.out.println(getCid() + "upperCaseing.......");
                    return s.toUpperCase();
                }
            });
            futures.add(future);
        }
        CompletableFuture future = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()])).whenComplete((res, th) -> {
            System.out.println(getCid() + "th is " + th);
            System.out.println(getCid() + "res is " + res);
            result.append(res);
        });

        System.out.println("final result is :" + future.get());
    }


    /**
     * anyOf 方法 异步执行
     * <p>
     * 就是一大堆future中获取最先执行完成的future
     * <p>
     * 下面代码采用的同步的执行方式
     */
    @Test
    public void anyOf2() throws ExecutionException, InterruptedException {
        List<CompletableFuture> futures = new ArrayList<>();
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("b", "a", "c");
        for (String msg : messages) {
            CompletableFuture future = CompletableFuture.completedFuture(msg).thenApplyAsync(new Function<String, String>() {
                @Override
                public String apply(String s) {
                    /**
                     * 始终是最先返回
                     * */
                    if (s.equals("a"))
                        sleepMillis(50);
                    else
                        sleepMillis(5000);
                    System.out.println(getCid() + "upperCaseing.......");
                    return s.toUpperCase();
                }
            });
            futures.add(future);
        }
        /**
         * 这个哥们很粗暴，不管那个事，有一个执行完成里面返回结果给future
         * 注意这里面是同步的，要等待所有的futur返回，也就是说，这里是阻塞的
         *
         * whenComplete 就是个回调函数
         * */
        CompletableFuture future = CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()])).whenComplete((res, th) -> {
            System.out.println(getCid() + "th is " + th);
            System.out.println(getCid() + "res is " + res);
            result.append(res);
        });
        // CompletableFuture.anyOf 是阻塞的，等待结果，所以future没有返回这里面肯定不会执行到

        System.out.println("final result is :" + future.get());
    }


    @Test
    public void stream() {
        List messages = Arrays.asList("a", "b", "c");
        messages.stream().map(new Function() {
            @Override
            public Object apply(Object o) {
                System.out.println("毛线意思？" + o);
                return o;
            }
        });
        System.out.println(messages);
    }

    /***
     * 结课
     */
    @Test
    public void testFinal() throws ExecutionException, InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                String result = "A";
                sleepMillis(5000);
                System.out.println(getCid() + "ok ready return result is :" + result);
                return result;
            }
        }).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                sleepMillis(2000);
                System.out.println(getCid() + "thenApplyAsync1:resive msg is :" + s);

                String newResult = "thenApplyAsync" + s;
                return newResult;
            }
        }).whenCompleteAsync((s,th) ->{
            System.out.println(getCid()+"whenComplete call back....");
        }).thenApplyAsync((q) ->{
            System.out.println(getCid()+"final .....");
            return "异步执行一大堆，实际上我就是返回给你个这样的结果，哥们洗洗睡吧！";


        });
        System.out.println(future.get());
    }

    private void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getCid() {
        return "当前线程：" + Thread.currentThread().getId() + Thread.currentThread().getName() + " ";
    }


    /***
     * PS:
     * whenComplete这个方法研究一下
     * */

}
