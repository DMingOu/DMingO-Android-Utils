package com.example.odm.testactivityjump.net;

import com.example.odm.testactivityjump.BannerData;

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

////     示例
//    String BASE_URL = "https://www.wanandroid.com/";
//    @GET("banner/json")
//    Observable<BannerData> getBannerData();

   static String BASE_URL = "到时候你的服务器地址的 BaseUrl，记得尾巴加 /";

}
