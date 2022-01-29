public class Thread implements Runnable {

  public void run() {
    System.out.println(1);
  }

  public native void start();
}
