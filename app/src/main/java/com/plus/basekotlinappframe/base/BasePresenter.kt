package com.plus.basekotlinappframe.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by zw on 2018/8/27.
 * 封装
 */
open class BasePresenter<V: IView> : IPresenter<V> {

    var mRootView: V? = null
        private set

    private var compositeDisposable = CompositeDisposable()

    override fun attachView(mRootView: V) {
        this.mRootView = mRootView
    }

    override fun detachView() {
        mRootView = null
        //保证activity结束时取消所有正在执行的订阅
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

    private val isViewAttached: Boolean
        get() = mRootView != null

    /**
     * 检查是否已经绑定了View
     */
    fun checkViewAttached() {
        if(!isViewAttached)
            throw MvpViewNotAttachedException()
    }

    /**
     * 添加订阅
     */
fun addSuvscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")
}