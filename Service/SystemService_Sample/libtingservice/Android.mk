LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
	src/ITingService.cpp \
	src/BnTingService.cpp \
	src/BpTingService.cpp \
	src/TingService.cpp
	
LOCAL_C_INCLUDES := \
	$(LOCAL_PATH) \
	$(LOCAL_PATH)/include

LOCAL_SHARED_LIBRARIES := \
	libutils \
	libcutils \
	libbinder \
	liblog

LOCAL_MODULE := libtingservice

LOCAL_MODULE_TAGS := optional eng debug

LOCAL_PRELINK_MODULE := false

include $(BUILD_SHARED_LIBRARY)
