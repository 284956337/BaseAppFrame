package com.plus.basekotlinappframe.utils

import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by zw on 2018/9/6.
 * 动态权限申请相关
 */
object PermissionUtil {

    /**
     * 权限组
     */
    val PERMISSIONS_CAMERA: Array<String> = arrayOf(android.Manifest.permission.CAMERA)

    fun hasPermissions(context: Context, vararg permissions: String): Boolean{
        context.let {
            return EasyPermissions.hasPermissions(it, *permissions)
        }
    }
}