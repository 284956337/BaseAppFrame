package com.plus.zxing.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.plus.zxing.R;
import com.plus.zxing.camera.CameraManager;
import com.plus.zxing.decoding.CaptureActivityHandler;
import com.plus.zxing.decoding.FinishListener;
import com.plus.zxing.decoding.InactivityTimer;
import com.plus.zxing.view.ViewfinderView;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 类描述:二维码扫码注册
 * 作者:${LiangAn}
 * 时间:2017/11/3 20:39
 * 备注:
 */
public class CaptureActivity extends AppCompatActivity implements Callback{
    Integer transTime = 15;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    public InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    // public static int CAMERA_FACING_BACK = 0;// 后摄像头
    // public static int CAMERA_FACING_FRONT = 1;// 前摄像头
    public int CAMERA_WHAT = 0;// 调用哪个
    public static final int OPEN_PHOTO = 10086;// 打开相册
    public static final int CROPIMAGES = 4;
    String photo_path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        initView();
    }

    public void initView() {
        CameraManager.init(getApplication());
        CAMERA_WHAT = 0;// 0后置,1前置
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        String fromWhere = getIntent().getStringExtra("fromWhere");
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /**
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            Intent intent = getIntent();
            intent.putExtra("codedContent", result.getText());
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "扫描失败！", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder, CAMERA_WHAT);
        } catch (IOException ioe) {
            displayFrameworkBugMessageAndExit();
            return;
        } catch (RuntimeException e) {
            displayFrameworkBugMessageAndExit();
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        // initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    public void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 20){
            switch(requestCode) {
                case CROPIMAGES:
                    try {
                        if (data != null) {
                            photo_path = data.getStringExtra("path");
                            new Thread(new Runnable() {

                                @Override
                                public void run() {

                                    Result result = scanningImage(photo_path);
                                    // String result = decode(photo_path);
                                    if (result == null) {
                                        Toast.makeText(CaptureActivity.this, "图片无法识别~！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Log.i("123result", result.getText());
                                        // 数据返回
                                        //showToast("识别结果="+result.getText());
                                        Intent intent = getIntent();
                                        intent.putExtra("codedContent", result.getText());
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }
                            }).start();
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "图片识别有误~！", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }

    }

    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);

        if (TextUtils.isEmpty(path)) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码

        options.inJustDecodeBounds = false; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);

        int[] lPixels = new int[scanBitmap.getWidth() * scanBitmap.getHeight()];
        scanBitmap.getPixels(lPixels, 0, scanBitmap.getWidth(), 0, 0, scanBitmap.getWidth(), scanBitmap.getHeight());
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap.getWidth(), scanBitmap.getHeight(), lPixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        try {
            return new MultiFormatReader().decode(bitmap1, hints);
        } catch (NotFoundException e) {
            return  null;
        }

    }

}
