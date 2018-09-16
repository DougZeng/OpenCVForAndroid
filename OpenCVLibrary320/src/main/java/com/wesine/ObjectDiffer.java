package com.wesine;

import android.util.Log;

import com.wesine.listener.OnCalcBackDifferListener;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractorKNN;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by doug on 18-1-30.
 */

public class ObjectDiffer {
    private static final String TAG = ObjectDiffer.class.getSimpleName();
    private BackgroundSubtractorKNN knn;
    private OnCalcBackDifferListener mOnCalcBackDifferListener;
    private static final Scalar TRACKING_RECT_COLOR = new Scalar(255, 255, 0, 255);

    double totalArea = 640 * 480;
    double minArea = totalArea * 0.01;
    double maxArea = totalArea * 0.99;
    private Mat fgmask;
    Mat newMat;

    public ObjectDiffer() {
        Size size = new Size(960, 1280);
        fgmask = new Mat(size, CvType.CV_8UC1);
        newMat = new Mat();
        knn = Video.createBackgroundSubtractorKNN(200, 600.0, true);

    }


    public void setOnCalcBackDifferListener(OnCalcBackDifferListener listener) {
        mOnCalcBackDifferListener = listener;
    }


    /**
     * 背景分离
     *
     * @param mRgba
     * @param mGray
     * @return
     */
    public List<MatOfPoint> objectDiffer(Mat mRgba, Mat mGray) {
        List<MatOfPoint> matOfPoints = new ArrayList<MatOfPoint>();

        try {
            Log.i(TAG, "objectDiffer: start");
            Imgproc.resize(mRgba, newMat, new Size(640, 480));
            Imgproc.GaussianBlur(newMat, newMat, new Size(11, 11), 0);
            Mat kernel_ellipse = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
//            Imgproc.erode(newMat, newMat, kernel_ellipse);
//            Imgproc.erode(newMat, newMat, kernel_ellipse);
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
            knn.apply(newMat, fgmask);

            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);
            Imgproc.erode(fgmask, fgmask, kernel_ellipse);
            Imgproc.erode(fgmask, fgmask, kernel_ellipse);
            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);
            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);
            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);

//            double threshold = Imgproc.threshold(mRgba, newMat, 160, 255, Imgproc.THRESH_BINARY);
//            Log.i(TAG, "objectDiffer: threshold = " + threshold);
            if (matOfPoints.size() > 0) {
                matOfPoints.clear();
            }
            Imgproc.findContours(fgmask, matOfPoints, new Mat(1280, 960, CvType.CV_32SC1), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            if (matOfPoints.size() > 0) {
                for (int i = 0; i < matOfPoints.size(); i++) {
                    MatOfPoint matOfPoint = matOfPoints.get(i);
                    if (matOfPoint != null) {
                        double v = Imgproc.contourArea(matOfPoint);
                        if (v < maxArea && v > minArea) {
                            Rect rect = Imgproc.boundingRect(matOfPoint);
                            Imgproc.rectangle(fgmask, rect.tl(), rect.br(), TRACKING_RECT_COLOR, 3);
                        }
                    }

                }
            }


            Log.i(TAG, "objectDiffer: end");


            if (mOnCalcBackDifferListener != null) {
                mOnCalcBackDifferListener.onCalcBackProject(fgmask);
            }

        } catch (Exception e) {
            e.printStackTrace();
            matOfPoints.clear();
            matOfPoints = null;
        }

        return matOfPoints;
    }

    /**
     * 背景分离
     *
     * @param mRgba
     * @param mGray
     * @return
     */
    public Mat objectDifferMat(Mat mRgba, Mat mGray) {

        List<MatOfPoint> matOfPoints = new ArrayList<MatOfPoint>();
        try {
            Log.i(TAG, "objectDiffer: start");
            Imgproc.resize(mGray, newMat, new Size(640, 480));
            Imgproc.GaussianBlur(newMat, newMat, new Size(11, 11), 0);
            Mat kernel_ellipse = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
//            Imgproc.erode(newMat, newMat, kernel_ellipse);
//            Imgproc.erode(newMat, newMat, kernel_ellipse);
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
//            Imgproc.dilate(newMat, newMat, kernel_ellipse);
            knn.apply(newMat, fgmask);

            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);
            Imgproc.erode(fgmask, fgmask, kernel_ellipse);
            Imgproc.erode(fgmask, fgmask, kernel_ellipse);
            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);
            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);
            //Imgproc.dilate(fgmask, fgmask, kernel_ellipse);

//            double threshold = Imgproc.threshold(mRgba, newMat, 160, 255, Imgproc.THRESH_BINARY);
//            Log.i(TAG, "objectDiffer: threshold = " + threshold);
            if (matOfPoints.size() > 0) {
                matOfPoints.clear();
            }
            //CvType.CV_32SC1
            Imgproc.findContours(fgmask, matOfPoints, new Mat(mGray.rows(), mGray.cols(), CvType.CV_32SC1), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            if (matOfPoints.size() > 0) {
                for (int i = 0; i < matOfPoints.size(); i++) {
                    MatOfPoint matOfPoint = matOfPoints.get(i);
                    if (matOfPoint != null) {
                        double v = Imgproc.contourArea(matOfPoint);
                        if (v < maxArea && v > minArea) {
                            Rect rect = Imgproc.boundingRect(matOfPoint);
                            Imgproc.rectangle(newMat, rect.tl(), rect.br(), TRACKING_RECT_COLOR, 3);
                        }
                    }

                }
            }
            Log.i(TAG, "objectDiffer: end");
        } catch (Exception e) {
            e.printStackTrace();
            matOfPoints.clear();
        }
        if (mOnCalcBackDifferListener != null) {
            if (newMat != null) {
                mOnCalcBackDifferListener.onCalcBackProject(newMat);
            }
        }
        return fgmask;
    }

    protected void releaseMat() {
        fgmask.release();
        newMat.release();
    }


}
