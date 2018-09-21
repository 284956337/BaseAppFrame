package com.plus.basekotlinappframe.ext

import android.content.Context
import android.support.v4.app.Fragment
import android.widget.Toast
import org.jetbrains.anko.runOnUiThread

/**
 * Created by zw on 2018/9/6.
 */

fun Context.showToast(content: String) {
    runOnUiThread {
        Toast.makeText(this, content, Toast.LENGTH_SHORT)
    }
}

fun Fragment.showToast(content: String) {
    Toast.makeText(this.activity?.applicationContext, content, Toast.LENGTH_SHORT)
}