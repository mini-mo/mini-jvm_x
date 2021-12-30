public class Obj2 extends Obj implements Obj2Interface1 {
  int ox;
  int oy;
  int oz;

  public static void main() {
    Obj2 o = new Obj2();
    o.x = 1;
    o.y = 2;
    o.ox = 3;
    o.oy = 4;
    o.oz = 5;

    RT.println(o.x);
    RT.println(o.y);
    RT.println(o.ox);
    RT.println(o.oy);
    RT.println(o.oz);
  }
}

interface Obj2Interface1 {
}

