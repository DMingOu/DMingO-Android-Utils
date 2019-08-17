package com.example.odm.testactivityjump.net;

import android.util.Log;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Observer的继承类,优化了对各类网络异常的处理
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

    /**
     * 强制实现，异常的处理
     * @param httpThrowable 网络异常
     */
    public abstract void onError(HttpThrowable httpThrowable);

    /**
     * 强制实现，对回调数据的处理
     */
    @Override
    public abstract void onNext(T t);
}


