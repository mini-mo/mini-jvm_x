import utils.Map;

public class MapTest {

  public static void main(String[] args) {
    final Map<Integer> map = new Map<>();

    for (byte i = 0; i < Byte.MAX_VALUE; i++) {
      map.put(new byte[]{i}, (int) i);
    }

    final Integer t = map.get(new byte[]{120});
    System.out.println(120 == t);
  }
}
