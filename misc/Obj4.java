public class Obj4 extends Obj3 {
  int zz;

  public static void main() {
  
    var o = new Obj4();
    o.x = 1;
    o.y = 2;
    o.z = 3;
    o.zz = 4;

    RT.println(o.x);
    RT.println(o.y);
    RT.println(o.z);
    RT.println(o.zz);
  }
}
