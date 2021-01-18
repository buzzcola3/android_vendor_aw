/*
**
** Copyright 2008, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License");
** you may not use this file except in compliance with the License.
** You may obtain a copy of the License at
**
**     http://www.apache.org/licenses/LICENSE-2.0
**
** Unless required by applicable law or agreed to in writing, software
** distributed under the License is distributed on an "AS IS" BASIS,
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
** See the License for the specific language governing permissions and
** limitations under the License.
*/


#define NDEBUG 0
#define LOG_TAG "SystemMixService"
#include "SystemMixService.h"
#include <binder/IServiceManager.h>
#include <utils/misc.h>
#include <utils/Log.h>
#include <stdio.h>
#include <malloc.h>
#include <dirent.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/vfs.h>
#include <sys/types.h>
#include <time.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/mount.h>
#include <errno.h>


#include <cutils/properties.h>

#define DEBUG true

namespace android{

#define CUSTOMER_BOOTANIMATION  "/data/local/bootanimation.zip"
#define BUFFSIZE  4096
#define CUSTOMER_BOOTLOGO "/Reserve0/advert.bmp"
#define CUSTOMER_BOOTLOGO_CRC "/Reserve0/advert.crc"

struct advert_head
{
char  magic[16];     //数据头标示 "advert-group"
unsigned int   version;      //00000100
char  partname[16];  //part name
unsigned int   check_sum;    //generated by android
unsigned int   length;  //
char  filename[16];   //图片名字,固定名字advert.bmp
unsigned char  reserve[452];   //512-60
};

void SystemMixService::instantiate(){
    defaultServiceManager()->addService(
        String16("softwinner.systemmix"), new SystemMixService());
}

SystemMixService::SystemMixService(){
    ALOGD("SystemMixService created");
}

SystemMixService::~SystemMixService(){
    ALOGD("SystemMixService destroyed");
}

int SystemMixService::setProperty(const char *key, const char *value){
    if(DEBUG){
        ALOGD("SystemMixService::setproperty()  key = %s  value = %s", key, value);
    }
    int ret = property_set(key, value);
    return ret;
}

int SystemMixService::getProperty(const char *key, char *value){
    if(DEBUG){
        ALOGD("SystemMixService::getproperty()  key = %s", key);
    }

    int ret = property_get(key, value, 0);
    ALOGD("SystemMixService::get()  value = %s", value);
    return ret;

}

int SystemMixService::getFileData(int8_t *data, int count, const char *filePath){
    if(DEBUG){
        ALOGD("SystemMixService::getFileData()  filepath=%s count=%d", filePath, count);
    }
    if(filePath == NULL){
        return 0;
    }
    if(access(filePath, F_OK | R_OK) != 0){
        ALOGE("file access error");
        return 0;
    }
    int MAX_NUM = 2048;
    FILE *srcFp;
    if((srcFp = fopen(filePath, "r")) == NULL){
        ALOGE("cannot open file %s to read", filePath);
        return 0;
    }

    int remainder = count;
    int total = 0;
    int num = 1;
    int8_t *p = data;
    if(count <= 0){
        fclose(srcFp);
        return 0;
    }
    while(remainder > 0 && num > 0){
        if(remainder >= MAX_NUM){
            num = fread(p, sizeof(int8_t), MAX_NUM, srcFp);
            remainder = remainder - num;
            p = p + num;
            total += num;
        }else{
            num = fread(p, sizeof(int8_t), remainder, srcFp);
            remainder = remainder - num;
            p = p + num;
            total += num;
        }
    }
    fclose(srcFp);
    return total;
}

int SystemMixService::putFileData(int8_t *data, int count, const char *filePath){
    if(DEBUG){
        ALOGD("SystemMixService::putFileData()  filepath=%s count=%d", filePath, count);
    }
    if(filePath == NULL){
        return 0;
    }
    if(access(filePath, F_OK | W_OK) != 0){
        ALOGE("file access error");
        //return 0; create new file
    }
    int MAX_NUM = 2048;
    FILE *srcFp;
    if((srcFp = fopen(filePath, "w")) == NULL){
        ALOGE("cannot open file %s to write", filePath);
        ALOGE("error is %s",strerror(errno));
        return 0;
    }

    int remainder = count;
    int total = 0;
    int num = 1;
    int8_t *p = data;
    if(count <= 0){
        fclose(srcFp);
        return 0;
    }
    while(remainder > 0 && num > 0){
        if(remainder >= MAX_NUM){
            num = fwrite(p, sizeof(int8_t), MAX_NUM, srcFp);
            remainder = remainder - num;
            p = p + num;
            total += num;
        }else{
            num = fwrite(p, sizeof(int8_t), remainder, srcFp);
            remainder = remainder - num;
            p = p + num;
            total += num;
        }
    }
    fflush(srcFp);
    fclose(srcFp);
    return total;
}

int SystemMixService::mountDev(const char *src, const char *mountPoint, const char *fs,
        unsigned int flag, const char *options){
    int ret = mount(src, mountPoint, fs, flag, options);
    ALOGD("============Mount============");
    ALOGD("src             %s", src);
    ALOGD("mountPoint    %s", mountPoint);
    ALOGD("fs            %s", fs);
    ALOGD("flag            %d", flag);
    ALOGD("options        %s", options);
    ALOGD("ret            %d", ret);
    if(ret != 0){
        ALOGD("errno            %s", strerror(errno));
    }
    ALOGD("=============================");
    return (ret == 0) ? ret : errno;
}

int SystemMixService::umountDev(const char *mountPoint){
    int ret = umount2(mountPoint,MNT_FORCE);
    ALOGD("============Mount============");
    ALOGD("umountPoint    %s", mountPoint);
    ALOGD("ret            %d", ret);
    if(ret != 0){
        ALOGD("errno            %s", strerror(errno));
    }
    ALOGD("=============================");
    return (ret == 0) ? ret : errno;
}

int SystemMixService::deleteFile(const char *pathName) {
    int ret = remove(pathName);
    if(ret != 0){
        ALOGD("errno            %s", strerror(errno));
    }
    ALOGD("=============================");
    return (ret == 0) ? ret : errno;
}

int SystemMixService::canWrite(const char *pathName) {
    struct stat buf;
    stat(pathName, &buf);
    if((buf.st_mode) & S_IWUSR ) {
        return 0;
    } else {
        return -1;
    }
}

int SystemMixService::canRead(const char *pathName) {
    struct stat buf;
    stat(pathName, &buf);
    if((buf.st_mode) & S_IRUSR ) {
        return 0;
    } else {
        return -1;
    }
}

int SystemMixService::renewName(const char *oldPathName, const char *newPathName) {
    int ret = rename(oldPathName, newPathName);
    if(ret != 0) {
        ALOGD("renewName: errno    %s", strerror(errno));
    }
    return (ret == 0) ? ret : errno;
}

int SystemMixService::makeDir(const char *pathName) {
    int ret = mkdir(pathName, 0777);
    if(ret != 0) {
        ALOGD("mkdir: errno    %s", strerror(errno));
    }
    return (ret == 0) ? ret : errno;
}
int SystemMixService::setBootLogo(const char *pathName) {
    int ret = 0;
    if (NULL==pathName)
    {
        ALOGW("setBootLogo Path is NULL");
        return -1;
    }
    int source = open(pathName,O_RDONLY);
    if (source < 0)
    {
        ALOGW("readBootLogo File %s fail: errno %s", pathName,strerror(errno));
        return -1;
    }
    int dest = open(CUSTOMER_BOOTLOGO, O_WRONLY|O_CREAT|O_TRUNC,S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);
    if (dest < 0)
    {
        close(source);
        ALOGW("creat or open BootLogo File %s fail : errno %s",CUSTOMER_BOOTLOGO,strerror(errno));
        return -1;
    }
    char buf[BUFFSIZE];
    int n=0;
    while ((n=read(source,buf,BUFFSIZE))>0)
        if (write(dest,buf,n)!=n)
            ret=-1;
    fsync(dest);
    close(source);
    close(dest);
    if (n<0)
        ret=-1;
    if(ret != 0) {
        ALOGW("copy BootLogo erro : errno %s", strerror(errno));
        return errno;
    }
    ret=updateReserve0Crc();
    return ret;
}
int SystemMixService::setBootAnimation(const char *pathName) {
    int ret = 0;
    if (NULL==pathName)
    {
        ALOGW("setBootLogo Path is NULL");
        return -1;
    }
    int source = open(pathName,O_RDONLY);
    if (source < 0)
    {
        ALOGW("readBootAnimation File %s fail: errno %s", pathName,strerror(errno));
        return -1;
    }
    int dest = open(CUSTOMER_BOOTANIMATION,
            O_WRONLY|O_CREAT|O_TRUNC,S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);
    if (dest < 0)
    {
        close(source);
        ALOGW("creat or open BootAnimation File %s fail : errno %s",
                CUSTOMER_BOOTANIMATION,strerror(errno));
        return -1;
    }
    char buf[BUFFSIZE];
    int n=0;
    while ((n=read(source,buf,BUFFSIZE))>0)
        if (write(dest,buf,n)!=n)
            ret=-1;
    fsync(dest);
    close(source);
    close(dest);
    chmod(CUSTOMER_BOOTANIMATION, S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);
    if (n<0)
        ret=-1;
    if(ret != 0) {
        ALOGW("copy BootAnimation erro : errno %s", strerror(errno));
    }
    return (ret == 0) ? ret : errno;
}

int SystemMixService::updateReserve0Crc() {
    struct advert_head head;
    memset(&head,0,sizeof(advert_head));

    int logo = open(CUSTOMER_BOOTLOGO,O_RDONLY);

    if (logo<0)
    {
        ALOGW("open bootlogo error : errno %s",strerror(errno));
        return -1;
    }

    int n=0;
    unsigned int sum=0;
    unsigned char buf[BUFFSIZE];
    while ((n=read(logo,buf,BUFFSIZE))>0)
    {
        if (n%4!=0)
        {
            memset(buf+n,0,BUFFSIZE-n);
            n=n+4;
        }
        unsigned int *x =(unsigned int *)buf;
        while(n>0)
        {
            sum += *x++;
            n -= 4;
        }
    }
    off_t length = lseek(logo,0,SEEK_END);
    close(logo);

    if (n<0)
    {
        ALOGW("check bootlogo error ");
        return -1;
    }

    int crc = open(CUSTOMER_BOOTLOGO_CRC, O_WRONLY|O_CREAT|O_TRUNC,S_IRUSR|S_IWUSR|S_IRGRP|S_IROTH);

    if (crc<0)
    {
        ALOGW("open bootlogo crc error : errno %s",strerror(errno));
        return -1;
    }

    strcpy(head.magic,"advert-group");
    head.version=0x00000100;
    head.check_sum=sum;
    head.length=(unsigned int)length;
    strcpy(head.partname,"Reserve0");
    strcpy(head.filename,"advert.bmp");

    if (write(crc,&head,sizeof(advert_head))!=sizeof(advert_head))
    {
        close(crc);
        ALOGW("save bootlogo crc error : errno %s",strerror(errno));
        return -1;
    }
    fsync(crc);
    close(crc);
    return 0;

}
#undef CUSTOMER_BOOTLOGO
#undef CUSTOMER_BOOTLOGO_CRC
#undef CUSTOMER_BOOTANIMATION
#undef BUFFSIZE

}
