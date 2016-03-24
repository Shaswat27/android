#include <jni.h>
#include "opencv2/core/core.hpp"
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/features2d/features2d.hpp>
#include <stdio.h>
#include <iostream>
#include <com_example_shaswat_testopencv_MainActivity.h>

using namespace std;
using namespace cv;

int toGray(Mat& img, Mat& gray);

extern "C" {

JNIEXPORT jint JNICALL Java_com_example_shaswat_testopencv_MainActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba, jlong addrGray);

JNIEXPORT jint JNICALL Java_com_example_shaswat_testopencv_MainActivity_convertNativeGray(JNIEnv*, jobject, jlong addrRgba, jlong addrGray) {

    Mat& mRgb = *(Mat*)addrRgba;
    Mat& mGray = *(Mat*)addrGray;

    int conv;
    jint retVal;

    conv = toGray(mRgb, mGray);
    retVal = (jint)conv;

    return retVal;

}

}

int toGray(Mat &img, Mat& gray)
{
    cvtColor(img, gray, CV_RGBA2GRAY); // Assuming RGBA input

    std::vector <cv::KeyPoint> keypoints;

    FAST(gray, keypoints, 70);

    /// Display corners
    drawKeypoints( gray, keypoints, img, Scalar::all(-1), DrawMatchesFlags::DEFAULT );

    if (gray.rows == img.rows && gray.cols == img.cols)
    {
        return (1);
    }
    return(0);
}
