package range;

import java.util.concurrent.ThreadLocalRandom;

public final class IntegerRange implements Range<Integer> {

  private final int start;
  private final int end;

  /**
   * Range from start (inclusive) to end (exclusive)
   * 
   * @param start
   * @param end
   */
  public IntegerRange(int start, int end) {

    this.start = start;
    this.end = end;

  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  @Override
  public boolean contains(Integer x) {
    return x >= getStart() && x < getEnd();
  }

}
