package core;

// 本地线程
public class NThread {

  public final Thread thread;
  public final ExecEnv ee;

  public NThread(
      Thread thread,
      ExecEnv ee
  ) {
    this.thread = thread;
    this.ee = ee;
  }
}
