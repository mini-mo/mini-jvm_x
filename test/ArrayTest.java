import utils.Array;

public class ArrayTest {

  public static void main(String[] args) {

    var array = new Array<Integer>();

    for (int i = 0; i < 10; i++) {
      array.append(i);
    }

    for (int i = 0; i < 10; i++) {
      System.out.println(i == array.get(i));
    }
  }
}