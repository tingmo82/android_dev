LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	MainTingServer.cpp

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)/../libtingservice/include
	
LOCAL_SHARED_LIBRARIES := \
	libutils \
	libbinder \
	liblog \
	libtingservice

LOCAL_MODULE := tingserver

LOCAL_MODULE_TAGS := optional eng debug

include $(BUILD_EXECUTABLE)
