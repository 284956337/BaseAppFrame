package com.plus.basekotlinappframe.base

/**
 * Created by zw on 2018/8/27.
 * 封装所有界面都需要使用到的事件
 */
interface IView {

    fun showLoading()

    fun hideLoading()

    fun showError(errorMsg: String)
}