//
// Created by Einim on 2016/8/23.
//

#include <jni.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <jni.h>
#include "reg_rw_multi.h"

JNIEXPORT jstring JNICALL Java_com_ln_himaxtouch_MultiRegisterRWActivity_writeCfg
        (JNIEnv *env, jobject thiz, jobjectArray stringArray) {
    int fd = 0;
    int i;
    char fileNode[1024];
    char command[2];
    char data[1024];
    int data_len;
    char test[32];
    int stringCount = (*env)->GetArrayLength(env, stringArray);


    memset(fileNode, 0, sizeof(fileNode));
    memset(command, 0, sizeof(command));
    memset(data, 0, sizeof(data));
    memset(test, 0, sizeof(test));


    // Parse the parameters
    for (i = 0; i < stringCount; i++) {
        jstring string = (jstring) (*env)->GetObjectArrayElement(env, stringArray, i);
        const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
        switch (i) {
            case 0: {
                sprintf(fileNode, "%s", rawString);
                (*env)->ReleaseStringUTFChars(env, string, rawString);
                fd = open(fileNode, O_RDWR);
                if (fd < 0) {
                    sprintf(test, "open fail");
                    return (*env)->NewStringUTF(env, test);
                }
                break;
            }
            case 1: {
                sprintf(data, "%s", rawString);
                data_len = strlen(rawString);
                (*env)->ReleaseStringUTFChars(env, string, rawString);
                break;
            }
            default:
                break;
        }
    }

    // Write configuration
    if (write(fd, &data[0], data_len) < 0) {
        sprintf(test, "write fail: %s %d", data, data_len);
        return (*env)->NewStringUTF(env, test);
    }
    if (fd > 0)
        close(fd);
    return (*env)->NewStringUTF(env, data);
}


JNIEXPORT jstring JNICALL Java_com_ln_himaxtouch_MultiRegisterRWActivity_readCfg
        (JNIEnv *env, jobject thiz, jobjectArray stringArray) {
    int fd = 0;
    char fileNode[1024];
    char data[5120];
    char test[32];

    memset(fileNode, 0, sizeof(fileNode));
    memset(data, 0, sizeof(data));
    memset(test, 0, sizeof(test));

    //Read string
    jstring string = (jstring) (*env)->GetObjectArrayElement(env, stringArray, 0);
    const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
    sprintf(fileNode, "%s", rawString);
    (*env)->ReleaseStringUTFChars(env, string, rawString);

    // Open file
    fd = open(fileNode, O_RDONLY);
    if (fd < 0) {
        sprintf(test, "open fail");
        return (*env)->NewStringUTF(env, test);
    }

    // Read data
    if (read(fd, &data[0], sizeof(data)) < 0) {
        sprintf(test, "read fail");
        return (*env)->NewStringUTF(env, test);
    }

    if (fd > 0)
        close(fd);
    return (*env)->NewStringUTF(env, data);
}

JNIEXPORT jstring JNICALL Java_com_ln_himaxtouch_Show_1MultiRegisterRWActivity_writeCfg
        (JNIEnv *env, jobject thiz, jobjectArray stringArray) {
    int fd = 0;
    int i;
    char fileNode[1024];
    char command[2];
    char data[1024];
    int data_len;
    char test[32];
    int stringCount = (*env)->GetArrayLength(env, stringArray);


    memset(fileNode, 0, sizeof(fileNode));
    memset(command, 0, sizeof(command));
    memset(data, 0, sizeof(data));
    memset(test, 0, sizeof(test));


    // Parse the parameters
    for (i = 0; i < stringCount; i++) {
        jstring string = (jstring) (*env)->GetObjectArrayElement(env, stringArray, i);
        const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
        switch (i) {
            case 0: {
                sprintf(fileNode, "%s", rawString);
                (*env)->ReleaseStringUTFChars(env, string, rawString);
                fd = open(fileNode, O_RDWR);
                if (fd < 0) {
                    sprintf(test, "open fail");
                    return (*env)->NewStringUTF(env, test);
                }
                break;
            }
            case 1: {
                sprintf(data, "%s", rawString);
                data_len = strlen(rawString);
                (*env)->ReleaseStringUTFChars(env, string, rawString);
                break;
            }
            default:
                break;
        }
    }

    // Write configuration
    if (write(fd, &data[0], data_len) < 0) {
        sprintf(test, "write fail: %s %d", data, data_len);
        return (*env)->NewStringUTF(env, test);
    }
    if (fd > 0)
        close(fd);
    return (*env)->NewStringUTF(env, data);
}


JNIEXPORT jstring JNICALL Java_com_ln_himaxtouch_Show_1MultiRegisterRWActivity_readCfg
        (JNIEnv *env, jobject thiz, jobjectArray stringArray) {
    int fd = 0;
    char fileNode[1024];
    char data[5120];
    char test[32];

    memset(fileNode, 0, sizeof(fileNode));
    memset(data, 0, sizeof(data));
    memset(test, 0, sizeof(test));

    //Read string
    jstring string = (jstring) (*env)->GetObjectArrayElement(env, stringArray, 0);
    const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
    sprintf(fileNode, "%s", rawString);
    (*env)->ReleaseStringUTFChars(env, string, rawString);

    // Open file
    fd = open(fileNode, O_RDONLY);
    if (fd < 0) {
        sprintf(test, "open fail");
        return (*env)->NewStringUTF(env, test);
    }

    // Read data
    if (read(fd, &data[0], sizeof(data)) < 0) {
        sprintf(test, "read fail");
        return (*env)->NewStringUTF(env, test);
    }

    if (fd > 0)
        close(fd);
    return (*env)->NewStringUTF(env, data);
}
