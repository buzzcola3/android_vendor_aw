LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := softwinner.audio.static
LOCAL_SRC_FILES := $(call all-java-files-under, java)
LOCAL_JAVA_LIBRARIES := services.core
LOCAL_STATIC_JAVA_LIBRARIES := softwinner.display
include $(BUILD_STATIC_JAVA_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := softwinner.audio
LOCAL_SRC_FILES := $(call all-java-files-under, java)
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_JAVA_LIBRARIES := services.core
LOCAL_STATIC_JAVA_LIBRARIES := softwinner.display
include $(BUILD_JAVA_LIBRARY)
