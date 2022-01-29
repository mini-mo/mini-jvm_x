public class ThreadTest2 {
  public static void main(String[] args) {

    var t1 =  new TestThread();
    t1.x = 100;
    t1.start();

    var t2 = new TestThread();
    t2.x = 500; 
    t2.start();
  }
}

class TestThread extends java.lang.Thread {

  int x;

  public void run() {
    for (int i = 0; i < x; i++) {
      System.out.println(i);
    }
  }
}
