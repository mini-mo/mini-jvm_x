package cls;

public class CpInfo {

  public final int tag;
  public final byte[] info;

  public CpInfo(
      int tag,
      byte[] info
  ) {
    this.tag = tag;
    this.info = info;
  }
}