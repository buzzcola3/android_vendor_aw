package com.softwinner;
interface IDisplayOutputManager {
  int getDisplayOutput(int display);
  int setDisplayOutput(int display, int format);
  int getDisplayOutputType(int display);
  int getDisplayOutputMode(int display);
  int setDisplayOutputMode(int display, int mode);
  int getDisplayOutputPixelFormat(int display);
  int setDisplayOutputPixelFormat(int display, int format);
  int getDisplayOutputCurDataspaceMode(int display);
  int getDisplayOutputDataspaceMode(int display);
  int setDisplayOutputDataspaceMode(int display, int mode);
  boolean isSupportHdmiMode(int display, int mode);
  int[] getSupportModes(int display, int type);
  int getDisplaySupport3DMode(int display);
  int setDisplay3DMode(int display, int mode, int videoCropHeight);
  int setDisplay3DLayerOffset(int display, int offset);
  int[] getDisplayMargin(int display);
  int[] getDisplayOffset(int display);
  int setDisplayMargin(int display, int margin_x, int margin_y);
  int setDisplayOffset(int display, int offset_x, int offset_y);
  int getDisplayEdge(int display);
  int setDisplayEdge(int display, int edge);
  int getDisplayDetail(int display);
  int setDisplayDetail(int display, int detail);
  int getDisplayBright(int display);
  int setDisplayBright(int display, int bright);
  int getDisplayDenoise(int display);
  int setDisplayDenoise(int display, int denoise);
  int getDisplaySaturation(int display);
  int setDisplaySaturation(int display, int saturation);
  int getDisplayContrast(int display);
  int setDisplayContrast(int display, int contrast);
  int getDisplayEnhanceMode(int display);
  int setDisplayEnhanceMode(int display, int mode);
  boolean supportedSNRSetting(int display);
  int getSNRFeatureMode(int display);
  int[] getSNRStrength(int display);
  int setSNRConfig(int display, int mode, int ystrength, int ustrength, int vstrength);
  int getHdmiNativeMode(int display);
  int getHdmiUserSetting(int display);
}
