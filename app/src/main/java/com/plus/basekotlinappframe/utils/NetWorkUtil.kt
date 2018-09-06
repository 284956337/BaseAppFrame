package com.plus.basekotlinappframe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager

/**
 * Created by zw on 2018/8/27.
 */
class NetWorkUtil {
    companion object {

        /**
         * 检车网络是否存在
         */
        @JvmStatic
        fun isNetWorkAvailable(context: Context): Boolean {
            val manager = context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return !(null == info || !info.isAvailable)
        }

        /**
         * 判断MOBILE网络是否可用
         */
        fun isMobile(context: Context?): Boolean {
            if (context != null) {
                //获取手机所有连接管理对象(包括对wi-fi,net等连接的管理)
                val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                //获取NetworkInfo对象
                val networkInfo = manager.activeNetworkInfo
                //判断NetworkInfo对象是否为空 并且类型是否为MOBILE
                if (null != networkInfo && networkInfo.type == ConnectivityManager.TYPE_MOBILE)
                    return networkInfo.isAvailable
            }
            return false
        }
    }
}