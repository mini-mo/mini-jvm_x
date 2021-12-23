public abstract class Flags {

  static boolean isAccPublic(int flags) {
    return (flags & 0x0001) != 0;
  }

  static boolean isAccStatic(int flags) {
    return (flags & 0x0008) != 0;
  }

  static boolean isAccSuper(int flags) {
    return (flags & 0x0020) != 0;
  }
}
