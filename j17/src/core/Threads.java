package core;

import cls.Clazz;
import cls.Method;

public class Threads {

  static VMThread main;

  static ThreadLocal<VMThread> self = new ThreadLocal<>();

  public static void init() {
    main = new VMThread(Thread.currentThread(), new ExecEnv());
    self.set(main);
  }

  public static ExecEnv getExecEnv() {
    return self.get().ee;
  }

  public static void init2() {
    main.ee.status = Const.THREAD_RUNNING;
  }

  public static void createJavaThread(int thread) {
    Thread t = new Thread(threadStart(thread));
    t.start();
  }

  public static Runnable threadStart(int thread) {
    return new Runnable() {
      @Override
      public void run() {
        VMThread vt = new VMThread(Thread.currentThread(), new ExecEnv());
        self.set(vt);

        Clazz cls = MetaSpace.resolveClass(Heap.getInt(thread, -8));
        Method m = Resolver.resolveMethod(cls, "run", "()V");
        vt.ee.status = Const.THREAD_RUNNING;
        Executor.executeMethod(thread, cls.offset, m);
        vt.ee.status = Const.THREAD_DONE;
      }
    };
  }
}
