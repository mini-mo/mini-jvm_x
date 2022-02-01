package core;

import utils.LongUtil;

public abstract class Natives {

  // 停表计时, ms
  private static long stopwatch;

  public static void init() {
    MetaSpace.registerNativeMethod("RT_println_(I)V".getBytes(), ((ostack, top) -> {
      System.out.println(ostack[top - 1]);
      return top - 1;
    }));

    MetaSpace.registerNativeMethod("RT_println_(J)V".getBytes(), ((ostack, top) -> {
      System.out.println(LongUtil.merge(ostack[top - 1], ostack[top - 2]));
      return top - 2;
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

    MetaSpace.registerNativeMethod("java/io/NOutStream_println_(J)V".getBytes(), ((ostack, top) -> {
      System.out.println(LongUtil.merge(ostack[top - 1], ostack[top - 2]));
      return top - 3;
    }));

    MetaSpace.registerNativeMethod("java/lang/Thread_start_()V".getBytes(), (((ostack, top) -> {
      Threads.createJavaThread(ostack[top - 1]);
      return top - 1;
    })));

    MetaSpace.registerNativeMethod("java/lang/Thread_sleep_(J)V".getBytes(), (((ostack, top) -> {
      long v = LongUtil.merge(ostack[top - 1], ostack[top - 2]);
      try {
        Thread.sleep(v);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return top - 2;
    })));

    MetaSpace.registerNativeMethod("java/lang/System_currentTimeMillis_()J".getBytes(), (((ostack, top) -> {
      long v = System.currentTimeMillis();
      int[] r = LongUtil.split(v);
      ostack[top] = r[0];
      ostack[top + 1] = r[1];
      return top + 2;
    })));
  }
}
