package core;

public abstract class Heap {

  private static byte[] mem;
  private static int heapMax;
  private static int cur;

  /**
   * 分配内存
   * @param len 对象大小
   */
  public static int malloc(int len) {
    // object header 8 byte
    // 8 字节对齐
    var n = (len + 8 + 8 - 1) & ~(8 - 1);
    if (cur + n >= heapMax) {
      throw new IllegalStateException("out of memory");
    }
    var point = cur;
    cur += n;
    return point + 8; // 实例 field 起点所在位置
  }

  // ops
  public static void set(
      int point,
      int offset,
      byte v
  ) {
    mem[point + offset] = v;
  }

  public static void setShort(
      int point,
      int offset,
      short v
  ) {
    mem[point + offset + 0] = (byte) (v >>> 8);
    mem[point + offset + 1] = (byte) (v >>> 0);
  }

  public static void setInt(
      int point,
      int offset,
      int v
  ) {
    mem[point + offset + 0] = (byte) (v >>> 24);
    mem[point + offset + 1] = (byte) (v >>> 16);
    mem[point + offset + 2] = (byte) (v >>> 8);
    mem[point + offset + 3] = (byte) (v >>> 0);
  }

  public static void setLong(
      int point,
      int offset,
      long v
  ) {
    mem[point + offset + 0] = (byte) (v >>> 56);
    mem[point + offset + 1] = (byte) (v >>> 48);
    mem[point + offset + 2] = (byte) (v >>> 40);
    mem[point + offset + 3] = (byte) (v >>> 32);
    mem[point + offset + 4] = (byte) (v >>> 24);
    mem[point + offset + 5] = (byte) (v >>> 16);
    mem[point + offset + 6] = (byte) (v >>> 8);
    mem[point + offset + 7] = (byte) (v >>> 0);
  }

  public static byte get(
      int point,
      int offset
  ) {
    return mem[point + offset];
  }

  public static short getShort(
      int point,
      int offset
  ) {
    return (short) ((mem[point + offset] << 8) + (mem[point + offset + 1] << 0));
  }

  public static int getInt(
      int point,
      int offset
  ) {
    return ((mem[point + offset] << 24) + (mem[point + offset + 1] << 16) + (mem[point + offset + 2] << 8) + (
        mem[point + offset + 3] << 0));
  }

  public static long getLong(
      int point,
      int offset
  ) {
    return (((long) mem[point + offset + 0] << 56) + ((long) (mem[point + offset + 1] & 255) << 48) + (
        (long) (mem[point + offset + 2] & 255) << 40) + ((long) (mem[point + offset + 3] & 255) << 32) + (
        (long) (mem[point + offset + 4] & 255) << 24) + ((mem[point + offset + 5] & 255) << 16) + (
        (mem[point + offset + 6] & 255) << 8) + ((mem[point + offset + 7] & 255) << 0));
  }
  // end ops

  /**
   * 堆初始化
   */
  public static boolean init(int size) {
    mem = new byte[size];
    heapMax = size;
    cur = 0;
    return true;
  }
}
