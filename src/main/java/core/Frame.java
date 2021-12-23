package core;

public class Frame {

  public int[] locals;
  public int[] stacks;
  public byte[] code;

  public Frame(){}

  public Frame(
      int[] locals,
      int[] stacks,
      byte[] code
  ) {
    this.locals = locals;
    this.stacks = stacks;
    this.code = code;
  }
}
