package benchmark;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.analysis.function.Min;
import java.lang.Math.*;
import range.IntegerRange;

public class OperationGenerator {

  public enum OPERATION {
    ADD, REMOVE, CONTAINS, RANGE
  };

  private ThreadLocalRandom random;
  private double addRange;
  private double removeRange;
  private double containsRange;
  private int maxValue;
  private int nextVal;


  private List c = new ArrayList(4);

  public OperationGenerator(int maxValue) {
    this.random = ThreadLocalRandom.current();
    this.addRange = 0.25;
    this.removeRange = 0.5;
    this.containsRange = 0.75;
    this.maxValue = maxValue;

  }

  public OperationGenerator(double addChance, double removeChance, double containsChance, int maxValue) {
    this.random = ThreadLocalRandom.current();
    this.addRange = addChance;
    this.removeRange = this.addRange + removeChance;
    this.containsRange = this.removeRange + containsChance;
    this.maxValue = maxValue;
   }

  public OPERATION nextOperation() {
    double choice = random.nextDouble();

    if (choice < addRange) {
      return OPERATION.ADD;
    }

    if (choice < removeRange) {
      return OPERATION.REMOVE;
    }

    if (choice < containsRange) {
      return OPERATION.CONTAINS;
    }

    return OPERATION.RANGE;
  }

  public OPERATION nextOperation(double addChance, double removeChance, double containsChance) {
    double choice = random.nextDouble();

    if (choice < addChance) {
      return OPERATION.ADD;
    }

    if (choice < addChance + removeChance) {
      return OPERATION.REMOVE;
    }

    if (choice < addChance + removeChance + containsChance) {
      return OPERATION.CONTAINS;
    }

    return OPERATION.RANGE;
  }

  public int nextValue() {
    return random.nextInt(maxValue);
  }

//  public IntegerRange nextRange() {
//    nextVal = random.nextInt(maxValue/2);
//    return new IntegerRange(nextVal, maxValue-nextVal);
//  }

  public IntegerRange nextRange() {

      nextVal = random.nextInt(25);
      return new IntegerRange(maxValue/2-nextVal, maxValue/2+nextVal);
  }
}