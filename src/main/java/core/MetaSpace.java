package core;


import utils.Map;

public abstract class MetaSpace {

  private static Map<NativeMethod> JNI = new Map<>();

  public static NativeMethod resolveNativeMethod(byte[] key) {
    return JNI.get(key);
  }

  public static void registerNativeMethod(
      byte[] key,
      NativeMethod nativeMethod
  ) {
    JNI.put(key, nativeMethod);
  }
}
