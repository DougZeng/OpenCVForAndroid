package com.wesine.util;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

/**
 * Created by doug on 18-2-2.
 */

public class SharpenUtil {
    private static final String TAG = SharpenUtil.class.getSimpleName();

    public static double saturate(double x) {
        return x > 255.0 ? 255.0 : (x < 0.0 ? 0.0 : x);
    }

    /**
     * @param myImage
     * @param result
     * @return
     */
    public static Mat sharpen(Mat myImage, Mat result) {
        myImage.convertTo(myImage, CvType.CV_8U);
        int nChannels = myImage.channels();
        result.create(myImage.size(), myImage.type());
        for (int j = 1; j < myImage.rows() - 1; ++j) {
            for (int i = 1; i < myImage.cols() - 1; ++i) {
                double sum[] = new double[nChannels];
                for (int k = 0; k < nChannels; ++k) {
                    double top = -myImage.get(j - 1, i)[k];
                    double bottom = -myImage.get(j + 1, i)[k];
                    double center = (5 * myImage.get(j, i)[k]);
                    double left = -myImage.get(j, i - 1)[k];
                    double right = -myImage.get(j, i + 1)[k];
                    sum[k] = saturate(top + bottom + center + left + right);
                }
                result.put(j, i, sum);
            }
        }
        result.row(0).setTo(new Scalar(0));
        result.row(result.rows() - 1).setTo(new Scalar(0));
        result.col(0).setTo(new Scalar(0));
        result.col(result.cols() - 1).setTo(new Scalar(0));
        return result;
    }
}
