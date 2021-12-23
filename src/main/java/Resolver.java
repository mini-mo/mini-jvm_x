public abstract class Resolver {

  static int u2(byte[] raw) {
    return u2(raw, 0);
  }

  static int u2(
      byte[] raw,
      int offset
  ) {
    return (raw[offset] << 8) + (raw[offset + 1] << 0);
  }

  static int u4(byte[] raw) {
    return u4(raw, 0);
  }

  static int u4(
      byte[] raw,
      int offset
  ) {
    return (((raw[offset++] & 0xff) << 24) + ((raw[offset++] & 0xff) << 16) + ((raw[offset++] & 0xff) << 8) + (
        (raw[offset++] & 0xff) << 0));
  }

  static String className(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info), cp);
  }

  static String methodName(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info), cp);
  }

  static String methodDescriptor(
      int idx,
      CpInfo[] cp
  ) {
    return utf8(u2(cp[idx].info, 2), cp);
  }

  static String utf8(
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
    final byte[] bytes = new byte[size];
    for (int i = 0; i < size; i++) {
      bytes[i] = raw[offset++];
    }
    return bytes;
  }
}
