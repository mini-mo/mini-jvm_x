public class Obj3 extends Obj {

  int x ;
  int z ;

  public static void main() {
    var o = new Obj3();
    o.x = 1;
    o.y = 2;
    o.z = 3;

    RT.println(o.x);
    RT.println(o.y);
    RT.println(o.z);
  }
}
