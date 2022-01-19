package core;

import cls.Method;

/**
 * 执行器
 */
public class Executor {

  public static int executeStaticMethod(
      int cls,
      Method method,
      int... args
  ) {
    return executeMethod(0, cls, method, args);
  }

  public static int executeMethod(
      int obj,
      int cls,
      Method method,
      int... args
  ) {
    var ee = Threads.getExecEnv();
    ee.createDummyFrame();
    var frame = ee.createFrame(MetaSpace.resolveClass(cls), method);

    var len = method.argsLength();
    var dp = 0;
    if (obj > 0) {
      frame.locals[0] = obj;
      dp = 1;
    }
    if (len > dp) {
      System.arraycopy(args, 0, frame.locals, dp, len);
    }

    try {
      return Interpreter.executeJava();
    } finally {
      ee.popFrame(); // pop dummy frame
    }
  }

}
