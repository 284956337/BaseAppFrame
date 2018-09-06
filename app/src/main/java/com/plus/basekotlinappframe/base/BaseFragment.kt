package com.plus.basekotlinappframe.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by zw on 2018/8/27.
 */
abstract class BaseFragment : Fragment() {

    /**
     * 视图是否加载完成
     */
    private var isViewPrepare = false
    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false
    /**
     * 加载布局
     */
    @LayoutRes
    abstract fun attachLayoutResId(): Int
    /**
     * 初始化 View
     */
    abstract fun initView()
    /**
     * 懒加载
     */
    abstract fun lazyLoad()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(attachLayoutResId(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewPrepare = true
        initView()
        lazyLoadDataIfPrepared()
    }

    private fun lazyLoadDataIfPrepared() {
        if (userVisibleHint && isViewPrepare && !hasLoadData){
            lazyLoad()
            hasLoadData = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}