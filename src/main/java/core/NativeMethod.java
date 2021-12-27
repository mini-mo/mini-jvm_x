package core;

public interface NativeMethod {

  /**
   * 本地方法调用
   *
   * @param ostack 调用方操作数栈
   * @param top 调用参数起索引
   * @return 新的调用栈 top
   */
  int invoke(
      int[] ostack,
      int top
  );
}
