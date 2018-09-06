package com.plus.basekotlinappframe.base

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import kotlin.properties.Delegates

/**
 * Created by zw on 2018/8/27.
 * 基类App，主模块需要继承此APP
 */
open class BaseApplication : Application() {

    companion object {
        private val TAG = "App"

        var context: Context by Delegates.notNull()
            private set

    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)

    }

    private val mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityPaused(p0: Activity?) {
        }

        override fun onActivityResumed(p0: Activity?) {
        }

        override fun onActivityStarted(p0: Activity?) {
        }

        override fun onActivityDestroyed(p0: Activity?) {
        }

        override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {
        }

        override fun onActivityStopped(p0: Activity?) {
        }

        override fun onActivityCreated(p0: Activity?, p1: Bundle?) {
        }

    }
}