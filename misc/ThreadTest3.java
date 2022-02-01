public class ThreadTest3 {
  public static void main(String[] args) throws Exception {
    long start = System.currentTimeMillis();
    Thread.sleep(2000);
    long end = System.currentTimeMillis();
    System.out.println(end - start);
  }
}
