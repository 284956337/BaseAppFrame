package com.plus.basekotlinappframe.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import com.plus.basekotlinappframe.constant.Constant
import com.plus.basekotlinappframe.utils.Preference

/**
 * Created by zw on 2018/8/27.
 * 基类Activity
 */
abstract class BaseActivity : AppCompatActivity() {

    /**
     * check login
     */
    protected var isLogin: Boolean by Preference(Constant.LOGIN_KEY, false)

    /**
     * 布局id
     */
    @LayoutRes
    protected abstract fun attachLayoutResId(): Int
    /**
     * 初始化View
     */
    abstract fun initView()
    /**
     * 初始化数据
     */
    abstract fun initData()
    /**
     * 初始化监听
     */
    abstract fun initListener()
    /**
     * 开始请求
     */
    abstract fun start()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutResId())
        initData()
        initView()
        initListener()
        start()
    }

    override fun onResume() {
        super.onResume()

    }
}