#include <jni.h>
#include "opencv2/core/core.hpp"
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <opencv2/video/tracking.hpp>
#include <stdio.h>
#include <iostream>
#include <com_example_shaswat_testopencv_MainActivity.h>

using namespace std;
using namespace cv;

int drawTracks(Mat curr, Mat prev, Mat& display);

extern "C" {

JNIEXPORT jint JNICALL Java_com_example_shaswat_testopencv_MainActivity_convertNativeGray(JNIEnv*, jobject, jlong addrCurr, jlong addrPrev, jlong addrDisplay);

JNIEXPORT jint JNICALL Java_com_example_shaswat_testopencv_MainActivity_convertNativeGray(JNIEnv*, jobject, jlong addrCurr, jlong addrPrev, jlong addrDisplay) {

    Mat& mCurr = *(Mat*)addrCurr;
    Mat& mPrev = *(Mat*)addrPrev;
    Mat& mDisplay = *(Mat*)addrDisplay;

    int conv;
    jint retVal;

    conv = drawTracks(mCurr, mPrev, mDisplay);
    retVal = (jint)conv;

    return retVal;

}

}

int drawTracks(Mat curr, Mat prev, Mat& display)
{
    Mat gr_prev, gr_next;

    /// Assuming RGBA input
    cvtColor(curr, gr_next, CV_RGBA2GRAY);
    cvtColor(prev, gr_prev, CV_RGBA2GRAY);

    std::vector <cv::KeyPoint> keypoints_prev;

    /// Detect features
    FAST(gr_prev, keypoints_prev, 50);

    ///  Apply KLT Tracker
    if(keypoints_prev.size()>0) {
        vector <uchar> status;
        vector <float> err;
        std::vector <cv::Point2f> _prev, temp, next;
        KeyPoint key;
        key.convert(keypoints_prev, _prev);

        if(_prev.size()>75)
        {
            std::vector<cv::Point2f>::const_iterator first = _prev.begin();
            std::vector<cv::Point2f>::const_iterator last = _prev.begin() + 75;
            std::vector<cv::Point2f> newVec(first, last);
            _prev = newVec;
        }

        std::vector<cv::Mat> pyr_prev, pyr_next;
        buildOpticalFlowPyramid(gr_prev, pyr_prev, Size(21,21), 3);
        buildOpticalFlowPyramid(gr_next, pyr_next, Size(21,21), 3);
        calcOpticalFlowPyrLK(pyr_prev, pyr_next, _prev, temp, status, err);

        ///display
        Mat disp;
        cvtColor(prev, disp, CV_RGBA2BGR);
        int i, k;

        for( int i = k = 0; i < temp.size(); i++ )
        {
            /// Status = 0 => feature not found
            if(status[i] == 0) {
                continue;
            }
            else {
                cv::line(disp,_prev[i],temp[i],cv::Scalar(255, 0, 0));
            }
        }

        cvtColor(disp, display, CV_BGR2RGBA);
    }
    else
    {
        Mat p,t;
        cvtColor(prev, p, CV_RGBA2BGR);
        drawKeypoints( p, keypoints_prev, t, Scalar::all(-1), DrawMatchesFlags::DEFAULT );

        cvtColor(t, display, CV_BGR2RGBA);
    }

    return 0;
}
