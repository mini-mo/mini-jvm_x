package core;

import cls.Clazz;
import utils.Array;
import utils.Map;

public abstract class MetaSpace {

  private static Map<NativeMethod> JNI = new Map<>();
  private static Array<Clazz> CLS = new Array<>();

  public static NativeMethod resolveNativeMethod(byte[] key) {
    return JNI.get(key);
  }

  public static void registerNativeMethod(
      byte[] key,
      NativeMethod nativeMethod
  ) {
    JNI.put(key, nativeMethod);
  }

  public static int registerClass(Clazz cls) {
    return CLS.append(cls);
  }

  public static Clazz resolveClass(int index) {
    return CLS.get(index);
  }
}
