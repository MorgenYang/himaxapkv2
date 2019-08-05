#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <jni.h>


jstring Java_com_ln_himaxtouch_MainActivity_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
              const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
		    case 0: {
		    	sprintf(fileNode, "%s", rawString);
		    	(*env)->ReleaseStringUTFChars(env, string, rawString);
		    	fd = open(fileNode, O_RDWR);
		    	if(fd<0){
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
		    default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
	    sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}


jstring
Java_com_ln_himaxtouch_MainActivity_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
   	(*env)->ReleaseStringUTFChars(env, string, rawString);

   	// Open file
   	fd = open(fileNode, O_RDONLY);
   	if(fd<0){
   		sprintf(test, "open fail");
   		return (*env)->NewStringUTF(env, test);
   	}

   	// Read data
    if(read(fd, &data[0], sizeof(data))<0){
	    sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}
//phone state
jstring
Java_com_ln_himaxtouch_Phonestate_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
			case 0: {
				sprintf(fileNode, "%s", rawString);
				(*env)->ReleaseStringUTFChars(env, string, rawString);
				fd = open(fileNode, O_RDWR);
				if(fd<0){
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
			default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
		sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
		close(fd);
	return (*env)->NewStringUTF(env, data);
}
jstring
Java_com_ln_himaxtouch_Phonestate_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray ) {
	int fd = 0;
	char fileNode[1024];
	FILE *ff;
	char data[5120];
	char test[32];
	int retry=3;

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));
retry_read: {
	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
	(*env)->ReleaseStringUTFChars(env, string, rawString);

	// Open file

	fd = open(fileNode,O_RDONLY);
	if(fd<0){

		if(retry>0)
		{
			retry--;
			goto retry_read;
		}
		else {
			sprintf(test, "open fail");
			return (*env)->NewStringUTF(env, test);
		}
	}

	// Read data
	if(read(fd, &data[0], sizeof(data))<0){
		sprintf(test, "read fail");
		if(retry>0)
		{
			retry--;
			goto retry_read;
		}
		else
			return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
		close(fd);
	return (*env)->NewStringUTF(env, data);
	}
}
jstring
Java_com_ln_himaxtouch_TouchMonitorActivity_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
		    case 0: {
		    	sprintf(fileNode, "%s", rawString);
		    	(*env)->ReleaseStringUTFChars(env, string, rawString);
		    	fd = open(fileNode, O_RDWR);
		    	if(fd<0){
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
		    default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
	    sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

jstring
Java_com_ln_himaxtouch_TouchMonitorActivity_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[10240];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
   	(*env)->ReleaseStringUTFChars(env, string, rawString);

   	// Open file
   	fd = open(fileNode, O_RDONLY);
   	if(fd<0){
   		sprintf(test, "open fail");
   		return (*env)->NewStringUTF(env, test);
   	}

   	// Read data
    if(read(fd, &data[0], sizeof(data))<0){
	    sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

//
jstring
Java_com_ln_himaxtouch_RegisterRWActivity_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
		    case 0: {
		    	sprintf(fileNode, "%s", rawString);
		    	(*env)->ReleaseStringUTFChars(env, string, rawString);
		    	fd = open(fileNode, O_RDWR);
		    	if(fd<0){
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
		    default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
	    sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

jstring
Java_com_ln_himaxtouch_RegisterRWActivity_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
   	(*env)->ReleaseStringUTFChars(env, string, rawString);

   	// Open file
   	fd = open(fileNode, O_RDONLY);
   	if(fd<0){
   		sprintf(test, "open fail");
   		return (*env)->NewStringUTF(env, test);
   	}

   	// Read data
    if(read(fd, &data[0], sizeof(data))<0){
	    sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}
//
jstring
Java_com_ln_himaxtouch_TotalSelfTestActivity_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
		    case 0: {
		    	sprintf(fileNode, "%s", rawString);
		    	(*env)->ReleaseStringUTFChars(env, string, rawString);
		    	fd = open(fileNode, O_RDWR);
		    	if(fd<0){
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
		    default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
	    sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

jstring
Java_com_ln_himaxtouch_TotalSelfTestActivity_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
   	(*env)->ReleaseStringUTFChars(env, string, rawString);

   	// Open file
   	fd = open(fileNode, O_RDONLY);
   	if(fd<0){
   		sprintf(test, "open fail");
   		return (*env)->NewStringUTF(env, test);
   	}

   	// Read data
    if(read(fd, &data[0], sizeof(data))<0){
	    sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}


//Self test
jstring
Java_com_ln_himaxtouch_SelfTestActivity_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
		    case 0: {
		    	sprintf(fileNode, "%s", rawString);
		    	(*env)->ReleaseStringUTFChars(env, string, rawString);
		    	fd = open(fileNode, O_RDWR);
		    	if(fd<0){
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
		    default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
	    sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

jstring
Java_com_ln_himaxtouch_SelfTestActivity_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
   	(*env)->ReleaseStringUTFChars(env, string, rawString);

   	// Open file
   	fd = open(fileNode, O_RDONLY);
   	if(fd<0){
   		sprintf(test, "open fail");
   		return (*env)->NewStringUTF(env, test);
   	}

   	// Read data
    if(read(fd, &data[0], sizeof(data))<0){
	    sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

//burst FW
jstring
Java_com_ln_himaxtouch_UpgradeFW_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
			case 0: {
				sprintf(fileNode, "%s", rawString);
				(*env)->ReleaseStringUTFChars(env, string, rawString);
				fd = open(fileNode, O_RDWR);
				if(fd<0){
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
			default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
		sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
		close(fd);
	return (*env)->NewStringUTF(env, data);
}
jstring
Java_com_ln_himaxtouch_UpgradeFW_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray ) {
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
	(*env)->ReleaseStringUTFChars(env, string, rawString);

	// Open file
	fd = open(fileNode, O_RDONLY);
	if(fd<0){
		sprintf(test, "open fail");
		return (*env)->NewStringUTF(env, test);
	}

	// Read data
	if(read(fd, &data[0], sizeof(data))<0){
		sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
		close(fd);
	return (*env)->NewStringUTF(env, data);

}
//Touchevent
jstring
Java_com_ln_himaxtouch_Touchevent_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
		const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
			case 0: {
				sprintf(fileNode, "%s", rawString);
				(*env)->ReleaseStringUTFChars(env, string, rawString);
				fd = open(fileNode, O_RDWR);
				if(fd<0){
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
			default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
		sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
		close(fd);
	return (*env)->NewStringUTF(env, data);
}


jstring
Java_com_ln_himaxtouch_Touchevent_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
	(*env)->ReleaseStringUTFChars(env, string, rawString);

	// Open file
	fd = open(fileNode, O_RDONLY);
	if(fd<0){
		sprintf(test, "open fail");
		return (*env)->NewStringUTF(env, test);
	}

	// Read data
	if(read(fd, &data[0], sizeof(data))<0){
		sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
		close(fd);
	return (*env)->NewStringUTF(env, data);
}

jstring Java_com_ln_himaxtouch_NodeDataSource_writeCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
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
	for(i=0;i<stringCount;i++)
	{
		jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, i);
              const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
		switch (i)
		{
		    case 0: {
		    	sprintf(fileNode, "%s", rawString);
		    	(*env)->ReleaseStringUTFChars(env, string, rawString);
		    	fd = open(fileNode, O_RDWR);
		    	if(fd<0){
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
		    default: break;
		}
	}

	// Write configuration
	if(write(fd, &data[0], data_len)<0){
	    sprintf(test, "write fail: %s %d", data, data_len);
		return (*env)->NewStringUTF(env, test);
	}
	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}


jstring
Java_com_ln_himaxtouch_NodeDataSource_readCfg( JNIEnv* env, jobject thiz, jobjectArray stringArray )
{
	int fd = 0;
	char fileNode[1024];
	char data[5120];
	char test[32];

	memset(fileNode, 0, sizeof(fileNode));
	memset(data, 0, sizeof(data));
	memset(test, 0, sizeof(test));

	//Read string
	jstring string = (jstring)(*env)->GetObjectArrayElement(env, stringArray, 0);
	const char *rawString = (*env)->GetStringUTFChars(env, string, 0);
	sprintf(fileNode, "%s", rawString);
   	(*env)->ReleaseStringUTFChars(env, string, rawString);

   	// Open file
   	fd = open(fileNode, O_RDONLY);
   	if(fd<0){
   		sprintf(test, "open fail");
   		return (*env)->NewStringUTF(env, test);
   	}

   	// Read data
    if(read(fd, &data[0], sizeof(data))<0){
	    sprintf(test, "read fail");
		return (*env)->NewStringUTF(env, test);
	}

	if(fd > 0)
	    close(fd);
    return (*env)->NewStringUTF(env, data);
}

