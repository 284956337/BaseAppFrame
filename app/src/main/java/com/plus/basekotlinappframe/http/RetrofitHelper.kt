package com.plus.basekotlinappframe.http

import com.plus.basekotlinappframe.BuildConfig
import com.plus.basekotlinappframe.base.BaseApplication
import com.plus.basekotlinappframe.constant.Constant
import com.plus.basekotlinappframe.constant.HttpConstant
import com.plus.basekotlinappframe.utils.NetWorkUtil
import com.plus.basekotlinappframe.utils.Preference
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by zw on 2018/8/27.
 * Retrofit网络请求封装操作
 */
object RetrofitHelper {

    private var retrofit: Retrofit? = null

    /**
     * token
     */
    private var token: String by Preference("token", "")

    private fun getRetrofit(): Retrofit? {
        if(retrofit == null) {
            synchronized(RetrofitHelper::class.java) {
                if(retrofit == null) {
                    retrofit = Retrofit.Builder()
                            .baseUrl(Constant.BASE_URL)
                            .client(getOkHttpClient())
                            .addConverterFactory(MoshiConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .build()
                }
            }
        }
        return retrofit
    }

    /**
     * 获取 OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient? {
        val builder = OkHttpClient().newBuilder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        if(BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }else {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        //设置请求缓存的大小跟位置
        val cacheFile = File(BaseApplication.context.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50) //50MB 缓存的大小

        builder.run {
//            addInterceptor(addQueryParameterInterceptor()) // 参数添加
//            addInterceptor(addHeaderInterceptor()) // token 过滤
            addInterceptor(httpLoggingInterceptor)
            addInterceptor(addHttpInterceptor())
            addInterceptor(addCacheInterceptor())
            addInterceptor({
                val request = it.request()
                val response = it.proceed(request)
                val requestUrl = request.url().toString()
                val domain = request.url().host()

                //set-cookie maybe has multi, login to save cookie
                if ((requestUrl.contains(HttpConstant.SAVE_USER_LOGIN_KEY)
                        || requestUrl.contains(HttpConstant.SAVE_USER_REGISTER_KEY))
                        && !response.headers(HttpConstant.SET_COOKIE_KEY).isEmpty()) {
                    val cookies = response.headers(HttpConstant.SET_COOKIE_KEY)
                    val cookie = HttpConstant.encodeCookie(cookies)
                    saveCookie(requestUrl, domain, cookie)
                }
                response
            })
            cache(cache)
            connectTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true) // 错误重连
//            cookieJar(CookieManager())
        }
        return builder.build()
    }

    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetWorkUtil.isNetWorkAvailable(BaseApplication.context)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (NetWorkUtil.isNetWorkAvailable(BaseApplication.context)) {
                val maxAge = 0
                //有网络时，设置缓存超时时间0个小时，意思就是不读取缓存数据，只对get有用，post没有缓冲

                response.newBuilder()
                        .header("Cache-Control", "public, max-age" + maxAge)
                        //清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .removeHeader("Retrofit")
                        .build()
            }else {
                //无网络时，设置超时为4周，只对get有用，post没有缓冲
                val maxStele = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStele)
                        .removeHeader("nyn")
                        .build()
            }
            response
        }
    }

    private fun addHttpInterceptor(): Interceptor {
        return Interceptor { chain ->
            val builder = chain.request().newBuilder()
            val request = builder.addHeader("Content-Type", "application/json; charset=utf-8").build()
            chain.proceed(request)
        }
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifieUrl = originalRequest.url().newBuilder()
                    .addQueryParameter("", "")
                    .addQueryParameter("", "")
                    .build()
            request = originalRequest.newBuilder().url(modifieUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                    .header("token", token)
                    .method(originalRequest.method(), originalRequest.body())
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    private fun saveCookie(requestUrl: String, domain: String?, cookies: String) {
        requestUrl ?: return
        var spUrl: String by Preference(requestUrl, cookies)
        @Suppress("UNUSED_VALUE")
        spUrl = cookies
        domain ?: return
        var spDomain: String by Preference(domain, cookies)
        @Suppress("UNUSED_VALUE")
        spDomain = cookies
    }
}