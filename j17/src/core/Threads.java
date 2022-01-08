package core;

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
}
