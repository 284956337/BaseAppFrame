package com.plus.basekotlinappframe.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import com.plus.basekotlinappframe.R
import com.plus.basekotlinappframe.base.BaseActivity
import com.plus.basekotlinappframe.utils.PermissionUtil
import com.plus.zxing.ui.CaptureActivity

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_toolbar.*
import org.jetbrains.anko.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks{

    companion object {
        private val PERMISSION_CAMERA_MSG = "此app需要获取相机权限"
        private const val PERMISSION_CAMERA_CODE: Int = 10001

        private const val SCAN_REQUEST_CODE: Int = 1001
    }

    override fun attachLayoutResId(): Int = R.layout.activity_main

    override fun initData() {
        setSupportActionBar(toolbar)
    }

    override fun initView() {
        toolbar.run {
            title = ""
            setSupportActionBar(this)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        tv_title.run {
            text = "数据"
        }
    }

    override fun initListener() {
        btn_scan.setOnClickListener {
            applyPermission()
        }

    }

    override fun start() {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            android.R.id.home -> { //toolbar 返回键
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @AfterPermissionGranted(PERMISSION_CAMERA_CODE)
    fun applyPermission() {
        if (PermissionUtil.hasPermissions(this, *PermissionUtil.PERMISSIONS_CAMERA)){
            //有权限
            Intent(this@MainActivity, CaptureActivity::class.java).run {
                startActivityForResult(this, SCAN_REQUEST_CODE)
            }
        }else{
            //申请权限
            EasyPermissions.requestPermissions(this, PERMISSION_CAMERA_MSG, PERMISSION_CAMERA_CODE, *PermissionUtil.PERMISSIONS_CAMERA);
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    //成功
    override fun onPermissionsGranted(requestCode: Int, list: List<String>) {
        Intent(this@MainActivity, CaptureActivity::class.java).run {
            startActivityForResult(this, SCAN_REQUEST_CODE)
        }
    }

    //失败
    override fun onPermissionsDenied(requestCode: Int, list: List<String>) {
        toast("申请权限失败！")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(Activity.RESULT_OK == resultCode){
            if (SCAN_REQUEST_CODE == requestCode){
                val msg = data?.getStringExtra("codedContent") ?: ""
                tv_msg.run {
                    text = msg
                }
            }
        }
    }

}
