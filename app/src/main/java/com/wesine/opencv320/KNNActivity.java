package com.wesine.opencv320;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.wesine.ObjectDiffView;
import com.wesine.listener.OnCalcBackDifferListener;
import com.wesine.listener.OnOpenCVLoadListener;

import org.opencv.android.Utils;
import org.opencv.core.Mat;


/**
 * Created by doug on 18-1-28.
 * BackgroundSubtractorKNN
 */

public class KNNActivity extends BaseActivity {

    private static final String TAG = KNNActivity.class.getSimpleName();
    private ObjectDiffView diffView;
    private ImageView im;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_knn);
        diffView = (ObjectDiffView) findViewById(R.id.diffView);
        im = (ImageView) findViewById(R.id.imageView);
        diffView.setOnOpenCVLoadListener(new OnOpenCVLoadListener() {
            @Override
            public void onOpenCVLoadSuccess() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOpenCVLoadFail() {
                Toast.makeText(getApplicationContext(), "OpenCV 加载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNotInstallOpenCVManager() {
                showInstallDialog();
            }
        });


        /**
         * 另一窗口显示处理视频
         */
        diffView.setOnCalcBackProjectListener(new OnCalcBackDifferListener() {
            @Override
            public void onCalcBackProject(final Mat backProject) {
                if (backProject == null) {
                    return;
                }
                KNNActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (null == bitmap) {
                                bitmap = Bitmap.createBitmap(backProject.width(), backProject.height(), Bitmap.Config.ARGB_8888);
                            }

                            Utils.matToBitmap(backProject, bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                            bitmap = null;
                            backProject.release();
                        }
                        if (bitmap == null) {
                            return;
                        }
                        im.setImageBitmap(bitmap);

                    }
                });
            }
        });


        diffView.loadOpenCV(getApplicationContext());
    }


    public void swapCamera(View view) {
        diffView.swapCamera();
    }

}