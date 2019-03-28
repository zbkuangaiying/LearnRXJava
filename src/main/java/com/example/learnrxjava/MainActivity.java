package com.example.learnrxjava;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getClass().getSimpleName();
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        BaseRXJava(); //基础rxjava用法
//        streamStyleBaseRXJava(); //流式rxjava用法
//        swithThread(); //练习使用线程
//        basicSwithThread(); //基础线程切换练习
//        ConsumerTag(); //练习使用consumerTag 并且 引出Disposable对象以及其使用
//        mapTagAndSwithThread(); //使用map tag  map 基本作用就是将一个 Observable 通过某种函数关系，转换为另一种 Observable，
//        zipTag(); //zip 专用于合并事件，该合并不是连接（连接操作符后面会说），而是两两配对，也就意味着，最终配对出的 Observable 发射事件数目只和少的那个相同。
//        concatTag(); // 对于单一的把两个发射器连接成一个发射器 按照发射器顺序  有条不紊
//        flatMapTag(); //一变多 无序
//        concatMapTag();//有序组合并接收事件
//        distantTag();//去重
//        filterTag();//过滤 自定义过滤规则
//        bufferTag(); // 对事件按照指定参数的规则分割..得到的一定是一个数组
//        timerTag();//定时任务  默认执行在新线程  相当于 hander.postDelay函数
//        intervalTag(); // 轮询
//        doOnNextTag();//在执行观察者任务之前做的事 注意一下线程  执行doOnNext的线程与 观察者的线程一致
//        skipTag();//跳过事件    参数是跳过任务的个数
//        takeTag();//接受事件的次数  高于轮询 高于takeUnitl
//        justTag();//简单的事件发射器 依次执行onNext();
//        singleTag(); //只接收一个参数 要么调用onSuccess 要么就抛异常
//        debounceTag(); //不是很懂    大概是防抖设计   只接受限定时间值内的最后一个事件    比如1秒收一个  你200毫秒发一次 只接收最后一次的事件  下一次事件是1秒后 则可以接
//        deferTag();//只有在确定订阅之后 才会去执行创造事件时的代码..... 可以在创造与订阅之间执行别的事... 比如变量数值改变..  Defer创建事件代码的时候 i是12 事件被订阅的时候i是15 那么defer真正创建事件的时候i 就是 15
//        lastTag(); //只接收能能收的最后一个事件    无论lastTag的值是什么
//        margeTag(); //合并事件  交错合并
//        reduceTag();//操作符每次用一个方法处理一个值，在 观察者这里只显示结果 即 之调用onNext一次
//        scanTag();//操作符每次用一个方法处理一个值，在 观察者这里显示过程 即 之调用onNext多次
//        windowTag(); //将指定数量的事件分发给观察者

    }

    private void windowTag() {
        Observable.interval(1,TimeUnit.SECONDS)
                .take(15)
                .window(5,TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Observable<Long>>() {
                    @Override
                    public void accept(Observable<Long> longObservable) throws Exception {
                        TestLog.e("onWindow : ");
                        longObservable.subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        TestLog.e("onNext : " + aLong);
                                    }
                                });
                    }
                });
    }

    private void scanTag() {
        Observable.just(1,6,9).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                TestLog.e("reduce paramer : " + integer+","+integer2);
                return integer+integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("onNext : " + integer);
            }
        });
    }

    private void reduceTag() {
        Observable.just(1,6,9).reduce(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                TestLog.e("reduce paramer : " + integer+","+integer2);
                return integer+integer2;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("onNext : " + integer);
            }
        });
    }

    private void margeTag() {
        Observable.merge(Observable.intervalRange(0, 3, 1, 1,TimeUnit.SECONDS), Observable.intervalRange(2, 3, 1, 1,TimeUnit.SECONDS))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        TestLog.e("onNext : " + aLong);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        TestLog.e("onComplete " );
                    }
                });

//        Observable.concat(Observable.just(6,8,0),Observable.just(1,3,5)).subscribe(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                TestLog.e("onNext : " + integer);
//            }
//        });
    }

    private void lastTag() {
        Observable.just(1,2,3,4,5,6,7)
                .last(3)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("onNext : " + integer);
                    }
                });
    }

    int i = 12;
    private void deferTag() {
        Observable<Integer> observable = Observable.defer(new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                return Observable.just(i);
            }
        });
        i = 15;
        observable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("onNext : " + integer);
            }
        });

    }

    private void debounceTag() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(400);
                emitter.onNext(2);
//                Thread.sleep(505);
                Thread.sleep(400);
                emitter.onNext(3);
//                Thread.sleep(100);
                Thread.sleep(200);
                emitter.onNext(4);
//                Thread.sleep(605);
                Thread.sleep(1400);
                emitter.onNext(5);
//                Thread.sleep(510);
                Thread.sleep(1000);
            }
        }).debounce(1000,TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("onNext : " + integer);
                    }
                });
    }

    private void singleTag() {
//        Single.just(new Random().nextInt()).subscribe(new SingleObserver<Integer>() {
//            @Override
//            public void onSubscribe(Disposable disposable) {
//                TestLog.e("onSubscribe  disposable: " + (disposable == null));
//            }
//
//            @Override
//            public void onSuccess(Integer integer) {
//                TestLog.e("onSuccess : " + integer);
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                TestLog.e("onError : " + throwable.getMessage());
//            }
//        });
        Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(SingleEmitter<Integer> singleEmitter) throws Exception {
                singleEmitter.onSuccess(1);
                singleEmitter.onSuccess(2);
                singleEmitter.onSuccess(3);
//                singleEmitter.onError(new Throwable("test error"));
            }
        }).subscribe(new SingleObserver<Integer>() {
            @Override
            public void onSubscribe(Disposable disposable) {
                TestLog.e("onSubscribe  disposable: " + (disposable == null));
            }
            @Override
            public void onSuccess(Integer integer) {
                TestLog.e("onSuccess : " + integer);
            }
            @Override
            public void onError(Throwable throwable) {
                TestLog.e("onError : " + throwable.getMessage());
            }
        });
    }

    private void justTag() {
        Observable.just(1,2,3,4,5).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("just : " + integer);
            }
        });
    }

    private void takeTag() {
        Observable.interval(0,1,TimeUnit.SECONDS).take(5).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<Integer>>() {
                    int i = 0;
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) throws Exception {
                        return Observable.just(i++);
                    }
                }).takeUntil(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer == 8;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("subscribe " + integer);
            }
        });
    }

    private void skipTag() {
        Observable.just(1,2,3,4,5).skip(2).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("展示结果:"+integer);
            }
        });
    }

    private void doOnNextTag() {
        Observable.just(1,2,3,4,5).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("先保存一下"+integer+",当前执行的线程是"+Thread.currentThread().getName());
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("展示结果:"+integer+",当前执行的线程是"+Thread.currentThread().getName());
            }
        });
    }

    private void intervalTag() {
        mDisposable = Observable.interval(0, 2, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
                .flatMap(new Function<Long, ObservableSource<Integer>>() {
                    int i =0;
                    @Override
                    public ObservableSource<Integer> apply(Long aLong) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                                observableEmitter.onNext(i++);
                            }
                        });
                    }
                })
                .takeUntil(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer == 555;
                    }
                }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                TestLog.e("subscribe " + integer);
            }
        });


    }

    private void timerTag() {
        TestLog.e("timer start at :" + new Date());
        Observable.timer(2, TimeUnit.SECONDS).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        TestLog.e("timer now:" + aLong + "at" + new Date());
                    }
                });
    }

    private void bufferTag() {
        Observable.just(1, 2, 3, 4, 5, 6).buffer(3, 2).subscribe(new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> integers) throws Exception {
                TestLog.e("经过buffer处理的结果 得到的数组长度是------------>" + integers.size());
                TestLog.e("经过buffer处理的结果是------------>" + integers.toString());
            }
        });
    }

    private void filterTag() {
        Observable.just(1, 2, 3, 4, 5, 6).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) throws Exception {
                return integer % 2 > 0;
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("经过filter处理的结果是------------>" + integer);
                    }
                });
    }

    private void distantTag() {
        Observable.just(1, 2, 2, 3, 3, 4, 4, 4)
                .distinct()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("经过distinct处理的结果是------------>" + integer);
                    }
                });
    }

    /**
     * 它可以把一个发射器 Observable 通过某种方法转换为多个 Observables，然后再把这些分散的 Observables装进一个单一的发射器 Observable。
     * 但有个需要注意的是，concatMap 能保证事件的顺序
     */
    private void concatMapTag() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < 3; i++) {
                            list.add("I am value  " + integer);
                        }
                        return Observable.fromIterable(list).delay(1, TimeUnit.SECONDS);
                    }
                }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                TestLog.e("concatmap之后的结果是 ----->" + s);
            }
        });
    }

    /**
     * 它可以把一个发射器 Observable 通过某种方法转换为多个 Observables，然后再把这些分散的 Observables装进一个单一的发射器 Observable。
     * 但有个需要注意的是，flatMap 并不能保证事件的顺序，如果需要保证，需要用到我们下面要讲的 ConcatMap
     */
    private void flatMapTag() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(Integer integer) throws Exception {
                List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("i am value " + integer);
                }
                int delayTime = (int) (1 + Math.random() * 10);
                return Observable.fromIterable(list).delay(delayTime, TimeUnit.SECONDS);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        TestLog.e("经过flatmap处理的结果是------------>" + s);
                    }
                });
    }

    private void concatTag() {
        Observable.concat(Observable.just(1, 5, 10), Observable.just(2, 4, 11))
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("concat resukt ----->" + integer);
                    }
                });

    }

    private void zipTag() {
        Observable.zip(getStringObservable(), getIntegetObservable(), new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer integer) throws Exception {
                return s + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                TestLog.e("zip之后的结果是----->" + s);
            }
        });
    }

    private ObservableSource<? extends Integer> getIntegetObservable() {
        return Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext(1);
                    TestLog.e("Integer  emitter---->1");
                    e.onNext(2);
                    TestLog.e("Integer  emitter---->2");
                    e.onNext(3);
                    TestLog.e("Integer  emitter---->3");
                    e.onNext(4);
                    TestLog.e("Integer  emitter---->4");
                    e.onNext(5);
                    TestLog.e("String  emitter---->5");
                    e.onNext(6);
                    TestLog.e("String  emitter---->6");
                    e.onNext(7);
                    TestLog.e("String  emitter---->7");
                    e.onNext(8);
                    TestLog.e("String  emitter---->8");
                }
            }
        });
    }

    private ObservableSource<? extends String> getStringObservable() {
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                if (!e.isDisposed()) {
                    e.onNext("A");
                    TestLog.e("String  emitter---->A");
                    e.onNext("B");
                    TestLog.e("String  emitter---->B");
                    e.onNext("C");
                    TestLog.e("String  emitter---->C");
                    e.onNext("D");
                    TestLog.e("String  emitter---->D");
//                    e.onNext("E");
//                    TestLog.e("String  emitter---->E");
                }
            }
        });
    }

    private void ConsumerTag() {
        mDisposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
//                observableEmitter.onNext(123);
//                Thread.sleep(3000);
//                observableEmitter.onNext(456);
                while (!mDisposable.isDisposed()) {
                    observableEmitter.onNext(123);
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        throw e;
                    }

                }
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, integer + "," + new Date());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable + "," + new Date());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.e(TAG, "action----->并不知道这个类是用来做什么的," + new Date());
                    }
                });
    }

    private void basicSwithThread() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                observableEmitter.onNext("连载1");
                observableEmitter.onNext("连载2");
                observableEmitter.onNext("连载3");
                Log.e(TAG, "currentThrad:" + Thread.currentThread().getName());
                observableEmitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        mDisposable = disposable;
                        Log.e(TAG, "onSubscribe" + ", currentThrad:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onNext(String s) {
                        if ("2".equals(s)) {
                            mDisposable.dispose();
                        }
                        Log.e(TAG, "onNext:" + s + ", currentThrad:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "onError:" + throwable.getMessage() + ", currentThrad:" + Thread.currentThread().getName());
                    }

                    @Override
                    public void onComplete() {
                        Log.e(TAG, "onComplete" + ", currentThrad:" + Thread.currentThread().getName());
                    }
                });
    }

    private void streamStyleBaseRXJava() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                TestLog.e("Observable emit 1" + "\n");
                observableEmitter.onNext(1);
                TestLog.e("Observable emit 2" + "\n");
                observableEmitter.onNext(2);
                TestLog.e("Observable emit 3" + "\n");
                observableEmitter.onNext(3);
                observableEmitter.onComplete();
                TestLog.e("Observable emit 4" + "\n");
                observableEmitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private int i;

            @Override
            public void onSubscribe(Disposable disposable) {
                mDisposable = disposable;
            }

            @Override
            public void onNext(Integer integer) {
                TestLog.e("onNext value---->" + integer + "\n");
                i++;
                if (i == 2) {
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                TestLog.e("onError : value :" + throwable.getMessage() + "\n");
            }

            @Override
            public void onComplete() {
                TestLog.e("onComplete" + "\n");
            }
        });
    }

    private void BaseRXJava() {
        //       第一步 创建被观察者 创建连载小说 创建事件源
        Observable navel = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                //Observable中文意思就是被观察者，
                // 通过create方法生成对象，里面放的参数ObservableOnSubscribe<T>，可以理解为一个计划表，泛型T是要操作对象的类型，
                // 重写subscribe方法，里面写具体的计划，本文的例子就是推送连载1、连载2和连载3，
                // 在subscribe中的ObservableEmitter<String>对象的Emitter是发射器的意思。
                // ObservableEmitter有三种发射的方法，分别是void onNext(T value)、void onError(Throwable error)、void onComplete()，
                // onNext方法可以无限调用，Observer（观察者）所有的都能接收到，
                // onError和onComplete是互斥的，Observer（观察者）只能接收到一个，
                // OnComplete可以重复调用，但是Observer（观察者）只会接收一次，
                // 而onError不可以重复调用，第二次调用就会报异常。

                observableEmitter.onNext("连载1");
                observableEmitter.onNext("连载2");
                observableEmitter.onNext("连载3");
                observableEmitter.onComplete();
            }
        });

        // 创建读者  创建观察者
        Observer<String> reader = new Observer<String>() {

            //通过new创建接口，并实现其内部的方法，看方法其实就应该差不多知道干嘛的，
            // onNext、onError、onComplete都是跟被观察者发射的方法一一对应的，这里就相当于接收了。
            // onSubscribe（Disposable d）里面的Disposable对象要说一下，Disposable英文意思是可随意使用的，
            // 这里就相当于读者和连载小说的订阅关系，如果读者不想再订阅该小说了，可以调用 mDisposable.dispose()取消订阅，
            // 此时连载小说更新的时候就不会再推送给读者了。
            //
            @Override
            public void onSubscribe(Disposable disposable) {
                mDisposable = disposable;
                Log.e(TAG, "onSubscribe");
            }

            @Override
            public void onNext(String s) {
                if ("连载2".equals(s)) {
                    mDisposable.dispose();
                }
                Log.e(TAG, "onNext:" + s);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError:" + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete");
            }
        };

        //第三步：读者和连载小说建立订阅关系

        //小说被读者订阅了...
        navel.subscribe(reader);
    }

    private void swithThread() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> observableEmitter) throws Exception {
                TestLog.e("Observable thread is :" + Thread.currentThread().getName());
                observableEmitter.onNext(1);
                observableEmitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("After observerOn(mainThread),current thread is" + Thread.currentThread().getName());
                    }
                }).observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        TestLog.e("After observerOn(io),current thread is" + Thread.currentThread().getName());
                    }
                });
    }

    private void mapTagAndSwithThread() {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(ObservableEmitter<Response> observableEmitter) throws Exception {
                TestLog.e("请求前 ---->current thread is" + Thread.currentThread().getName());
                Request.Builder builder = new Request.Builder().url("http://api.avatardata.cn/MobilePlace/LookUp?key=ec47b85086be4dc8b5d941f5abd37a4e&mobileNumber=13021671512")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                observableEmitter.onNext(response);
            }
        }).map(new Function<Response, String>() {
            @Override
            public String apply(Response response) throws Exception {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        TestLog.e("map 转换前 ---->" + response.body() + "current thread is" + Thread.currentThread().getName());
                        return response.body() + "AAAAAAAAAAAAAAAAAAAA";
                    }
                }
                return null;
            }
        }).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        TestLog.e("map 转换后 ---->" + s + "current thread is" + Thread.currentThread().getName());
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        TestLog.e("成功---->" + s + "current thread is" + Thread.currentThread().getName());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        TestLog.e("失败 ---->" + throwable.getMessage() + "current thread is" + Thread.currentThread().getName());
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            Log.e(TAG, "目前的订阅状态---->" + mDisposable.isDisposed());
        }
    }
}


//





