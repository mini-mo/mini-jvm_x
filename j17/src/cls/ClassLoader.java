package cls;

import core.Const;
import core.Flags;
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

    // prepare

    // link
    linkClass(cls);

    // init
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

    return defineClass(cf);
  }

  private static void linkClass(Clazz cls) {
    if (cls.state >= Const.CLASS_LINKED) {
      return;
    }
    Clazz sprCls = cls.spr;
    if (sprCls != null) {
      if (sprCls.state < Const.CLASS_LINKED) {
        linkClass(sprCls); // link super
      }
    }

    var size = 0;
    Field[] sprFields;
    if (sprCls == null) {
      sprFields = new Field[0];
    } else {
      size = sprCls.size;
      sprFields = sprCls.fields;
    }

    var fields = new Field[sprFields.length + cls.fields.length];
    var tfi = 0;
    for (int si = 0; si < sprFields.length; si++) {
      var sf = sprFields[si];
      var f = new Field(sf.name, sf.descriptor, sf.accessFlags, sf.offset);
      f.cls = cls.offset;
      fields[tfi++] = f;
    }
    System.arraycopy(cls.fields, 0, fields, sprFields.length, cls.fields.length);

    for (int i = 0; i < cls.fields.length; i++) {
      var f = cls.fields[i];
      char ch = f.descriptor.charAt(0);
      switch (ch) {
        case 'I', 'L' -> {
          f.offset = size;
          f.cls = cls.offset;
          size += 4;
        }
        default -> {
          throw new IllegalStateException();
        }
      }
    }
    cls.size = size;
    cls.fields = fields;

    // methods
    Method[] sm = sprCls == null ? new Method[0] : sprCls.methods;
    Method[] methods = new Method[sm.length + cls.methods.length];
    var len = 0;
    for (var i = 0; i < sm.length; i++) {
      var smi = sm[i];
      if (smi.name.startsWith("<") || Flags.isAccStatic(smi.accessFlags) || Flags.isAccPrivate(smi.accessFlags)) {
        continue;
      }
      var m = new Method();
      m.accessFlags = smi.accessFlags;
      m.name = smi.name;
      m.descriptor = smi.descriptor;
      m.code = smi.code;
      m.maxLocals = smi.maxLocals;
      m.maxStacks = smi.maxStacks;
      m.cls = smi.cls;
      m.offset = len;
      methods[len] = m;
      len++;
    }

    int sl = len;
    for (var i = 0; i < cls.methods.length; i++){
      var mi = cls.methods[i];
      var m = new Method();
      m.accessFlags = mi.accessFlags;
      m.name = mi.name;
      m.descriptor = mi.descriptor;
      m.code = mi.code;
      m.maxLocals = mi.maxLocals;
      m.maxStacks = mi.maxStacks;
      m.cls = cls.offset;
      m.offset = len;

      if (mi.name.startsWith("<") || Flags.isAccStatic(mi.accessFlags) || Flags.isAccPrivate(mi.accessFlags)) {
        methods[len] = m;
        len++;
        continue;
      }

      // 重写了父类方法？
      boolean override = false;
      for (var j = 0; j < sl; j++) {
        var tm = methods[j];
        if (tm.name.equals(m.name) && tm.descriptor.equals(m.descriptor) /* TODO access */) {
          methods[j] = m;
          override = true;
          break;
        }
      }
      if (!override) {
        methods[len] = m;
        len++;
      }
    }
    if (len < methods.length) {
      var tmp = new Method[len];
      System.arraycopy(methods, 0, tmp, 0, len);
      methods = tmp;
    }
    cls.methods = methods;


    // interface methods

    cls.state = Const.CLASS_LINKED;
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
    Field[] fields = new Field[cf.fields.length];
    for (int i = 0; i < fields.length; i++) {
      var fi = cf.fields[i];
      var f = new Field();
      f.accessFlags = fi.accessFlags;
      f.name = Resolver.utf8(fi.nameIndex, cp);
      f.descriptor = Resolver.utf8(fi.descriptorIndex, cp);
      fields[i] = f;
    }

    return new Clazz(name, sprCls, interfaces, fields, methods, cp);
  }
}
