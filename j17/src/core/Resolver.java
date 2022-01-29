package core;

import cls.ClassLoader;
import cls.Clazz;
import cls.CpInfo;
import cls.Field;
import cls.Method;

public abstract class Resolver {

  public static int u2(byte[] raw) {
    return u2(raw, 0);
  }

  public static int u2(
      byte[] raw,
      int offset
  ) {
    return (raw[offset] << 8) + (raw[offset + 1]);
  }

  public static int s2(
      byte[] raw,
      int offset
  ) {
    return (raw[offset] << 8) | (raw[offset + 1]);
  }

  /**
   * 两个 u1 合并 s2
   *
   * > The immediate unsigned byte1 and byte2 values are assembled into an intermediate short, where the value of the short is (byte1 << 8) | byte2. The intermediate value is then sign-extended to an int value. That value is pushed onto the operand stack.
   */
  public static int c2(
      byte[] raw,
      int offset
  ) {
    return (raw[offset] << 8) | (raw[offset + 1] & 0xff);
  }

  public static int u4(byte[] raw) {
    return u4(raw, 0);
  }

  public static int u4(
      byte[] raw,
      int offset
  ) {
    return (((raw[offset++] & 0xff) << 24) + ((raw[offset++] & 0xff) << 16) + ((raw[offset++] & 0xff) << 8) + ((
        raw[offset++] & 0xff)));
  }

  public static String className(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info), cp);
  }

  public static String methodName(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info), cp);
  }

  public static String methodDescriptor(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info, 2), cp);
  }

  public static String fieldName(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info), cp);
  }

  public static String fieldDescriptor(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info, 2), cp);
  }

  public static String utf8(
      int idx,
      CpInfo[] cp
  ) {
    return new String(cp[idx].info);
  }

  public static byte[] raw(
      byte[] raw,
      int offset,
      int size
  ) {
    var bytes = new byte[size];
    for (int i = 0; i < size; i++) {
      bytes[i] = raw[offset++];
    }
    return bytes;
  }

  public static Method resolveMethod(
      Clazz cls,
      String name,
      String descritor
  ) {
    for (Method m : cls.methods) {
      if (m.name.equals(name) && m.descriptor.equals(descritor)) {
        return m;
      }
    }
    return null;
  }

  public static Method resolveIMethod(
      Clazz cls,
      String name,
      String descritor
  ) {
    for (Method m : cls.imethods) {
      if (m.name.equals(name) && m.descriptor.equals(descritor)) {
        return m;
      }
    }
    return null;
  }

  public static int u1(
      byte[] code,
      int pc
  ) {
    return code[pc] & 0xff;
  }

  public static int s1(
      byte[] code,
      int pc
  ) {
    return code[pc];
  }

  public static Field resolveField(
      Clazz cls,
      String name,
      String type
  ) {
    for (int i = cls.fields.length - 1; i >= 0; i--) {
      var field = cls.fields[i];
      if (field.name.equals(name) && field.descriptor.equals(type)) {
        return field;
      }
    }
    return null;
  }

  public static Clazz resolveClass(String className) {
    Clazz cls = ClassLoader.findSystemClass(className);
    if (cls == null) {
      throw new IllegalStateException();
    }

    // cinit
    clinit(cls);

    return cls;
  }

  public static Clazz clinit(Clazz cls) {
    if (cls.state >= Const.CLASS_INITING) {
      return cls;
    }
    // spr
    if (cls.spr != null && cls.spr.state < Const.CLASS_INITING) {
      clinit(cls.spr);
    }

    Method m = resolveMethod(cls, "<clinit>", "()V");
    if (m == null) { // 无类初始化方法
      cls.state = Const.CLASS_INITED;
      return cls;
    }

    cls.state = Const.CLASS_INITING;
    Executor.executeStaticMethod(cls.offset, m);
    cls.state = Const.CLASS_INITED;

    return cls;
  }

}
