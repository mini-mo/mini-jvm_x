import cls.ClassLoader;
import core.Executor;
import core.Heap;
import core.Natives;
import core.Resolver;
import core.Threads;
import java.io.IOException;

/**
 * java
 */
public class Java {

  // misc/Hello.class method args...
  public static void main(String[] args) throws IOException {

    String name = args[0];

    init();

    int[] pa = new int[args.length - 1];
    for (int i = 1; i < args.length; i++) {
      pa[i - 1] = Integer.parseInt(args[i]);
    }

    var cls = ClassLoader.findSystemClass(name);

    var main = Resolver.resolveMethod(cls, "main", "()V"); // 无参
    if (pa.length == 1) {
      var tmp = Resolver.resolveMethod(cls, "main", "(I)V");
      if (tmp != null) {
        main = tmp;
      }
    }
    if (pa.length == 2) {
      var tmp = Resolver.resolveMethod(cls, "main", "(II)V");
      if (tmp != null) {
        main = tmp;
      }
    }
    if (pa.length >= 3) { // 最多三个参数
      var tmp = Resolver.resolveMethod(cls, "main", "(III)V");
      if (tmp != null) {
        main = tmp;
      }
    }
    if (main == null) {
      throw new IllegalStateException("not found main method");
    }

    Executor.executeStaticMethod(cls.offset, main, pa);
  }

  private static void init() {
    ClassLoader.init("misc:sdk");
    Natives.init();
    Threads.init();
    Heap.init(10 * 1024 * 1024); // 10 MB
  }
}
  