public class Array {

  public static void main() {
    var arr = new int[] {1, 2, 3, 4, 5};
    RT.println(arr.length);
    for(var val : arr) {
      RT.println(val);
    }
  }
}
