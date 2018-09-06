package com.plus.basekotlinappframe.mvp.presenter

import com.plus.basekotlinappframe.base.BasePresenter
import com.plus.basekotlinappframe.mvp.contract.LoginContract
import com.plus.basekotlinappframe.mvp.model.LoginModel

/**
 * Created by zw on 2018/9/6.
 */
class LoginPresenter : BasePresenter<LoginContract.View>(), LoginContract.Presenter {

    private val loginModel: LoginModel by lazy {
        LoginModel()
    }
}