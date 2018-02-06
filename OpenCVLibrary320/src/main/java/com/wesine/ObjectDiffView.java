package com.wesine;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.wesine.listener.OnCalcBackDifferListener;
import com.wesine.listener.OnObjectDifferListener;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by doug on 18-1-29.
 */

public class ObjectDiffView extends BaseCameraView implements OnCalcBackDifferListener {


    private static final String TAG = ObjectDiffView.class.getSimpleName();
    private static final Scalar TRACKING_RECT_COLOR = new Scalar(255, 255, 0, 255);

    double totalArea = 640 * 480;
    double minArea = totalArea * 0.01;
    double maxArea = totalArea * 0.99;


    private ObjectDiffer mObjectDiffer;
    private boolean isTracking = false;
    List<MatOfPoint> matOfPoints;


    public ObjectDiffView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    public void onOpenCVLoadSuccess() {
        Log.i(TAG, "onOpenCVLoadSuccess: ");
        // 目标追踪器
        mObjectDiffer = new ObjectDiffer();
        mObjectDiffer.setOnCalcBackDifferListener(this);
        matOfPoints = new ArrayList<>();
//        mObjectDiffer.creatDifferedObject();


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


        setCameraFrame1();
//        Mat mat = setCameraFrame();


        Log.i(TAG, "onCameraFrame: end");
        return mGray;
//        return mat;
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
//        if (matOfPoints.size() > 0) {
//            for (int i = 0; i < matOfPoints.size(); i++) {
//                MatOfPoint matOfPoint = matOfPoints.get(i);
//                if (matOfPoint != null) {
//                    double v = Imgproc.contourArea(matOfPoint);
//                    if (v < maxArea && v > minArea) {
//                        Rect rect = Imgproc.boundingRect(matOfPoint);
//                        Imgproc.rectangle(mGray, rect.tl(), rect.br(), TRACKING_RECT_COLOR, 3);
//                    }
//                }
//            }
//        }
//        if (mOnCalcBackDifferListener != null) {
//            mOnCalcBackDifferListener.onCalcBackProject(mGray);
//        }

    }


    private Mat setCameraFrame() {
        Mat mat = null;
        try {

            mat = mObjectDiffer.objectDifferMat(mRgba, mGray);
        } catch (Exception e) {
            e.printStackTrace();
            mat.release();
        }
        return mat;
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
