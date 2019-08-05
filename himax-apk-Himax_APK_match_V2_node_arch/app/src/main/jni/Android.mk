LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := HimaxAPK
LOCAL_SRC_FILES := HimaxAPK.c
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

LOCAL_MODULE    := get_permission
LOCAL_SRC_FILES := get_permission.c

include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)


LOCAL_MODULE    := reg_rw_multi
LOCAL_SRC_FILES := reg_rw_multi.c

include $(BUILD_SHARED_LIBRARY)
