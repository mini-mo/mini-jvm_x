package core;

public abstract class Natives {

  public static void init() {
    MetaSpace.registerNativeMethod("RT_println_(I)V".getBytes(), ((ostack, top) -> {
      System.out.println(ostack[top]);
      return top;
    }));

  }
}
