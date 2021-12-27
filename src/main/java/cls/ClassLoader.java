package cls;

import core.Resolver;

public class ClassLoader {

  // tmp
  public static Clazz load(ClassFile cf) {
    Method[] methods = new Method[cf.methods.length];
    CpInfo[] cp = cf.cp;
    for (int i = 0; i < cf.methods.length; i++) {
      MethodInfo mi = cf.methods[i];
      Method m = new Method();
      m.accessFlags = mi.accessFlags;
      m.name = Resolver.utf8(mi.nameIndex, cp);
      m.descriptor = Resolver.utf8(mi.descriptorIndex, cp);

      // Code
      for (var attribute : mi.attributes) {
        final String name = Resolver.utf8(attribute.attributeNameIndex, cp);
        if (name.equals("Code")) {
          var raw = attribute.info;
          var offset = 0;
          var ms = Resolver.u2(raw, offset);
          offset += 2;
          var ml = Resolver.u2(raw, offset);
          offset += 2;

          var clen = Resolver.u4(raw, offset);
          offset += 4;
          byte[] code = Resolver.raw(raw, offset, clen);
          m.maxStacks = ms;
          m.maxLocals = ml;
          m.code = code;
        }
      }
      methods[i] = m;
    }
    return new Clazz(Resolver.className(cf.thisClass, cp), methods, cp);
  }
}
