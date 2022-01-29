class ObjectInitTest1 {
  {
    System.out.println(1);
  }

  ObjectInitTest1() {
    System.out.println(2);
  }

  public static void main(String[] args) {
    new ObjectInitTest1();
  }
}
class ObjectInitTest2 extends ObjectInitTest1 {
  {
    System.out.println(3);
  }

  ObjectInitTest2() {
    System.out.println(4);
  }

  public static void main(String[] args) {
    new ObjectInitTest2();
  }
}
class ObjectInitTest3 extends ObjectInitTest2 {
  {
    System.out.println(3);
  }

  ObjectInitTest3() {
    System.out.println(2);
  }

  public static void main(String[] args) {
    new ObjectInitTest3();
  }
}
