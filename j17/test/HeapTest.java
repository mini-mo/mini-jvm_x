import core.Heap;

public class HeapTest {

  public static void main(String[] args) {
    Heap.init(100 * 1024); // 100 KB

    var p = Heap.malloc(220);
    System.out.println(p == 16);

    p = Heap.malloc(224);
    System.out.println(p == 248);

    // ---
    Heap.setInt(p, 0, 10);
    var int_x = Heap.getInt(p, 0);
    System.out.println(int_x == 10);

    Heap.setShort(p, 4, (short) 12345);
    var s_x = Heap.getShort(p, 4);
    System.out.println(s_x == 12345);

    Heap.setLong(p, 6, Long.MAX_VALUE);
    var l_x = Heap.getLong(p, 6);
    System.out.println(l_x == Long.MAX_VALUE);

    Heap.set(p, 14, (byte) 211);
    var b_x = Heap.get(p, 14);
    System.out.println(b_x == (byte) 211);

    // -
    int_x = Heap.getInt(p, 0);
    System.out.println(int_x == 10);
    s_x = Heap.getShort(p, 4);
    System.out.println(s_x == 12345);
    l_x = Heap.getLong(p, 6);
    System.out.println(l_x == Long.MAX_VALUE);
    b_x = Heap.get(p, 14);
    System.out.println(b_x == (byte) 211);
  }

}
