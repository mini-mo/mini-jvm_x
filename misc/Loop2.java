public class Loop2 {

  public static int loop(int n) {

    int sum = 0;
    for (int i = 0; i <= n; i++) {
      sum += i;
    }
    return sum;
  }

  public static void main(int n) {
    loop(n);
    loop(n);

    RT.begin();

    RT.println(loop(n));

    RT.end();
  }
}
