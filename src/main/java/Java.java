import cls.ClassReader;
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

    MethodInfo method = null;
    for (MethodInfo mi : cf.methods) {
      final String name = Resolver.utf8(mi.nameIndex, cp);
      if (name.equals(args[1])) {
        method = mi;
      }
    }

    byte[] code = null;
    int[] locals = null;
    int[] stacks = null;
    for (var attribute : method.attributes) {
      final String name = Resolver.utf8(attribute.attributeNameIndex, cp);
      if (name.equals("Code")) {
        var raw = attribute.info;
        var offset = 0;
        var ms = Resolver.u2(raw, offset);
        stacks = new int[ms];
        offset += 2;
        var ml = Resolver.u2(raw, offset);
        locals = new int[ml];
        offset += 2;

        var clen = Resolver.u4(raw, offset);
        offset += 4;
        code = Resolver.raw(raw, offset, clen);
      }
    }

    final Frame frame = Threads.getExecEnv().createFrame();
    frame.locals = locals;
    frame.stacks = stacks;
    frame.code = code;
    frame.cp = cf.cp;

    // args
    for (int i = 0; i < pa.length; i++) {
      locals[i] = pa[i];
    }

    Interpreter.executeJava();
  }

  private static void init() {
    Natives.init();
    Threads.init();
  }
}
  