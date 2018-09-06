package com.plus.basekotlinappframe.http

/**
 * Created by zw on 2018/8/27.
 * 自定义异常
 */
class ApiException : RuntimeException {

    private var code: Int? = null

    constructor(throwable: Throwable, code: Int) : super(throwable) {
        this.code = code
    }

    constructor(message: String) : super(Throwable(message))
}