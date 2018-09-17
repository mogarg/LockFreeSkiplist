package range;

public  interface Range<T> {

  /**
   * Returns true if x is in the range.
   * 
   * @param x
   * 
   * @return true if x is in the range
   */
  public boolean contains(T x);

}
