package com.std.y.guava;

import com.google.common.util.concurrent.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * 一. guava 对java future进行了扩展，采用回调的方式进行异步处理
 * <p>
 * 二. 传统的future 需要不断的轮序future结果，或者get阻塞
 * <p>
 * <p>
 * ListenableFuture是可以监听的Future，它是对java原生Future的扩展增强。Future表示一个异步计算任务，当任务完成时可以得到计算结果。如果希望计算完成时马上就拿到结果展示给用户或者做另外的计算，就必须使用另一个线程不断的查询计算状态。这样做会使得代码复杂，且效率低下。如果使用ListenableFuture，Guava会帮助检测Future是否完成了，如果完成就自动调用回调函数，这样可以减少并发程序的复杂度。
 * <p>
 * 1、MoreExecutors
 * <p>
 * 该类是final类型的工具类，提供了很多静态方法。例如listeningDecorator方法初始化ListeningExecutorService方法，使用此实例submit方法即可初始化ListenableFuture对象。
 * <p>
 * 2、ListeningExecutorService
 * <p>
 * 该类是对ExecutorService的扩展，重写ExecutorService类中的submit方法，返回ListenableFuture对象。
 * <p>
 * 3、ListenableFuture
 * <p>
 * 该接口扩展了Future接口，增加了addListener方法，该方法在给定的excutor上注册一个监听器，当计算完成时会马上调用该监听器。不能够确保监听器执行的顺序，但可以在计算完成时确保马上被调用。
 * <p>
 * 4、FutureCallback
 * <p>
 * 该接口提供了OnSuccess和OnFailuren方法。获取异步计算的结果并回调。
 * <p>
 * 5、Futures
 * <p>
 * 该类提供和很多实用的静态方法以供使用。
 * <p>
 * 6、ListenableFutureTask
 * <p>
 * 该类扩展了FutureTask类并实现ListenableFuture接口，增加了addListener方法。
 * <p>
 * <p>
 * ps：如果我们main线程中启动的线程是非守护线程，那面非守护线程如果没有执行完成，则 jvm不会退出，虽然
 * main 线程执行完成了，jvm退出的原则是之后非守护线程全部执行完成了，才会退出
 * 守护线程 例如 gc线程，就相当于没有线程在工作了，那面守护线程没必要存活了，jvm可以退出了
 *
 *
 */
public class ListenableFutureY {
    // 创建线程池
    final static ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    public static void main(String[] args) throws Exception {
        should_test_furture();
    }

    /**
     * guava listenableFuture 的程序小实战
     * 采用添加回调的方式，异步执行future task 避免了jdk7 future的轮序，和get阻塞
     */
    public static void listanbleFuture() {
        Long t1 = System.currentTimeMillis();
        // 任务1
        ListenableFuture<Boolean> booleanTask = executorService.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Thread.sleep(5000);
                System.out.println("booleanTask 任务完成....");
                return true;
            }
        });
        // 任务2
        ListenableFuture stringTask = executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println(1 / 0);
            }
        });
        sleepMillis(2000);
        /***
         *
         * 1.添加回调随时可以，只不过是任务已经执行完了，如果添加回调，会立即调用回调
         * 2.如果任务没有执行完，那面将执行完成之后再调用回调，主线程添加回调的速度远比任务执行块啊
         * 3.可以添加sleep 来测是
         * */
        // 任务1添加回调
        Futures.addCallback(booleanTask, new FutureCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                System.out.println(getCid() + "booleanTask success...." + aBoolean);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(getCid() + "booleanTask fail...." + throwable.getMessage());
            }
        });
        // 任务2 添加监听回调
        Futures.addCallback(stringTask, new FutureCallback() {
            @Override
            public void onSuccess(Object o) {
                System.out.println(getCid() + "stringTask success......" + o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(getCid() + "stringTask fail...." + throwable.getMessage());
            }
        });
        System.out.println(getCid() + "添加回调完成......");
        System.out.println(getCid() + "main is over ");
    }

    public static void futures() {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(20));
        /**
         * 提交任务1 异步执行
         * */
        ListenableFuture<Integer> future1 = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                sleepMillis(1000);
                System.out.println("call future 1");
                return 1;
            }
        });
        /**
         * 提交任务2
         * */
        ListenableFuture<Integer> future2 = service.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {

                sleepMillis(1000);
                System.out.println("call future 2");
                return 2;
            }
        });
        final ListenableFuture allFutures = Futures.allAsList(future1, future2);

    /*    final ListenableFuture transForm=Futures.transform(allFutures, new AsyncFunction<List<Integer>,Boolean>() {

            @Override
            public ListenableFuture<Boolean> apply(List<Integer> results) throws Exception {
                return Futures.immediateFuture(String.format("success future:%d", results.size()));
            }
        });*/
    }

    public static void should_test_furture() throws Exception {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

        ListenableFuture future1 = service.submit(new Callable<Integer>() {
            public Integer call() throws InterruptedException {
                Thread.sleep(10000);
                System.out.println(getCid() + "call future 1.");
                return 1;
            }
        });

        ListenableFuture future2 = service.submit(new Callable<Integer>() {
            public Integer call() throws InterruptedException {
                Thread.sleep(1000);
                System.out.println(getCid() + "call future 2.");
                /***
                 * 如果task 执行异常，那不好意思，get就是个挂掉，会一直阻塞住
                 *
                 * 注意这里面加上异常的 操作测试 case ，好多隐在的问题将会暴露出来
                 *
                 * 如果测试正常流程，请把这里的异常去掉
                 * */
                throw new RuntimeException("call future 2.异常执行");
                // return 2;
            }
        });
        /**
         * 解决添加回调解决异常问题，知道失败之后如何处理，打印log可以知道future执行失败的原因
         * */

        Futures.addCallback(future2, new FutureCallback() {
            @Override
            public void onSuccess(Object o) {
                System.out.println(getCid() + "future2-onSuccess..." + o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(getCid() + "future2-onFailure..." + throwable);
            }
        });

        /**
         * 把多个future的结果进行合并,返回集合中的顺序，是根据allAsList(future2, future1);入参的顺序
         * 注意:添加回调，知道调用失败的原因
         * 注意：这里面只要有一个future 失败，就返回失败了
         * */
        final ListenableFuture allFutures = Futures.allAsList(future2, future1);
        Futures.addCallback(allFutures, new FutureCallback() {
            @Override
            public void onSuccess(Object o) {
                System.out.println(getCid() + "allFutures ..onSuccess.. " + o);
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(getCid() + "allFutures ..onFailure.. " + throwable);
            }
        });
        System.out.println(getCid() + "ready get future result.....");
        /**
         * get 肯定是会卡住的，因为他要等待多个future都完成，才能合并，然后才能触发allFutures的完成
         *
         * 1. 之所以没有打印出  transform 是因为，在发生异常的时候 get（）方法抛出了异常，导致下面的代码没有执行
         * 加上了try catch 一眼就看出来了
         * 2. 和jdk7中future异常的，如果执行过程中出现异常则get 方法也会返回异常
         * */

        try {
            System.out.println(getCid() + "allFutures result is :" + allFutures.get());
        } catch (Exception e) {
            System.out.println(getCid() + "allFutures exception is :" + e.getMessage());
        }
        /**
         * 1.转换,这个也要等到allFutures完成才能进行转换，也很好理解，future没有完成，转换个啥子呢？
         *
         * 2.这里面如果前面的 allFutures中有异常，则不会执行，如果transform设置了失败回调，则会调用失败的回调
         *
         * 3. 使用的时候最好的习惯就是都加上回调，要不不知道为啥失败，这里如果加上回调则执行回调，不加上回调，就不知道为啥
         * 没有调用AsyncFunction 的逻辑
         * 4. 这里面只要有一个future 失败，就返回失败了
         *
         *
         * */
        final ListenableFuture transform = Futures.transform(allFutures, new AsyncFunction<List<Integer>, Boolean>() {
            @Override
            public ListenableFuture apply(List<Integer> results) throws Exception {
                System.out.println(getCid() + "transform AsyncFunction: " + results);
                List<String> aList = new ArrayList<String>();
                for (int s : results) {
                    System.out.println("transform AsyncFunction for: " + s);
                    aList.add("convert" + s);
                }
                /**
                 * 立即返回一个future对象，带有返回值 就是new 一个对象
                 * */
                return Futures.immediateFuture(aList);
            }
        });
        /**
         * 添加回调知道调用失败的原因
         * */
        Futures.addCallback(transform, new FutureCallback<Object>() {
            public void onSuccess(Object result) {
                System.out.println(result.getClass());
                System.out.println(getCid() + "transform-onSuccess" + result);
            }

            public void onFailure(Throwable thrown) {
                System.out.println(getCid() + "transform-onFailure" + thrown.getMessage());
            }
        });

        System.out.println("main-transform-get()" + transform.get());
    }

    private static void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String getCid() {
        return "当前线程：" + Thread.currentThread().getId() + "-" + Thread.currentThread().getName() + "是否为守护线程 " + Thread.currentThread().isDaemon() + "-->";
    }
}
