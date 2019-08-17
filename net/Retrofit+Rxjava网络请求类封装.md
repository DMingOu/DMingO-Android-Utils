## Retrofit + RxJava2 网络请求类封装

### 1、RetrofitManger.java

```
public class RetrofitManager {
    private static RetrofitManager retrofitManager;
    private Retrofit retrofit;
    private HttpService service;
    public static int DEFAULT_TIME_OUT = 8;
    /**
     * 超时时间，默认为8秒
     */
    private static int timeoutTime = SharedPreferencesManager.getManager().getInt(SharedPreferencesManager.CONST_TIME_OUT,DEFAULT_TIME_OUT);
    /**
     * 服务器ip地址
     */
    private static String baseUrl = Api.CONST_BASE_URL;
    public final static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private RetrofitManager(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                        cookieStore.put(httpUrl.host(),list);
                    }
                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                })
                .retryOnConnectionFailure(true)
                .connectTimeout(timeoutTime, TimeUnit.SECONDS)
                .writeTimeout(timeoutTime,TimeUnit.SECONDS)
                .readTimeout(timeoutTime,TimeUnit.SECONDS);

        //创建Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(builder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(HttpService.class);
    }

    /**
     * 获取网络管理的manager
     * @return 该单例类
     */
    public static RetrofitManager getInstance(){
        if(retrofitManager == null){
            synchronized (Object.class){
                if(retrofitManager == null){
                    retrofitManager = new RetrofitManager();
                }
            }
        }
        return retrofitManager;
    }

    /**
     * 获取访问http的service
     * @return HttpService
     */
    public HttpService getHttpService() {
        return service;
    }

    /**
     * 重新设置服务器和超时时间
     * @param timeout 超时时间
     * @param url 服务器地址
     */
    public static void setTimeoutAndUrl(int timeout,String url){
        timeoutTime = timeout;
        Log.d("RetorfitManager","" + timeoutTime);
        baseUrl = url;
        //重新生成service
        retrofitManager = new RetrofitManager();
    }

    public static int getTimeoutTime(){
        return timeoutTime;
    }
}
```



### 2、ObserverManger.java

使用Retrofit+RxJava发起请求后，如果请求失败，会回调observer中的onError方法，该方法的参数为Throwable，并没能反馈更直接清楚的异常信息给我们，所以有必要对Throwable异常进行处理转换.

```
import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Observer的继承类,优化了异常的处理
 * @author: ODM
 * @date: 2019/8/17
 */
public abstract class ObserverManager<T >  implements Observer <T>{

    private static final String TAG = "ObserverManager";
    @Override
    public void onSubscribe(Disposable d) {
        Log.d(TAG, "onSubscribe: ");
    }



    @Override
    public void onError(Throwable e) {
        if (e instanceof Exception) {
            onError(ThrowableHandler.handleThrowable(e));
        } else {
            onError(new HttpThrowable(HttpThrowable.UNKNOWN,"未知错误",e));
        }
    }

    @Override
    public void onComplete() {
    }


    public abstract void onError(HttpThrowable httpThrowable);

    @Override
    public abstract void onNext(T t);
}
```

#### 2.1 、 ThrowableHandler.java

```
import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

/**
 * 根据网络异常状态进行处理
 *
 * @author: ODM
 * @date: 2019/8/17
 */
public class ThrowableHandler {

    public static HttpThrowable handleThrowable(Throwable throwable) {
        if (throwable instanceof HttpException) {
            return new HttpThrowable(HttpThrowable.HTTP_ERROR, "网络(协议)异常", throwable);
        } else if (throwable instanceof JsonParseException || throwable instanceof JSONException || throwable instanceof ParseException) {
            return new HttpThrowable(HttpThrowable.PARSE_ERROR, "数据解析异常", throwable);
        } else if (throwable instanceof UnknownHostException) {
            return new HttpThrowable(HttpThrowable.NO_NET_ERROR, "网络连接失败，请检查您的网络，稍后重试", throwable);
        } else if (throwable instanceof SocketTimeoutException) {
            return new HttpThrowable(HttpThrowable.TIME_OUT_ERROR, "连接超时", throwable);
        } else if (throwable instanceof ConnectException) {
            return new HttpThrowable(HttpThrowable.CONNECT_ERROR, "连接异常", throwable);
        } else if (throwable instanceof javax.net.ssl.SSLHandshakeException) {
            return new HttpThrowable(HttpThrowable.SSL_ERROR, "证书验证失败", throwable);
        } else {
            return new HttpThrowable(HttpThrowable.UNKNOWN, throwable.getMessage(), throwable);
        }
    }
}
```



### 2.2 、HttpThrowable.java

```
/**
 * 网络异常状态类
 *
 * @author: ODM
 * @date: 2019/8/17
 */
public class HttpThrowable extends Exception {

    public int errorType;
    public String message;
    public Throwable throwable;

    /**
     * 未知错误
     */
     static final int UNKNOWN = 1000;
    /**
     * 解析错误
     */
     static final int PARSE_ERROR = 1001;
    /**
     * 连接错误
     */
     static final int CONNECT_ERROR = 1002;
    /**
     * DNS解析失败（无网络）
     */
     static final int NO_NET_ERROR = 1003;
    /**
     * 连接超时错误
     */
     static final int TIME_OUT_ERROR = 1004;
    /**
     * 网络（协议）错误
     */
     static final int HTTP_ERROR = 1005;
    /**
     * 证书错误
     */
     static final int SSL_ERROR = 1006;

     HttpThrowable(int errorType, String message, Throwable throwable) {
        super(throwable);
        this.errorType = errorType;
        this.message = message;
        this.throwable = throwable;
    }
}
```



### 3 、 RetryFunction.java

为网络连接加入重试机制

```
public  class RetryFunction implements Function<Observable<Throwable>, ObservableSource<?>> {

    //延迟重试的时间
    private int retryDelaySeconds;
    //记录当前重试次数
    private int retryCount;
    //最大重试次数
    private int retryCountMax;

    public RetryFunction(int retryDelaySeconds, int retryCountMax) {
        this.retryDelaySeconds = retryDelaySeconds;
        this.retryCountMax = retryCountMax;
    }

    @Override
    public ObservableSource<?> apply(Observable<Throwable> throwableObservable) throws Exception {

        //方案一：使用全局变量来控制重试次数，重试3次后不再重试，通过代码显式回调onError结束请求
        return throwableObservable.flatMap(new Function<Throwable, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Throwable throwable) throws Exception {
                //如果失败的原因是UnknownHostException（DNS解析失败，当前无网络），则没必要重试，直接回调error结束请求即可
                if (throwable instanceof UnknownHostException) {
                    return Observable.error(throwable);
                }

                //没超过最大重试次数的话则进行重试
                if (++retryCount <= retryCountMax) {
                    //延迟retryDelaySeconds后开始重试
                    return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
                }

                return Observable.error(throwable);
            }
        });


/*方案二：使用zip控制重试次数，重试3次后不再重试（会隐式回调onComplete结束请求，但我需要的是回调onError，所以采用了方案一）
            return Observable.zip(throwableObservable,Observable.range(1, retryCountMax),new BiFunction<Throwable, Integer, Throwable>() {
                @Override
                public Throwable apply(Throwable throwable, Integer integer) throws Exception {
                    LogUtil.e("ljy",""+integer);
                    return throwable;
                }
            }).flatMap(new Function<Throwable, ObservableSource<?>>() {
                @Override
                public ObservableSource<?> apply(Throwable throwable) throws Exception {
                    if (throwable instanceof UnknownHostException) {
                        return Observable.error(throwable);
                    }
                    return Observable.timer(retryDelaySeconds, TimeUnit.SECONDS);
                }
            });*/

    }
}
```



### 4  、 ApiService.java

请求的具体方法，配置网络请求的参数，请求头等等

```
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 存放Retrofit会调用的具体请求方法 接口
 * @author: ODM
 * @date: 2019/8/17
 */
public interface   ApiService {

    /*
     * @其他声明
     * @请求方式("请求地址")
     * Observable<请求返回的实体> 请求方法名(请求参数)；
     */

//     示例e
    String BASE_URL = "https://www.wanandroid.com/";
    @GET("banner/json")
    Observable<BannerData> getBannerData();

//   static String BASE_URL = "到时候你的服务器地址的 BaseUrl，记得尾巴加 /";

}
```





### 使用示例

```
  //在一个类里进行网络请求操作(例子为请求Banner数据，需要在ApiService写好getBannerData方法)
   public void getBannerData() {
        RetrofitManager.getInstance()
                .getApiService()
                .getBannerData()
                .retryWhen(new RetryFunction(3 , 3))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ObserverManager<BannerData>() {
                    @Override
                    public void onError(HttpThrowable httpThrowable) {
                        showText(httpThrowable.message);
                    }

                    @Override
                    public void onNext(BannerData bannerData) {
                        if (bannerData != null) {
                            showText(gson.toJson(bannerData));
                        }
                    }
                });
    }
```

#### MVP 模式下 

可以在 P层 调用 方法获取  在 P层创建的 Observerable 对象 ，即是在 M层定义方法

`Observerable<T> getXXXXX()`  ,

再让 P层  链式完成订阅操作

```
mModel.getXXXXXXX()
.subscribe(new ObserverManager<T>() {
                    @Override
                    public void onError(HttpThrowable httpThrowable) {
                    //可以打印具体异常信息查看
                        showText(httpThrowable.message);
                    }

                    @Override
                    public void onNext(T t) {
                        if (t != null) {
                          //操作
                        }
                    }
                });
```

