package com.plus.basekotlinappframe.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

/**
 * Created by zw on 2018/8/28.
 */
object ImageLoader {
    /**
     *
     */
    fun load(context: Context, url: String?, iv: ImageView?) {
        if(!SettingUtil.getIsNoPhotoMode()) {
            iv?.let {
                Glide.with(context)
                        .load(url)
                        .transition(DrawableTransitionOptions().crossFade())
                        .into(iv)
            }
        }
    }
}