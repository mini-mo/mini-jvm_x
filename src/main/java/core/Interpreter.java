package core;

import static core.Const.*;

import cls.CpInfo;
import cls.Method;

public class Interpreter {

  public static void executeJava() {
    final ExecEnv ee = Threads.getExecEnv();
    Frame frame = ee.current();

    int[] locals = frame.locals;
    int[] stacks = frame.stacks;
    byte[] code = frame.code;
    CpInfo[] cp = frame.clazz.cp;
    int si = 0;

    int pc = 0;
    while (pc < code.length) {
      final int op = code[pc++] & 0xff;
      // log
//      System.out.println("%d %d".formatted(pc - 1, op));
      // end
      switch (op) {
        case OPC_ICONST_0 -> {
          stacks[si++] = 0;
        }
        case OPC_ICONST_1 -> {
          stacks[si++] = 1;
        }
        case OPC_ILOAD -> {
          int idx = code[pc++] & 0xff;
          stacks[si++] = locals[idx];
        }
        case OPC_ILOAD_0 -> {
          stacks[si++] = locals[0];
        }
        case OPC_ILOAD_1 -> {
          stacks[si++] = locals[1];
        }
        case OPC_ILOAD_2 -> {
          stacks[si++] = locals[2];
        }
        case OPC_ILOAD_3 -> {
          stacks[si++] = locals[3];
        }
        case OPC_ISTORE -> {
          int idx = code[pc++] & 0xff;
          locals[idx] = stacks[--si];
        }
        case OPC_ISTORE_0 -> {
          locals[0] = stacks[--si];
        }
        case OPC_ISTORE_1 -> {
          locals[1] = stacks[--si];
        }
        case OPC_ISTORE_2 -> {
          locals[2] = stacks[--si];
        }
        case OPC_ISTORE_3 -> {
          locals[3] = stacks[--si];
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
        case OPC_IMUL -> {
          final int v2 = stacks[--si];
          final int v1 = stacks[--si];
          var tmp = v1 * v2;
          stacks[si++] = tmp;
        }
        case OPC_IF_ICMPGT -> {
          final int v2 = stacks[--si];
          final int v1 = stacks[--si];
          final int next = Resolver.s2(code, pc);
          if (v1 > v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IRETURN -> {
          var tmp = stacks[--si];
          final Frame old = ee.popFrame();
          pc = old.returnPc;

          if (ee.empty()) {
            System.out.println(tmp);
            return;
          }

          frame = ee.current();
          code = frame.code;
          stacks = frame.stacks;
          locals = frame.locals;
          si = frame.si;

          stacks[si++] = tmp;
        }
        case OPC_RETURN -> {
          // TODO ...
          if (ee.empty()) {
            return;
          }
        }
        case OPC_INVOKESTATIC -> {
          final int ci = Resolver.u2(code, pc);
          pc += 2;
          final String cls = Resolver.className(Resolver.u2(cp[ci].info), cp);
          final String mn = Resolver.methodName(Resolver.u2(cp[ci].info, 2), cp);
          final String mt = Resolver.methodDescriptor(Resolver.u2(cp[ci].info, 2), cp);

          final String key = cls.concat("_").concat(mn).concat("_").concat(mt);
          final NativeMethod nm = MetaSpace.resolveNativeMethod(key.getBytes());
          if (nm != null) { // native
            si = nm.invoke(stacks, si - 1);
            continue;
          }

          // find method
          if (!cls.equals(frame.clazz.name)) {
            throw new IllegalStateException();
          }

          Frame old = frame;
          Method neo = Resolver.resolveMethod(frame.clazz, mn, mt);
          final Frame nf = ee.createFrame(old.clazz, neo);
          nf.returnPc = pc;

          frame = nf;
          code = frame.code;
          stacks = frame.stacks;
          locals = frame.locals;
          cp = frame.clazz.cp;

          // args
          locals[0] = old.stacks[--si];
          old.si = si;

          si = 0;
          pc = 0;

          continue;
//          throw new IllegalStateException();
        }
        default -> {
          System.out.println(op);
        }
      }
    }
  }
}
