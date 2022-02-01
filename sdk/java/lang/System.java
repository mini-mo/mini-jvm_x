package java.lang;
public class System {

  public static java.io.PrintStream out = new java.io.NOutStream();

  public static native long currentTimeMillis();
}
