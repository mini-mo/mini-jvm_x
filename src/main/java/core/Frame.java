package core;

public class Frame {

  int[] locals;
  int[] stacks;
  byte[] code;

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
