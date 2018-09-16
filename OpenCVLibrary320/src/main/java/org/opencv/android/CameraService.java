package org.opencv.android;

import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import java.util.List;

/**
 * Created by doug on 18-2-10.
 */

public class CameraService implements Camera.PreviewCallback {

    private Camera.PreviewCallback callback;
    private SurfaceView surfaceView;
    private Context con;

    private Camera mCamera;
    private static final String TAG = CameraService.class.getSimpleName();
    private byte mBuffer[];
    protected int mFrameWidth;
    protected int mFrameHeight;

    protected int mCameraIndex = CAMERA_ID_ANY;

    public static final int CAMERA_ID_ANY   = -1;
    public static final int CAMERA_ID_BACK  = 99;
    public static final int CAMERA_ID_FRONT = 98;

    public CameraService(Camera.PreviewCallback callback,SurfaceView surfaceView,Context con) {
        this.callback = callback;
        this.surfaceView = surfaceView;
        this.con = con;
    }

    public boolean start(){
        if (!initializeCamera())
            return false;
        return true;
    }

    public void stop(){
        synchronized (this) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);

                mCamera.release();
            }
            mCamera = null;
        }
    }

    protected boolean initializeCamera() {
        Log.d(TAG, "Initialize java camera");
        boolean result = true;
        synchronized (this) {
            mCamera = null;

            if (mCameraIndex == CAMERA_ID_ANY) {
                Log.d(TAG, "Trying to open camera with old open()");
                try {
                    mCamera = Camera.open();
                } catch (Exception e) {
                    Log.e(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
                }

                if (mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    boolean connected = false;
                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")");
                        try {
                            mCamera = Camera.open(camIdx);
                            connected = true;
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage());
                        }
                        if (connected) break;
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    int localCameraIndex = mCameraIndex;
                    if (mCameraIndex == CAMERA_ID_BACK) {
                        Log.i(TAG, "Trying to open back camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    } else if (mCameraIndex == CAMERA_ID_FRONT) {
                        Log.i(TAG, "Trying to open front camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    }
                    if (localCameraIndex == CAMERA_ID_BACK) {
                        Log.e(TAG, "Back camera not found!");
                    } else if (localCameraIndex == CAMERA_ID_FRONT) {
                        Log.e(TAG, "Front camera not found!");
                    } else {
                        Log.d(TAG, "Trying to open camera with new open(" + Integer.valueOf(localCameraIndex) + ")");
                        try {
                            mCamera = Camera.open(localCameraIndex);
                        } catch (RuntimeException e) {
                            Log.e(TAG, "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage());
                        }
                    }
                }
            }

            if (mCamera == null)
                return false;

            /* Now set camera parameters */
            try {
                Camera.Parameters params = mCamera.getParameters();
                Log.d(TAG, "getSupportedPreviewSizes()");
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();

                if (sizes != null) {
                    /* Select the size that fits surface considering maximum size allowed */
//                    Size frameSize = calculateCameraFrameSize(sizes, new JavaCameraView.JavaCameraSizeAccessor(), width, height);

                    params.setPreviewFormat(ImageFormat.NV21);
//                    Log.d(TAG, "Set preview size to " + Integer.valueOf((int) frameSize.width) + "x" + Integer.valueOf((int) frameSize.height));
//                    params.setPreviewSize((int) frameSize.width, (int) frameSize.height);
                    //set the preview size
                    params.setPreviewSize(320, 240);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && !android.os.Build.MODEL.equals("GT-I9100"))
                        params.setRecordingHint(true);

                    List<String> FocusModes = params.getSupportedFocusModes();
                    if (FocusModes != null && FocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    }

                    mCamera.setParameters(params);

                    params = mCamera.getParameters();
                    params.setPreviewFrameRate(30);
                    int previewFrameRate = params.getPreviewFrameRate();

                    Log.i(TAG, "initializeCamera: previewFrameRate = " + previewFrameRate);


                    mFrameWidth = params.getPreviewSize().width;
                    mFrameHeight = params.getPreviewSize().height;
//
//                    if ((getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT) && (getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT))
//                        mScale = Math.min(((float) height) / mFrameHeight, ((float) width) / mFrameWidth);
//                    else
//                        mScale = 0;
//
//                    if (mFpsMeter != null) {
//                        mFpsMeter.setResolution(mFrameWidth, mFrameHeight);
//                    }
//
                    int size = mFrameWidth * mFrameHeight;
                    size = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                    mBuffer = new byte[size];

//                    mCamera.addCallbackBuffer(mBuffer);
//                    mCamera.setPreviewCallbackWithBuffer(this.callback);
                    mCamera.setPreviewCallback(this.callback);

//                    mFrameChain = new Mat[2];
//                    mFrameChain[0] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);
//                    mFrameChain[1] = new Mat(mFrameHeight + (mFrameHeight / 2), mFrameWidth, CvType.CV_8UC1);
//
//                    AllocateCache();
//
//                    mCameraFrame = new JavaCameraView.JavaCameraFrame[2];
//                    mCameraFrame[0] = new JavaCameraView.JavaCameraFrame(mFrameChain[0], mFrameWidth, mFrameHeight);
//                    mCameraFrame[1] = new JavaCameraView.JavaCameraFrame(mFrameChain[1], mFrameWidth, mFrameHeight);

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                        mSurfaceTexture = new SurfaceTexture(MAGIC_TEXTURE_ID);
//                        mCamera.setPreviewTexture(mSurfaceTexture);
//                    } else
                    SurfaceView sur = new SurfaceView(con);
                        mCamera.setPreviewDisplay(new SurfaceView(con).getHolder());
//                        mCamera.setPreviewDisplay(surfaceView.getHolder());

                    /* Finally we are ready to start the preview */
                    Log.d(TAG, "startPreview");
                    mCamera.startPreview();
                } else
                    result = false;
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
//
    }
}
