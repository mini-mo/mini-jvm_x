public class Hello2 {

  public static void add(int x, int y) {
    int r = x + y;
    RT.println(r);
  }

  public static void sub(int x, int y) {
    int r = x - y;
    RT.println(r);
  }

  public static void add_add_sub(int x, int y) {
    int ar = x + y;
    RT.println(ar);

    int sr = x - y;
    RT.println(sr);

    int r = ar + sr;
    RT.println(r);
  }

  public static void main(int x , int y) {
    add(x, y);
    sub(x, y);
    add_add_sub(x, y);
  }

}
