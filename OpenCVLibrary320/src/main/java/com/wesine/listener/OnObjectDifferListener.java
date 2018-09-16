package com.wesine.listener;

import org.opencv.core.Point;

import java.util.List;

/**
 * Created by doug on 18-2-1.
 */

public interface OnObjectDifferListener {
    void onObjectLocation(List<Point> centers);

    void onObjectLost();
}
