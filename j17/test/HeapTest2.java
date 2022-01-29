import core.Heap;

public class HeapTest2 {

  public static void main(String[] args) {
    Heap.init(100 * 1024); // 100 KB

    Heap.setInt(0, 0, 1000);
    int v = Heap.getInt(0, 0);
    System.out.println(v);
  }

}
