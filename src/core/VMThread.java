package core;

// 本地线程
public class VMThread {

  public final Thread thread;
  public final ExecEnv ee;

  public VMThread(
      Thread thread,
      ExecEnv ee
  ) {
    this.thread = thread;
    this.ee = ee;
  }
}
