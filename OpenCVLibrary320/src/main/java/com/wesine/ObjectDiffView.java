package com.wesine;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.wesine.listener.OnCalcBackDifferListener;
import com.wesine.listener.OnObjectDifferListener;

import org.opencv.android.FpsMeter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by doug on 18-1-29.
 */

public class ObjectDiffView extends BaseCameraView implements OnCalcBackDifferListener {


    private static final String TAG = ObjectDiffView.class.getSimpleName();
    //    private static final Scalar TRACKING_RECT_COLOR = new Scalar(255, 255, 0, 255);
    private static final Scalar TRACKING_RECT_COLOR = new Scalar(0, 0, 255);

    double totalArea = 640 * 480;
    double minArea = totalArea * 0.01;
    double maxArea = totalArea * 0.99;


    private ObjectDiffer mObjectDiffer;
    //    private boolean isTracking = false;
    List<MatOfPoint> matOfPoints;


    public ObjectDiffView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public void onOpenCVLoadSuccess() {
        Log.i(TAG, "onOpenCVLoadSuccess: ");
        mObjectDiffer = new ObjectDiffer();
        mObjectDiffer.setOnCalcBackDifferListener(this);
        matOfPoints = new ArrayList<>();
    }

    @Override
    public void onOpenCVLoadFail() {
        Log.e(TAG, "onOpenCVLoadFail: ");
    }


    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Log.i(TAG, "onCameraFrame: start");
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();

        Imgproc.putText(mRgba, generateTimestamp(), new Point(mFrameWidth / 20, mFrameHeight / 20), Typeface.NORMAL, 0.5, TRACKING_RECT_COLOR);
//        setCameraFrame1();
        setCameraFrame();


        Log.i(TAG, "onCameraFrame: end");
        return mRgba;
//        return mat;
    }

    private static String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS", Locale.US);//yyyy_MM_dd_HH_mm_ss_SSS
        return sdf.format(new Date());
    }

    private void setCameraFrame1() {
        if (matOfPoints.size() > 0) {
            matOfPoints.clear();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                matOfPoints = mObjectDiffer.objectDiffer(mRgba, mGray);

            }
        }).start();
    }


    private void setCameraFrame() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mObjectDiffer.objectDifferMat(mRgba, mGray);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onCameraViewStopped() {
        super.onCameraViewStopped();
        mObjectDiffer.releaseMat();
    }

    private OnCalcBackDifferListener mOnCalcBackDifferListener;

    public void setOnCalcBackProjectListener(OnCalcBackDifferListener listener) {
        this.mOnCalcBackDifferListener = listener;
    }


    private OnObjectDifferListener mOnObjectDifferListener;

    public void setOnObjectDifferListener(OnObjectDifferListener listener) {
        mOnObjectDifferListener = listener;
    }


    @Override
    public void onCalcBackProject(Mat backProject) {
        if (mOnCalcBackDifferListener != null) {
            mOnCalcBackDifferListener.onCalcBackProject(backProject);
        }
    }
}
