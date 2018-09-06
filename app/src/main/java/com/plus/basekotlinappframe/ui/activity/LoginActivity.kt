package com.plus.basekotlinappframe.ui.activity

import android.content.Intent
import com.plus.basekotlinappframe.R
import com.plus.basekotlinappframe.base.BaseActivity
import com.plus.basekotlinappframe.mvp.contract.LoginContract
import com.plus.basekotlinappframe.mvp.presenter.LoginPresenter
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by zw on 2018/9/4.
 */
class LoginActivity : BaseActivity(), LoginContract.View {

    private val loginPresenter: LoginPresenter by lazy {
        LoginPresenter()
    }

    override fun attachLayoutResId(): Int = R.layout.activity_login

    override fun initData() {
    }

    override fun initView() {
        btn_login.setOnClickListener {
            Intent(this@LoginActivity, MainActivity::class.java).run {
                startActivity(this)
            }
        }

    }

    override fun initListener() {
    }

    override fun start() {
    }

    override fun showLoading() {

    }

    override fun hideLoading() {

    }

    override fun showError(errorMsg: String) {

    }



}
