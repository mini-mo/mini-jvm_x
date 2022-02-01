public class ArrayLong {

  public static void main() {
    var arr = new long[] {1L, 2L, 3L, 4L, 5L};
    RT.println(arr.length);
    for(var val : arr) {
      RT.println(val);
    }
  }
}
