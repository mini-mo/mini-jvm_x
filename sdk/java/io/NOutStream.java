package java.io;

public class NOutStream implements PrintStream {

  public native void println(int value);

  public native void println(long value);
}
