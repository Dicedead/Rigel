#include <jni.h>
#include "ObservedSky.h"

JNIEXPORT jobject JNICALL Java_ObservedSky_objectClosestToNative(JNIEnv *env, jobject obj, jint i)
{
  return i * i;
}