package skiplist;

import java.util.ArrayList;
import java.util.List;

import snapshot.AtomicSnapshot;

public class SkipListSnapshot<T> implements AtomicSnapshot<T> {

  private long threadId;
  private ArrayList<T> values;
  private ArrayList<Integer> keys;
  private ArrayList<Integer> stamps;

  public SkipListSnapshot() {
    this.threadId = Thread.currentThread().getId();
    this.values = new ArrayList<>();
    this.keys = new ArrayList<>();
    this.stamps = new ArrayList<>();
  }

  @Override
  public long getThread() {
    return threadId;
  }

  @Override
  public List<T> getValues() {
    return values;
  }

  @Override
  public List<Integer> getStamps() {
    return stamps;
  }

  public List<Integer> getKeys() {
    return keys;
  }

  public boolean equals(SkipListSnapshot<T> snap) {
    List<Integer> snapKeys = snap.getKeys();
    List<Integer> snapStamps = snap.getStamps();

    if (snapKeys.size() != keys.size() || snapStamps.size() != stamps.size()) {
      return false;
    }

    for (int i = 0; i < keys.size(); i++) {
      if (!snapKeys.get(i).equals(keys.get(i))
          || !snapStamps.get(i).equals(stamps.get(i))) {
        return false;
      }
    }

    return true;
  }

  public void addValue(T value, int key, int stamp) {
    values.add(value);
    keys.add(key);
    stamps.add(stamp);
  }

}
