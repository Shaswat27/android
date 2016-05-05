LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

include /home/shaswat/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE     := visodo
LOCAL_SRC_FILES  := visodo.cpp
LOCAL_LDLIBS     += -llog -ldl
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

include /home/shaswat/OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_MODULE     := native_sample
LOCAL_SRC_FILES  := jni_part.cpp
LOCAL_LDLIBS     += -llog -ldl


include $(BUILD_SHARED_LIBRARY)
