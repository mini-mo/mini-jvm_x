package utils;

/**
 * Array 精简实现
 *
 */
public class Array<T> {

  private Object[] entries = null;
  private int len;
  private int size;

  public Array() {
    len = 2;
    size = 0;
    entries = new Object[2];
  }

  private void resize() {
    if (len > size) {
      return;
    }
    var ni = len * 2;
    var neo = new Object[ni];
    System.arraycopy(entries, 0, neo, 0, len);
    entries = neo;
    len = ni;
  }

  public T get(int index) {
    return ((T) entries[index]);
  }

  public int append(T val) {
    resize();
    entries[size++] = val;
    return size - 1;
  }
}
