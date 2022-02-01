public class Thread implements Runnable {

  public void run() {
    System.out.println(1);
  }

  public native void start();

  public static native void sleep(long millis);
}
