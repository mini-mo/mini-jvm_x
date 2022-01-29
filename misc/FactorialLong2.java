public class FactorialLong2 {

  public static long factorial(long n) {
    if (n <= 1L) {
      return 1L;
    }
    return n * factorial(n-1);
  }

  public static void main(long n) {
    System.out.println(factorial(n));
  }
}
