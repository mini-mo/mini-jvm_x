package core;

import static core.Const.*;

import cls.ClassLoader;
import cls.Clazz;
import cls.Field;
import utils.LongUtil;

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
      if (ee.status != THREAD_RUNNING) {
        // 空转
        continue;
      }
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
        case OPC_LCONST_0 -> {
          int[] r = LongUtil.split(0L);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
        }
        case OPC_LCONST_1 -> {
          int[] r = LongUtil.split(1L);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
        }
        case OPC_BIPUSH -> {
          int x = Resolver.s1(code, pc++);
          stacks[si++] = x;
        }
        case OPC_SIPUSH -> {
          int x = Resolver.c2(code, pc);
          pc += 2;
          stacks[si++] = x;
        }
        case OPC_LDC2_W -> {
          int ldwIdx = Resolver.u2(code, pc);
          pc += 2;
          var cpInfo = cp[ldwIdx];
          switch (cpInfo.tag) {
            case CONSTANT_Long, CONSTANT_Double -> {
              stacks[si++] = Resolver.u4(cpInfo.info);
              stacks[si++] = Resolver.u4(cpInfo.info, 4);
            }
            default -> throw new IllegalStateException();
          }
        }
        case OPC_ILOAD -> {
          int idx = code[pc++] & 0xff;
          stacks[si++] = locals[idx];
        }
        case OPC_LLOAD -> {
          int idx = code[pc++] & 0xff;
          stacks[si++] = locals[idx];
          stacks[si++] = locals[idx + 1];
        }
        case OPC_ILOAD_0, OPC_ILOAD_1, OPC_ILOAD_2, OPC_ILOAD_3 -> {
          stacks[si++] = locals[op - OPC_ILOAD_0];
        }
        case OPC_ISTORE -> {
          int idx = code[pc++] & 0xff;
          locals[idx] = stacks[--si];
        }
        case OPC_LSTORE -> {
          int idx = code[pc++] & 0xff;
          locals[idx + 1] = stacks[--si];
          locals[idx] = stacks[--si];
        }
        case OPC_ISTORE_0, OPC_ISTORE_1, OPC_ISTORE_2, OPC_ISTORE_3 -> {
          locals[op - OPC_ISTORE_0] = stacks[--si];
        }
        case OPC_LSTORE_0, OPC_LSTORE_1, OPC_LSTORE_2, OPC_LSTORE_3 -> {
          int idx = op - OPC_LSTORE_0;
          locals[idx + 1] = stacks[--si];
          locals[idx] = stacks[--si];
        }
        case OPC_IADD -> {
          var tmp = stacks[--si] + stacks[--si];
          stacks[si++] = tmp;
        }
        case OPC_LADD -> {
          var tmp1 = LongUtil.merge(stacks[--si], stacks[--si]);
          var tmp2 = LongUtil.merge(stacks[--si], stacks[--si]);
          int[] r = LongUtil.split(tmp1 + tmp2);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
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
        case OPC_LSUB -> {
          var v2 = LongUtil.merge(stacks[--si], stacks[--si]);
          var v1 = LongUtil.merge(stacks[--si], stacks[--si]);
          var tmp = v1 - v2;
          int[] r = LongUtil.split(tmp);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
        }
        case OPC_IMUL -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var tmp = v1 * v2;
          stacks[si++] = tmp;
        }
        case OPC_LMUL -> {
          var v2 = LongUtil.merge(stacks[--si], stacks[--si]);
          var v1 = LongUtil.merge(stacks[--si], stacks[--si]);
          var tmp = v1 * v2;
          int[] r = LongUtil.split(tmp);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
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
        case OPC_LCMP -> {
          long v2 = LongUtil.merge(stacks[--si], stacks[--si]);
          long v1 = LongUtil.merge(stacks[--si], stacks[--si]);
          if (v1 == v2) {
            stacks[si++] = 0;
          } else if (v1 < v2) {
            stacks[si++] = -1;
          } else {
            stacks[si++] = 1;
          }
        }
        case OPC_IFGT -> {
          int tmp = stacks[--si];
          if (tmp > 0) {
            var next = Resolver.s2(code, pc);
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
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
        case OPC_IF_ICMPEQ -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (v1 == v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IF_ICMPNE -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (v1 != v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IF_ICMPLT -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (v1 < v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IF_ICMPLE -> {
          var v2 = stacks[--si];
          var v1 = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (v1 <= v2) {
            pc = pc + next - 1;
            continue;
          }
          pc += 2;
        }
        case OPC_IFEQ -> {
          var tmp = stacks[--si];
          var next = Resolver.s2(code, pc);
          if (tmp == 0) {
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
        case OPC_LRETURN -> {
          var tmp = LongUtil.merge(stacks[--si], stacks[--si]);
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

          int[] r = LongUtil.split(tmp);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
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
        case OPC_IASTORE -> {
          int val = stacks[--si];
          int idx = stacks[--si];
          int point = stacks[--si];
          Heap.setInt(point, 4 + (idx * 4), val);
        }
        case OPC_LASTORE -> {
          long val = LongUtil.merge(stacks[--si], stacks[--si]);
          int idx = stacks[--si];
          int point = stacks[--si];
          Heap.setLong(point, 4 + (idx * 8), val);
        }
        case OPC_LLOAD_0, OPC_LLOAD_1, OPC_LLOAD_2, OPC_LLOAD_3 -> {
          stacks[si++] = locals[op - OPC_LLOAD_0];
          stacks[si++] = locals[op - OPC_LLOAD_0 + 1];
        }
        case OPC_ALOAD_0, OPC_ALOAD_1, OPC_ALOAD_2, OPC_ALOAD_3 -> {
          stacks[si++] = locals[op - OPC_ALOAD_0];
        }
        case OPC_IALOAD -> {
          int idx = stacks[--si];
          int point = stacks[--si];
          int val = Heap.getInt(point, 4 + (idx * 4));
          stacks[si++] = val;
        }
        case OPC_LALOAD -> {
          int idx = stacks[--si];
          int point = stacks[--si];
          long val = Heap.getLong(point, 4 + (idx * 8));
          int[] r = LongUtil.split(val);
          stacks[si++] = r[0];
          stacks[si++] = r[1];
        }
        case OPC_POP -> {
          si--;
        }
        case OPC_DUP -> {
          int tmp = stacks[si - 1];
          stacks[si++] = tmp;
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
        case OPC_NEWARRAY -> {
          int count = stacks[--si];
          if (count < 0) {
            throw new NegativeArraySizeException();
          }
          var type = Resolver.u1(code, pc);
          pc += 1;
          int size = switch (type) {
            case 4 ->
                //BOOLEAN
                count;
            case 5 ->
                //CHAR
                count * 2;
            case 6 ->
                //FLOAT
                count * 4;
            case 7 ->
                //DOUBLE
                count * 8;
            case 8 ->
                //BYTE
                count;
            case 9 ->
                //SHORT
                count * 2;
            case 10 ->
                //INT
                count * 4;
            case 11 ->
                //LONG
                count * 8;
            default -> throw new IllegalStateException();
          };
          int point = Heap.malloc(size);
          Heap.setInt(point, 0, count);
          stacks[si++] = point;
        }
        case OPC_ARRAYLENGTH -> {
          int point = stacks[--si];
          if (point == 0) {
            throw new NullPointerException();
          }
          int length = Heap.getInt(point, 0);
          stacks[si++] = length;
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
          f.value = stacks[--si];
        }
        case OPC_GETSTATIC -> {
          int fi = Resolver.u2(code, pc);
          pc += 2;
          var ci = Resolver.u2(cp[fi].info);
          var ndi = Resolver.u2(cp[fi].info, 2);
          var cn = Resolver.className(ci, cp);

          Clazz cls = Resolver.resolveClass(cn);
          Field f = Resolver.resolveField(cls, Resolver.fieldName(ndi, cp), Resolver.fieldDescriptor(ndi, cp));

          stacks[si++] = (int) f.value;
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
          if (neo == null) {
            throw new IllegalStateException("nosuchmethod " + mn);
          }

          var clz = MetaSpace.resolveClass(neo.cls);
          key = clz.name.concat("_").concat(mn).concat("_").concat(mt);
          nm = MetaSpace.resolveNativeMethod(key.getBytes());
          if (nm != null) { // native
            si = nm.invoke(stacks, si);
            continue;
          }

          var old = frame;
          var nf = ee.createFrame(clz, neo);
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

          Clazz clz = Resolver.resolveClass(cls);

          var old = frame;
          var neo = Resolver.resolveMethod(clz, mn, mt);
          if (neo == null) {
            throw new IllegalStateException("nosuchmethod " + mn);
          }
          var nf = ee.createFrame(MetaSpace.resolveClass(neo.cls), neo);
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
        }
        case OPC_INVOKESTATIC -> {
          var ci = Resolver.u2(code, pc);
          pc += 2;
          var cls = Resolver.className(Resolver.u2(cp[ci].info), cp);
          var mn = Resolver.methodName(Resolver.u2(cp[ci].info, 2), cp);
          var mt = Resolver.methodDescriptor(Resolver.u2(cp[ci].info, 2), cp);

          // find method
          Clazz clz = Resolver.resolveClass(cls);

          var old = frame;
          var neo = Resolver.resolveMethod(clz, mn, mt);
          if (neo == null) {
            throw new IllegalStateException("nosuchmethod " + mn);
          }

          if (Flags.isAccNative(neo.accessFlags)) {
            var key = cls.concat("_").concat(mn).concat("_").concat(mt);
            var nm = MetaSpace.resolveNativeMethod(key.getBytes());
            if (nm == null) {
              throw new IllegalStateException("missing " + key + " native method");
            }
            si = nm.invoke(stacks, si);
            continue;
          }

          var nf = ee.createFrame(MetaSpace.resolveClass(neo.cls), neo);
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
        case OPC_INVOKEINTERFACE -> {
          var ci = Resolver.u2(code, pc);
          pc += 2;
          // code 0
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

          var neo = Resolver.resolveIMethod(cls, mn, mt);
          if (neo == null) {
            throw new IllegalStateException("nosuchmethod " + mn);
          }
          var old = frame;
          var nf = ee.createFrame(MetaSpace.resolveClass(neo.cls), neo);
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
        default -> {
          throw new IllegalStateException("opc not impl: 0x%x %d".formatted(op, op));
        }
      }
    }
  }
}
