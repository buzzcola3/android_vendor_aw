$(call inherit-product-if-exists, vendor/aw/homlet/hardware/input/multi_ir/multiir.mk)
#app
PRODUCT_PACKAGES += \
    AwTvProvision \
    TvdVideo \
    TvLauncher \
    TvSettings \
    TvdFileManager \
    SettingsAssist \
    MiracastReceiver \
    WebScreensaver \
    DragonBox \
    DragonAgingTV \
    GalleryTV \
    provision-permissions \
    privapp-vendor-permissions \
    #DragonSN  \

PRODUCT_PACKAGES += \
    LiveTv \
    TvProvider \
    tv_input.default

#service
PRODUCT_PACKAGES += \
    libsystemmix_jni \
    systemmixservice \
    libsystemmixservice \
    isomountservice \
    libisomountmanager_jni \
    libisomountmanagerservice \
    libsystemmix \
    nfsprobe \
    libjni_swos \
    libadmanager_jni \
    libjni_WFDManager.so \
    libwfdrtsp.so \
    libwfdplayer.so \
    libwfdmanager.so

# utils, add multi_ir to recovery
PRODUCT_PACKAGES += \
   qw \

PRODUCT_PACKAGES += \
   appsdisable

#add debug method for eng img
ifeq ($(TARGET_BUILD_VARIANT),eng)
PRODUCT_PROPERTY_OVERRIDES += \
    vold.debug=true
endif



# for gpio
PRODUCT_PACKAGES += \
    gpioservice \
    libgpio_jni \
    libgpioservice \

# for homlet display hal
PRODUCT_PACKAGES += \
    libhwcprivateservice \
    libedid \
    libdisplayd \
    softwinner.homlet.displayd@1.0 \
    softwinner.homlet.displayd@1.0-service

PRODUCT_PACKAGES += \
    displayd-test \
    softwinner.display \
    DisplayDaemon \
    DisplayDaemonService \
    dispdebug \
    dispconfig \
    hdcptool

# for audio
PRODUCT_PACKAGES += \
    softwinner.audio

# secure storage support
PRODUCT_PACKAGES += \
    libsst \
    sst_test_v2

# format Reserve0 for save display config
PRODUCT_PACKAGES += \
   format_device

#logcatd -n 2 -r 200000
PRODUCT_SYSTEM_DEFAULT_PROPERTIES += \
    persist.logd.RotateSizeKBytes=200000 \
    persist.logd.logpersistd.size=2
