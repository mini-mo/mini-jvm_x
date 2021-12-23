package core;

public class Threads {

  static NThread main;

  static ThreadLocal<NThread> self = new ThreadLocal<>();

  public static void init() {
    main = new NThread(Thread.currentThread(), new ExecEnv());
    self.set(main);
  }

  public static ExecEnv getExecEnv() {
    return self.get().ee;
  }
}
