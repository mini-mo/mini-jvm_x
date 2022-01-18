public class Loop3 {

  public static int loop(int n) {

    int sum = 0;
    for (int i = 0; i <= n; i++) {
      sum += i;
    }
    return sum;
  }

  public static void main(int n) {
    System.out.println(loop(n));
  }

  public static void main(String ...args) {
    System.out.println(loop(Integer.parseInt(args[0])));
  }
}
