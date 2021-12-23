package core;

import static core.Const.*;

public class Interpreter {

  public static void execute() {
    final ExecEnv ee = Threads.getExecEnv();
    final Frame frame = ee.current();

    int[] locals = frame.locals;
    int[] stacks = frame.stacks;
    byte[] code = frame.code;
    int si = 0;

    int pc = 0;
    while (pc < code.length) {
      final int op = code[pc++] & 0xff;
      switch (op) {
        case OPC_ILOAD_0 -> {
          stacks[si++] = locals[0];
        }
        case OPC_ILOAD_1 -> {
          stacks[si++] = locals[1];
        }
        case OPC_IADD -> {
          var tmp = stacks[--si] + stacks[--si];
          stacks[si++] = tmp;
        }
        case OPC_ISUB -> {
          final int v2 = stacks[--si];
          final int v1 = stacks[--si];
          var tmp = v1 - v2;
          stacks[si++] = tmp;
        }
        case OPC_IRETURN -> {
          var tmp = stacks[--si];
          System.out.println(tmp);
        }
        default -> {
          System.out.println(op);
        }
      }
    }
  }
}
