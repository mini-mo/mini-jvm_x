import cls.ClassLoader;
import cls.Method;
import core.Executor;
import core.Heap;
import core.Natives;
import core.Resolver;
import core.Threads;
import utils.LongUtil;

import java.io.IOException;

/**
 * java
 */
public class Java {

  // misc/Hello.class method args...
  public static void main(String[] args) throws IOException {

    String name = args[0];

    init();

    boolean longFlag = args.length > 1
            && (args[1].endsWith("L") || args[1].endsWith("l") || Long.parseLong(args[1]) > Integer.MAX_VALUE);
    int[] pa = new int[longFlag ? ((args.length - 1) * 2) : args.length - 1];
    for (int i = 1; i < args.length; i++) {
      if (longFlag){
        long val = (args[i].endsWith("L") || args[i].endsWith("l"))
                ? Long.parseLong(args[i].substring(0, args[i].length() - 1)) : Long.parseLong(args[i]);
        int[] r = LongUtil.split(val);
        pa[i - 1] = r[0];
        pa[i++] = r[1];
      } else {
        pa[i - 1] = Integer.parseInt(args[i]);
      }
    }

    var cls = ClassLoader.findSystemClass(name);

    var main = Resolver.resolveMethod(cls, "main", "()V"); // 无参
    if (args.length == 2) {
      Method tmp;
      if (longFlag) {
        tmp = Resolver.resolveMethod(cls, "main", "(J)V");
      } else {
        tmp = Resolver.resolveMethod(cls, "main", "(I)V");
      }
      if (tmp != null) {
        main = tmp;
      }
    }
    if (args.length == 3) {
      Method tmp;
      if (longFlag) {
        tmp = Resolver.resolveMethod(cls, "main", "(JJ)V");
      } else {
        tmp = Resolver.resolveMethod(cls, "main", "(II)V");
      }
      if (tmp != null) {
        main = tmp;
      }
    }
    if (args.length >= 4) { // 最多三个参数
      Method tmp;
      if (longFlag) {
        tmp = Resolver.resolveMethod(cls, "main", "(JJJ)V");
      } else {
        tmp = Resolver.resolveMethod(cls, "main", "(III)V");
      }
      if (tmp != null) {
        main = tmp;
      }
    }
    // try default main
    if (main == null) {
      var tmp = Resolver.resolveMethod(cls, "main", "([Ljava/lang/String;)V");
      if (tmp != null) {
        main = tmp;
        // hack for default main
        pa = new int[]{0};
      }
    }

    if (main == null) {
      throw new IllegalStateException("not found main method");
    }

    Executor.executeStaticMethod(cls.offset, main, pa);
  }

  private static void init() {
    ClassLoader.init("../misc:../sdk");
    Natives.init();
    Threads.init();
    Heap.init(10 * 1024 * 1024); // 10 MB
  }
}
  