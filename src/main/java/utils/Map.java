package utils;

/**
 * Map 精简实现
 *
 * bytes -> Object
 *
 * array + chain
 */
public class Map<T> {

  private Entry<T>[] entries = null;
  private int size = 0;
  private float capacity = 0.75f;

  public Map() {
    entries = new Entry[4];
  }

  public void put(
      byte[] key,
      T val
  ) {
    if (key == null) {
      return;
    }
    resize();

    put0(key, val);
  }

  public void put0(
      byte[] key,
      T val
  ) {
    int hash = hash(key);
    int i = hash % entries.length;
    if (entries[i] == null) {
      size++;
      entries[i] = new Entry(key, val);
      return;
    }
    // hash 冲突
    Entry entry = entries[i];
    while (true) {
      if (equals(entry.key, key)) { // equals
        // replace
        entry.key = key;
        entry.data = val;
        return;
      }
      if (entry.next == null) {
        break;
      }
      entry = entry.next;
    }

    // 不在链中
    size++;
    entry.next = new Entry(key, val);
    return;
  }

  private void resize() {
    if (entries.length * capacity > size) {
      return;
    }
    Entry<T>[] old = entries;
    entries = new Entry[entries.length * 2];
    for (Entry<T> entry : old) {
      Entry<T> cur = entry;
      while (cur != null) {
        put(cur.key, cur.data);
        cur = cur.next;
      }
    }
  }

  public T get(byte[] key) {
    if (key == null) {
      return null;
    }
    int hash = hash(key);
    int i = hash % entries.length;
    Entry<T> entry = entries[i];
    if (entry == null) {
      return null;
    }
    while (true) {
      if (equals(key, entry.key)) { // found
        return (T) entry.data;
      }
      if (entry.next == null) {
        break;
      }
      entry = entry.next;
    }

    // not found
    return null;
  }

  private boolean equals(
      byte[] a,
      byte[] b
  ) {
    if (a == b) {
      return true;
    }
    if (a.length != b.length) {
      return false;
    }
    for (int i = 0; i < a.length; i++) {
      if (a[i] != b[i]) {
        return false;
      }
    }
    return true;
  }

  private int hash(byte[] key) {
    int hash = 0;
    for (byte b : key) {
      hash = hash * 37 + ((int) b) & 0xff;
    }
    return hash;
  }

  static class Entry<T> {

    byte[] key;
    T data;

    Entry<T> next;

    Entry(
        byte[] key,
        T data
    ) {
      this.key = key;
      this.data = data;
    }
  }
}
