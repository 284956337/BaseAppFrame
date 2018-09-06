package com.plus.basekotlinappframe.base

/**
 * Created by zw on 2018/8/27.
 * 公共事件 绑定View和解绑View
 */
interface IPresenter<in V : IView> {

    fun attachView(mRootView: V)

    fun detachView()
}