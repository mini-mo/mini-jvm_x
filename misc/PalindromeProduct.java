public class PalindromeProduct {
  public static void main(String[] args) {
    int maxPalindrome = 0;
    for (int i = 100; i <= 999; i++) {
      for (int j = 100; j <= 999; j++) {
        int product = i * j;

        int n = product;
        int reversed = 0;
        while (n != 0) {
            reversed = reversed * 10 + n % 10;
            n /= 10;
        }

        if (product > maxPalindrome && product == reversed) {
          maxPalindrome = product;
        }
      }
    }
    System.out.println(maxPalindrome);
  }
}
