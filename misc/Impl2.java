public class Impl2 implements Int2 {

  public int i1(){return 1;}
  public int i2(){return 2;}
  public int i3(){return 3;}

  public static void main() {
    Int2 i2 = new Impl2();
    System.out.println(i2.i3());
  }

  public static void main(String ...args) {
    Int2 i2 = new Impl2();
    System.out.println(i2.i3());
  }
}
