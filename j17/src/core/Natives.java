package core;

public abstract class Natives {

  // 停表计时, ms
  private static long stopwatch;

  public static void init() {
    MetaSpace.registerNativeMethod("RT_println_(I)V".getBytes(), ((ostack, top) -> {
      System.out.println(ostack[top - 1]);
      return top - 1;
    }));

    MetaSpace.registerNativeMethod("RT_begin_()V".getBytes(), ((ostack, top) -> {
      stopwatch = System.currentTimeMillis();
      return top;
    }));

    MetaSpace.registerNativeMethod("RT_end_()V".getBytes(), ((ostack, top) -> {
      long tmp = System.currentTimeMillis();
      System.err.println(tmp - stopwatch);
      stopwatch = tmp;
      return top;
    }));

    MetaSpace.registerNativeMethod("java/io/NOutStream_println_(I)V".getBytes(), ((ostack, top) -> {
      System.out.println(ostack[top - 1]);
      return top - 2;
    }));

    MetaSpace.registerNativeMethod("java/lang/Thread_start_()V".getBytes(), (((ostack, top) -> {
      Threads.createJavaThread(ostack[top - 1]);
      return top - 1;
    })));
  }
}
