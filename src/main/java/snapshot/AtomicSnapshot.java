package snapshot;

import java.util.List;

public interface AtomicSnapshot<T> {
  /**
   * Identifies which thread took the snapshot.
   * 
   * @return the thread's unique identifier
   */
  long getThread();

  public List<T> getValues();

  public List<Integer> getStamps();
}
