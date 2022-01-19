public class Impl3 extends Impl2 {

  public int i2(){return 4;}

  public static void main() {
    Int2 i2 = new Impl3();
    System.out.println(i2.i2());
  }

  public static void main(String ...args) {
    Int2 i2 = new Impl3();
    System.out.println(i2.i2());
  }
}
