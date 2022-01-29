public class ThreadTest {
  public static void main(String[] args) {
    Thread t = new Thread();
    t.start();
    Thread t2 = new Thread();
    t2.start();
  }
}
