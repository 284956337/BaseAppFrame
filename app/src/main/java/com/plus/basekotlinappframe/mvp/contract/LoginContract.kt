package com.plus.basekotlinappframe.mvp.contract

import com.plus.basekotlinappframe.base.IPresenter
import com.plus.basekotlinappframe.base.IView

/**
 * Created by zw on 2018/9/6.
 */
interface LoginContract {

    interface View : IView {

    }

    interface Presenter : IPresenter<View> {

    }
}