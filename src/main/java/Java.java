import cls.ClassLoader;
import cls.ClassReader;
import cls.Clazz;
import cls.Method;
import cls.MethodInfo;
import core.Frame;
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
    var cp = cf.cp;


    init();

    int[] pa = new int[args.length - 2];
    for (int i = 2; i < args.length; i++) {
      pa[i - 2] = Integer.parseInt(args[i]);
    }

    Clazz cls = ClassLoader.load(cf);

    Method main = null;
    for (Method m : cls.methods) {
      if (m.name.equals(args[1])) {
        main = m;
      }
    }
    final Frame frame = Threads.getExecEnv().createFrame(cls, main);

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
  