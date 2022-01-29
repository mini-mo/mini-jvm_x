package utils;

public class LongUtil {

  public static long merge(int low, int high) {
    return ((high & 0x000000ffffffffL) << 32) | (low & 0x00000000ffffffffL);
  }

  public static int[] split(long val) {
    int high = (int) (val >> 32);
    int low = (int) (val & 0x000000ffffffffL);
    return new int[]{high, low};
  }

}
