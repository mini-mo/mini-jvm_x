public class Hello3 {
  public static void main(int x, int y) {
    RT.println(add_x(x, y));
  }

  public static int add_x(int x, int y) {
    return div(x, y) + multiply(x, y);
  }

  public static int multiply(int x, int y) {
    return x * y;
  }

  public static int div(int x, int y) {
    return x / y;
  }
}
