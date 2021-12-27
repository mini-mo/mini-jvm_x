package core;

/**
 * 执行环境, 绑定至线程
 */
public class ExecEnv {

  // 调用栈
  private Frame[] stack;
  private int top = 0;

  public ExecEnv() {
    stack = new Frame[128]; // default 128
  }

  public Frame current() {
    return stack[top - 1];
  }

  public Frame createFrame() {
    ensure();
    final Frame nf = new Frame(null, null, null, null);
    stack[top++] = nf;
    return nf;
  }

  public Frame popFrame() {
    Frame old = stack[top - 1];
    stack[--top] = null;
    return old;
  }

  public boolean empty() {
    return top == 0;
  }

  private void ensure() {
    if (top >= stack.length) { // need grow
      stack = new Frame[stack.length * 2];
    }
  }

}
