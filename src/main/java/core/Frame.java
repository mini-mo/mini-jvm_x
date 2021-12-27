package core;

import cls.Clazz;
import cls.Method;

public class Frame {

  public Clazz clazz;
  public Method method;

  public int[] locals;
  public int[] stacks;
  public int si;
  public byte[] code;

  public int returnPc;

  public Frame(){}
}
