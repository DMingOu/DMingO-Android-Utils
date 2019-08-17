package com.example.odm.testactivityjump.net;

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
