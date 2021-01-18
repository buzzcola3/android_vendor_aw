LOCAL_PATH:= $(call my-dir)

CONF_ANDROID_VERSION = $(shell echo $(PLATFORM_VERSION) | cut -c 1-3)

MY_SDK="notdef"

ifeq ($(CONF_ANDROID_VERSION), 9)
MY_SDK=ap
else ifeq ($(CONF_ANDROID_VERSION), 10)
MY_SDK=aq
else
$(warning notdef CONF_ANDROID_VERSION:$(CONF_ANDROID_VERSION))
endif

#$(warning "CONF_ANDROID_VERSION: "$(CONF_ANDROID_VERSION) "MY_SDK: "$(MY_SDK))
include $(CLEAR_VARS)
LOCAL_MODULE_TARGET_ARCH := arm
LOCAL_MODULE_TAGS := optional
LOCAL_MODULE_CLASS := APPS
LOCAL_MODULE_SUFFIX := $(COMMON_ANDROID_PACKAGE_SUFFIX)
LOCAL_CERTIFICATE := platform
LOCAL_MODULE := MiracastReceiver
LOCAL_SRC_FILES := $(MY_SDK)/$(LOCAL_MODULE).apk

ifeq ($(CONF_ANDROID_VERSION), 9)
LOCAL_PREBUILT_JNI_LIBS := \
    lib/arm/$(MY_SDK)/libjni_WFDManager.so \
    lib/arm/$(MY_SDK)/libwfdmanager.so \
    lib/arm/$(MY_SDK)/libwfdrtsp.so \
    lib/arm/$(MY_SDK)/libwfdplayer.so
else ifeq ($(CONF_ANDROID_VERSION), 10)
LOCAL_PREBUILT_JNI_LIBS := \
    lib/arm/$(MY_SDK)/libjni_WFDManager.so \
    lib/arm/$(MY_SDK)/libwfdmanager.so \
    lib/arm/$(MY_SDK)/libwfdrtsp.so \
    lib/arm/$(MY_SDK)/libwfdplayer.so \
    lib/arm/$(MY_SDK)/libcdv_output.so \
    lib/arm/$(MY_SDK)/libcdv_playback.so
endif
include $(BUILD_PREBUILT)
