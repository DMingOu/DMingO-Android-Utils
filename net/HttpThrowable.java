package com.example.odm.testactivityjump.net;

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
