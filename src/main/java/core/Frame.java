package core;

import cls.CpInfo;

public class Frame {

  public int[] locals;
  public int[] stacks;
  public int si;

  public byte[] code;
  public CpInfo[] cp;

  public int returnPc;

  public Frame(){}

  public Frame(
      int[] locals,
      int[] stacks,
      byte[] code,
      CpInfo[] cp
  ) {
    this.locals = locals;
    this.stacks = stacks;
    this.code = code;
    this.cp = cp;
  }
}
