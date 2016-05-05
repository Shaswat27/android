#include "vo_features.h"
#include <com_example_shaswat_mobilevo_LibVisodo.h>
#include <stdio.h>
#include <android/log.h>
using namespace cv;
using namespace std;

#define MAX_FRAME 20000
#define MIN_NUM_FEAT 2000
#define LOG_TAG "Visodo"
#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__))
Mat R_f, t_f;
double focal = 730.8560;
cv::Point2d pp(435.3405, 245.2157);
Mat E, R, t, mask;
Mat prevImage;
Mat currImage;
vector<Point2f> prevFeatures;

extern "C"
{
	JNIEXPORT jstring JNICALL Java_com_example_shaswat_mobilevo_LibVisodo_init(JNIEnv* jenv, jobject, jlong firstPic, jlong secondPic) {
	Mat img_1, img_2;
	ofstream myfile;
	LOGD("Opening Text File");
	myfile.open("results1_1.txt");
	LOGD("Text File Opened");
	char filename1[200];
	char filename2[200];
	sprintf(filename1, "/storage/emulated/0/image_2/%06d.png", 0);
	sprintf(filename2, "/storage/emulated/0/image_2/%06d.png", 1);

//	//read the first two frames from the dataset
//	Mat img_1_c = imread(filename1);
//	Mat img_2_c = imread(filename2);

	Mat& img_1_c = *(Mat*) firstPic;
	Mat& img_2_c = *(Mat*) secondPic;

	if (!img_1_c.data || !img_2_c.data) {
		//	    std::cout<< " --(!) Error reading images " << std::endl;           //�޸�
		LOGD("Error reading images");
		return jenv->NewStringUTF("Error reading images");
	}
	// we work with grayscale images
	cvtColor(img_1_c, img_1, COLOR_BGR2GRAY);
	cvtColor(img_2_c, img_2, COLOR_BGR2GRAY);

	// feature detection, tracking
	vector<Point2f> points1, points2; //vectors to store the coordinates of the feature points
	featureDetection(img_1, points1); //detect features in img_1
	vector<uchar> status;
	featureTracking(img_1, img_2, points1, points2, status); //track those features to img_2

	E = findEssentialMat(points2, points1, focal, pp, RANSAC, 0.999, 3.0, mask);
	recoverPose(E, points2, points1, R, t, focal, pp, mask);

	prevImage = img_2;
	prevFeatures = points2;

	R_f = R.clone();
	t_f = t.clone();

	clock_t begin = clock();

	return jenv->NewStringUTF("Init success");
	}

// IMP: Change the file directories (4 places) according to where your dataset is saved before running!
	JNIEXPORT jstring JNICALL Java_com_example_shaswat_mobilevo_LibVisodo_start(JNIEnv* jenv, jobject, jlong addrRgba, jlong afterPic, jint i) {
	Mat& traj = *(Mat*) addrRgba;

	double scale = 0.50;

	char text[100];
	int fontFace = FONT_HERSHEY_PLAIN;
	double fontScale = 1;
	int thickness = 1;
	cv::Point textOrg(10, 50);

	vector<Point2f> currFeatures;

	char filename[100];

//	  Mat traj = Mat::zeros(600, 600, CV_8UC3);

//	for (int numFrame = 2; numFrame < MAX_FRAME; numFrame++) {
	sprintf(filename, "/storage/emulated/0/image_2/%06d.png", i);
//	Mat currImage_c = imread(filename);
	Mat & currImage_c = *(Mat*) afterPic;
	cvtColor(currImage_c, currImage, COLOR_BGR2GRAY);
	vector<uchar> status;
	featureTracking(prevImage, currImage, prevFeatures, currFeatures, status);

	E = findEssentialMat(currFeatures, prevFeatures, focal, pp, RANSAC, 0.999,
			3.0, mask);
	recoverPose(E, currFeatures, prevFeatures, R, t, focal, pp, mask);

	Mat prevPts(2, prevFeatures.size(), CV_64F), currPts(2, currFeatures.size(),
			CV_64F);

	/*cv::Point p1, p2;
	p1.y = currImage.rows/2.0; p1.x = 0.0;
	p2.y = currImage.rows; p2.x = currImage.cols;
	cv::Rect mask(p1,p2);
	cv::Mat croppedImage1 = prevImage(mask);
	cv::Mat croppedImage2 = currImage(mask);

	vector<uchar> status_h;
	featureTracking(croppedImage1, croppedImage2, prevFeatures_h, currFeatures_h, status_h);

	H = findHomography(currFeatures_h, prevFeatures_h);
	recoverPose(E, currFeatures, prevFeatures, R, t, focal, pp, mask);*/

//	for (int i = 0; i < prevFeatures.size(); i++) { //this (x,y) combination makes sense as observed from the source code of triangulatePoints on GitHub
//	  		prevPts.at<double>(0,i) = prevFeatures.at(i).x;
//	  		prevPts.at<double>(1,i) = prevFeatures.at(i).y;
//
//	  		currPts.at<double>(0,i) = currFeatures.at(i).x;
//	  		currPts.at<double>(1,i) = currFeatures.at(i).y;
//	}

	scale = 5; //getAbsoluteScale(numFrame, 0, t.at<double>(2));
	if ((scale > 0.1) && (t.at<double>(2) > t.at<double>(0))
			&& (t.at<double>(2) > t.at<double>(1))) {

		t_f = t_f + scale * (R_f * t);
		R_f = R * R_f;
	}

	else {
		//cout << "scale below 0.1, or incorrect translation" << endl;
	}
	// a redetection is triggered in case the number of feautres being trakced go below a particular threshold
	if (prevFeatures.size() < MIN_NUM_FEAT) {
		featureDetection(prevImage, prevFeatures);
		featureTracking(prevImage, currImage, prevFeatures, currFeatures,
				status);
	}
	prevImage = currImage.clone();
	prevFeatures = currFeatures;

	int x = int(t_f.at<double>(0)) + 300;
	int y = int(t_f.at<double>(2)) + 200;
	circle(traj, Point(x, y), 1, CV_RGB(0,0,255), 2);

	rectangle(traj, Point(10, 30), Point(550, 50), CV_RGB(0,0,0), CV_FILLED);
	/*sprintf(text, "Coordinates: x = %02fm y = %02fm z = %02fm",
			t_f.at<double>(0), t_f.at<double>(1), t_f.at<double>(2));
	putText(traj, text, textOrg, fontFace, fontScale, Scalar::all(255),
			thickness, 8);*/

	clock_t end = clock();

	char n1[40];
	sprintf(n1, "%d %d", x, y);

	return jenv->NewStringUTF(n1);

	}	

}