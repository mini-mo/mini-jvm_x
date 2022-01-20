package core;

import static core.Const.*;

import cls.ClassLoader;
import cls.Clazz;
import cls.Field;

public class Interpreter {

  /**
   * 解释执行字节码
   *
   * @return 返回方法的执行结果, 当返回结果为引用类型时，为引用类型的地址，其他类型为相对应的二进制对应的 long.
   */
  public static long executeJava() {
    var ee = Threads.getExecEnv();
    var frame = ee.current();

    var locals = frame.locals;
    var stacks = frame.stacks;
    var code = frame.code;
    var cp = frame.clazz.cp;
    var si = 0;

    var pc = 0;
    while (true) {
      if (pc >= code.length) {
        throw new IllegalStateException("unreachable code, %d %d".formatted(pc, code.length));
      }
      var op = code[pc++] & 0xff;
      // log
//      System.out.println("%d %d".formatted(pc - 1, op));
      // end
      switch (op) {
        case OPC_NOP -> {
        }
        case OPC_ACONST_NULL -> {
          stacks[si++] = 0;
        }
        case OPC_ICONST_M1 -> {
          stacks[si++] = -1;
        }
        case OPC_ICONST_0, OPC_ICONST_1, OPC_ICONST_2, OPC_ICONST_3, OPC_ICONST_4, OPC_ICONST_5 -> {
          stacks[si++] = op - OPC_ICONST_0;
        }
        case OPC_ILOAD -> {
          int idx = code[pc++] & 0xff;
          stacks[si++] = locals[idx];
        }
        case OPC_ILOAD_0, OPC_ILOAD_1, OPC_ILOAD_2, OPC_ILOAD_3 -> {
          stacks[si++] = locals[op - OPC_ILOAD_0];
        }
        case OPC_ISTORE -> {
          int idx = code[pc++] & 0xff;
          locals[idx] = stacks[--si];
        }
        case OPC_ISTORE_0, OPC_ISTORE_1, OPC_ISTORE_2, OPC_ISTORE_3 -> {
          locals[op - OPC_ISTORE_0] = stacks[--si];
        }
        case OPC_IADD -> {
          var tmp = stacks[--si] + stacks[--si];
          stacks[si++] = tmp;
        }
        case OPC_IDIV -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var tmp = v1 / v2;
          stacks[si++] = tmp;
        }
        case OPC_ISUB -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var tmp = v1 - v2;
          stacks[si++] = tmp;
        }
        case OPC_IMUL -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var tmp = v1 * v2;
          stacks[si++] = tmp;
        }
        case OPC_IREM -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var tmp = v1 % v2;
          stacks[si++] = tmp;
        }
        case OPC_IINC -> {
          var idx = Resolver.u1(code, pc);
          pc++;
          var step = Resolver.s1(code, pc);
          pc++;
          locals[idx] += step;
        }
        case OPC_IF_ICMPGE -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (v1 >= v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IF_ICMPGT -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (v1 > v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IRETURN -> {
          var tmp = stacks[--si];
          var old = ee.popFrame();
          pc = old.returnPc;

          if (ee.empty() || ee.current().dummy) {
            return tmp;
          }

          frame = ee.current();
          code = frame.code;
          cp = frame.clazz.cp;
          stacks = frame.stacks;
          locals = frame.locals;
          si = frame.si;

          stacks[si++] = tmp;
        }
        case OPC_RETURN -> {
          var old = ee.popFrame();
          pc = old.returnPc;

          if (ee.empty() || ee.current().dummy) {
            return 0;
          }

          frame = ee.current();
          code = frame.code;
          cp = frame.clazz.cp;
          stacks = frame.stacks;
          locals = frame.locals;
          si = frame.si;
        }
        case OPC_ASTORE_0, OPC_ASTORE_1, OPC_ASTORE_2, OPC_ASTORE_3 -> {
          locals[op - OPC_ASTORE_0] = stacks[--si];
        }
        case OPC_ALOAD_0, OPC_ALOAD_1, OPC_ALOAD_2, OPC_ALOAD_3 -> {
          stacks[si++] = locals[op - OPC_ALOAD_0];
        }
        case OPC_POP -> {
          si--;
        }
        case OPC_DUP -> {
          stacks[si++] = stacks[si - 1];
        }
        case OPC_NEW -> {
          var ci = Resolver.u2(code, pc);
          pc += 2;
          var cn = Resolver.className(ci, cp);
          var cls = ClassLoader.findSystemClass(cn);
          var point = Heap.malloc(cls.size);
          Heap.setInt(point, -8, cls.offset); // make relation from instance to class
          stacks[si++] = point;
        }
        case OPC_GOTO -> {
          var offset = Resolver.s2(code, pc);
          pc = pc + offset - 1;
        }

        case OPC_PUTSTATIC -> {
          int fi = Resolver.u2(code, pc);
          pc += 2;
          var ci = Resolver.u2(cp[fi].info);
          var ndi = Resolver.u2(cp[fi].info, 2);
          var cn = Resolver.className(ci, cp);

          Clazz cls = Resolver.resolveClass(cn);

          Field f = Resolver.resolveField(cls, Resolver.fieldName(ndi, cp), Resolver.fieldDescriptor(ndi, cp));
          Heap.setInt(cls.offset, f.offset, stacks[--si]);
        }
        case OPC_GETSTATIC -> {
          int fi = Resolver.u2(code, pc);
          pc += 2;
          var ci = Resolver.u2(cp[fi].info);
          var ndi = Resolver.u2(cp[fi].info, 2);
          var cn = Resolver.className(ci, cp);

          Clazz cls = Resolver.resolveClass(cn);
          Field f = Resolver.resolveField(cls, Resolver.fieldName(ndi, cp), Resolver.fieldDescriptor(ndi, cp));

          stacks[si++] = Heap.getInt(cls.offset, f.offset);
        }
        case OPC_PUTFIELD -> {
          var fi = Resolver.u2(code, pc);
          pc += 2;

          var ci = Resolver.u2(cp[fi].info);
          var ndi = Resolver.u2(cp[fi].info, 2);
          var cn = Resolver.className(ci, cp);
//          var cls = ClassLoader.findSystemClass(cn);

          var fn = Resolver.utf8(Resolver.u2(cp[ndi].info), cp);
          var fd = Resolver.utf8(Resolver.u2(cp[ndi].info, 2), cp);

          // TODO int
          var v = stacks[--si];
          var p = stacks[--si];

          var cls = MetaSpace.resolveClass(Heap.getInt(p, -8));
          var field = Resolver.resolveField(cls, fn, fd);

          Heap.setInt(p, field.offset, v);
        }

        case OPC_GETFIELD -> {
          var fi = Resolver.u2(code, pc);
          pc += 2;

          var ci = Resolver.u2(cp[fi].info);
          var ndi = Resolver.u2(cp[fi].info, 2);
          var cn = Resolver.className(ci, cp);
//          var cls = ClassLoader.findSystemClass(cn);

          var fn = Resolver.utf8(Resolver.u2(cp[ndi].info), cp);
          var fd = Resolver.utf8(Resolver.u2(cp[ndi].info, 2), cp);

          // TODO int
          var p = stacks[--si];

          var cls = MetaSpace.resolveClass(Heap.getInt(p, -8));
          var field = Resolver.resolveField(cls, fn, fd);

          var v = Heap.getInt(p, field.offset);
          stacks[si++] = v;
        }
        case OPC_INVOKEVIRTUAL -> {
          // TODO ...
          var ci = Resolver.u2(code, pc);
          pc += 2;

          var cn = Resolver.className(Resolver.u2(cp[ci].info), cp);
          var mn = Resolver.methodName(Resolver.u2(cp[ci].info, 2), cp);
          var mt = Resolver.methodDescriptor(Resolver.u2(cp[ci].info, 2), cp);

          var key = cn.concat("_").concat(mn).concat("_").concat(mt);
          var nm = MetaSpace.resolveNativeMethod(key.getBytes());
          if (nm != null) { // native
            si = nm.invoke(stacks, si);
            continue;
          }

          var ocs = ClassLoader.findSystemClass(cn);
          var om = Resolver.resolveMethod(ocs, mn, mt);
          int len = om.argsLength();

          int self = stacks[si - len];
          var cls = MetaSpace.resolveClass(Heap.getInt(self, -8));

          key = cls.name.concat("_").concat(mn).concat("_").concat(mt);
          nm = MetaSpace.resolveNativeMethod(key.getBytes());
          if (nm != null) { // native
            si = nm.invoke(stacks, si);
            continue;
          }

          var neo = Resolver.resolveMethod(cls, mn, mt);
          var old = frame;
          var nf = ee.createFrame(cls, neo);
          nf.returnPc = pc;

          frame = nf;
          code = frame.code;
          stacks = frame.stacks;
          locals = frame.locals;
          cp = frame.clazz.cp;

          // args
          si -= len;
          if (len > 0) {
            System.arraycopy(old.stacks, si, locals, 0, len);
          }
          old.si = si;

          si = 0;
          pc = 0;

          continue;
//          throw new IllegalStateException();
        }
        case OPC_INVOKESPECIAL -> {
          si--;
          pc += 2;
        }
        case OPC_INVOKESTATIC -> {
          var ci = Resolver.u2(code, pc);
          pc += 2;
          var cls = Resolver.className(Resolver.u2(cp[ci].info), cp);
          var mn = Resolver.methodName(Resolver.u2(cp[ci].info, 2), cp);
          var mt = Resolver.methodDescriptor(Resolver.u2(cp[ci].info, 2), cp);

          var key = cls.concat("_").concat(mn).concat("_").concat(mt);
          var nm = MetaSpace.resolveNativeMethod(key.getBytes());
          if (nm != null) { // native
            si = nm.invoke(stacks, si);
            continue;
          }

          // find method
          if (!cls.equals(frame.clazz.name)) {
            // TODO ...
            throw new IllegalStateException();
          }

          var old = frame;
          var neo = Resolver.resolveMethod(frame.clazz, mn, mt);
          var nf = ee.createFrame(old.clazz, neo);
          nf.returnPc = pc;

          frame = nf;
          code = frame.code;
          stacks = frame.stacks;
          locals = frame.locals;
          cp = frame.clazz.cp;

          // args
          var len = neo.argsLength();
          si -= len;
          if (len > 0) {
            System.arraycopy(old.stacks, si, locals, 0, len);
          }
          old.si = si;

          si = 0;
          pc = 0;

          continue;
//          throw new IllegalStateException();
        }
        default -> {
          throw new IllegalStateException("opc not impl: 0x%x %d".formatted(op, op));
        }
      }
    }
  }
}
