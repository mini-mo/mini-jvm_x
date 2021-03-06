package core;

public abstract class Flags {

  public static boolean isAccPublic(int flags) {
    return (flags & 0x0001) != 0;
  }

  public static boolean isAccStatic(int flags) {
    return (flags & 0x0008) != 0;
  }

  public static boolean isAccSuper(int flags) {
    return (flags & 0x0020) != 0;
  }

  public static boolean isAccPrivate(int flags) {
    return (flags & 0x0002) != 0;
  }

  public static boolean isAccNative(int flags) {
    return (flags & 0x0100) != 0;
  }
}
