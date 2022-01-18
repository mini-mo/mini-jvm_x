package cls;

import core.MetaSpace;
import core.Resolver;
import java.io.File;
import java.io.FileInputStream;
import utils.Map;

public abstract class ClassLoader {

  private static Map<Clazz> bootClasses = new Map<>();
  private static Clazz javaLangClass;

  // only dir
  private static String[] paths;

  public static boolean init(String bootClassPaths) {
    paths = bootClassPaths.split(":");
    return true;
  }

  public static Clazz findSystemClass(String name) {
    var cache = bootClasses.get(name.getBytes());
    if (cache != null) {
      return cache;
    }

    var cls = loadSystemClass(name);
    bootClasses.put(name.getBytes(), cls);
    cls.offset = MetaSpace.registerClass(cls);
    return cls;
  }

  private static Clazz loadSystemClass(String name) {
    var cn = name.replace('.', '/');
    cn = cn.concat(".class");

    ClassFile cf = null;
    for (var path : paths) {
      var cp = path.concat("/").concat(cn);
      var f = new File(cp);
      if (!f.exists()) {
        continue;
      }
      try {
        var fis = new FileInputStream(f);
        var bytes = fis.readAllBytes();
        cf = ClassReader.read(bytes);
        break;
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalStateException();
      }
    }

    if (cf == null) {
      throw new IllegalStateException("class not found, ".concat(name));
    }

    var cls = defineClass(cf);

    // prepare

    // link

    // init
    return cls;
  }

  private static void prepareClass(Clazz cls) {

  }

  public static Clazz defineClass(ClassFile cf) {
    var cp = cf.cp;
    Clazz sprCls = null;
    var name = Resolver.className(cf.thisClass, cp);
    // super
    if (cf.superClass == 0) {
      if (!name.equals("java/lang/Object")) {
        throw new IllegalStateException();
      }
    } else {
      var sprcn = Resolver.className(cf.superClass, cp);
      sprCls = findSystemClass(sprcn);
    }

    var interfaces = new Clazz[cf.interfaces.length];
    for (int i = 0; i < cf.interfaces.length; i++) {
      interfaces[i] = findSystemClass(Resolver.className(cf.interfaces[i], cp));
    }

    var methods = new Method[cf.methods.length];
    for (int i = 0; i < cf.methods.length; i++) {
      var mi = cf.methods[i];
      var m = new Method();
      m.accessFlags = mi.accessFlags;
      m.name = Resolver.utf8(mi.nameIndex, cp);
      m.descriptor = Resolver.utf8(mi.descriptorIndex, cp);

      // Code
      for (var attribute : mi.attributes) {
        final var man = Resolver.utf8(attribute.attributeNameIndex, cp);
        if (man.equals("Code")) {
          var raw = attribute.info;
          var offset = 0;
          var ms = Resolver.u2(raw, offset);
          offset += 2;
          var ml = Resolver.u2(raw, offset);
          offset += 2;

          var clen = Resolver.u4(raw, offset);
          offset += 4;
          var code = Resolver.raw(raw, offset, clen);
          m.maxStacks = ms;
          m.maxLocals = ml;
          m.code = code;
        }
      }
      methods[i] = m;
    }

    // fields
    var size = 0;
    Field[] sprFields;
    if (sprCls == null) {
      sprFields = new Field[0];
    } else {
      size = sprCls.size;
      sprFields = sprCls.fields;
    }

    var fields = new Field[sprFields.length + cf.fields.length];
    var tfi = 0;
    for (int si = 0; si < sprFields.length; si++) {
      var sf = sprFields[si];
      fields[tfi++] = new Field(sf.name, sf.descriptor, sf.accessFlags, sf.offset);
    }
    for (int i = 0; i < cf.fields.length; i++) {
      var fi = cf.fields[i];
      var f = new Field();
      f.accessFlags = fi.accessFlags;
      f.name = Resolver.utf8(fi.nameIndex, cp);
      f.descriptor = Resolver.utf8(fi.descriptorIndex, cp);

      char ch = f.descriptor.charAt(0);
      switch (ch) {
        case 'I' -> {
          f.offset = size;
          size += 4;
        }
        case 'L' -> {
          f.offset = size;
          size += 4;
        }
        default -> {
          throw new IllegalStateException();
        }
      }
      fields[tfi++] = f;
    }

    return new Clazz(name, sprCls, interfaces, fields, methods, cp, size);
  }
}
