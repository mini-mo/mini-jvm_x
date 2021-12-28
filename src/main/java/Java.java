import cls.ClassLoader;
import cls.ClassReader;
import core.Interpreter;
import core.Natives;
import core.Resolver;
import core.Threads;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * java
 */
public class Java {

  // misc/Hello.class method args...
  public static void main(String[] args) throws IOException {
    var file = new File(args[0]);
    var fis = new FileInputStream(file);
    var bytes = fis.readAllBytes();
    var reader = new ClassReader(bytes);
    var cf = reader.read();


    init();

    int[] pa = new int[args.length - 1];
    for (int i = 1; i < args.length; i++) {
      pa[i - 1] = Integer.parseInt(args[i]);
    }

    var cls = ClassLoader.load(cf);

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

    var frame = Threads.getExecEnv().createFrame(cls, main);

    // args
    for (int i = 0; i < pa.length; i++) {
      frame.locals[i] = pa[i];
    }

    Interpreter.executeJava();
  }

  private static void init() {
    Natives.init();
    Threads.init();
  }
}
  