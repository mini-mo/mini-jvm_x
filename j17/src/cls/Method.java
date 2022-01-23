package cls;

import core.Flags;

public class Method {

  public String name;
  public String descriptor;

  public int accessFlags;
  public int maxLocals;
  public int maxStacks;
  public byte[] code;

  public int offset;
  public int cls;

  // exceptions
  // line table

  public int argsLength() {
    int s = 1;
    if (Flags.isAccStatic(accessFlags)) {
      s = 0;
    }
    final char[] chars = this.descriptor.toCharArray();

    for (int i = 1; i < chars.length; i++) {
      final char ch = chars[i];
      if (ch == ')') {
        break;
      }
      switch (ch) {
        case 'D', 'J' -> {
          i += 1;
        }
        case 'L', '[' -> {
          while (chars[i + 1] != ';') {
            i++;
          }
          i++;
        }
      }
      s++;
    }

    return s;
  }
}
