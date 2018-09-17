package skiplist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicStampedReference;

import range.Range;

public class RSkipList<T> {
  private static final int MARKED_STAMP = Integer.MAX_VALUE;

  private final int MAX_LEVEL;
  private final Node<T> head;
  private final Node<T> tail;
  private ThreadLocalRandom random;
  private SkipListSnapshot<T> lastSnapshot;

  public RSkipList(int maxLevel) {
    this.MAX_LEVEL = maxLevel;

    this.head = new Node<T>(Integer.MIN_VALUE);
    this.tail = new Node<T>(Integer.MAX_VALUE);

    for (int i = 0; i < MAX_LEVEL + 1; i++) {
      head.next[i] = new AtomicStampedReference<>(tail, getFirstStamp());
    }

    random = ThreadLocalRandom.current();
  }

  public boolean add(T x) {
    int topLevel = randomLevel();
    int bottomLevel = 0;

    // initialize the arrays
    ArrayList<Node<T>> preds = new ArrayList<>(MAX_LEVEL + 1);
    ArrayList<Node<T>> succs = new ArrayList<>(MAX_LEVEL + 1);
    for (int i = 0; i < MAX_LEVEL + 1; i++) {
      preds.add(null);
      succs.add(null);
    }

    while (true) {
      boolean found = find(x, preds, succs);
      if (found) {
        return false;
      } else {
        Node<T> newNode = new Node<>(x, topLevel);

        // setting new node next pointers
        for (int level = bottomLevel; level <= topLevel; level++) {
          Node<T> succ = succs.get(level);
          newNode.next[level].set(succ, getFirstStamp());
        }

        Node<T> pred = preds.get(bottomLevel);
        Node<T> succ = succs.get(bottomLevel);
        int oldStamp = pred.next[bottomLevel].getStamp();
        if (!pred.next[bottomLevel].compareAndSet(succ, newNode, oldStamp, getNextStamp(oldStamp))) {
          continue; // we couldn't add the node at the bottom level, so we need
                    // to start over
        }

        lastSnapshot = collect(); // update the last snapshot

        for (int level = bottomLevel + 1; level <= topLevel; level++) {
          while (true) {
            pred = preds.get(level);
            succ = succs.get(level);
            oldStamp = pred.next[level].getStamp();
            if (pred.next[level].compareAndSet(succ, newNode, oldStamp, getNextStamp(oldStamp))) {
              break; // we were able to add this level in, so proceed to next
                     // step of loop
            }

            find(x, preds, succs); // we couldn't add to this level, so we need
                                   // to refresh the preds and succs
          }
        }

        return true;
      }
    }
  }

  public boolean remove(T x) {
    int bottomLevel = 0;
    Node<T> succ;

    // initialize the arrays
    ArrayList<Node<T>> preds = new ArrayList<>(MAX_LEVEL + 1);
    ArrayList<Node<T>> succs = new ArrayList<>(MAX_LEVEL + 1);
    for (int i = 0; i < MAX_LEVEL + 1; i++) {
      preds.add(null);
      succs.add(null);
    }

    while (true) {
      boolean found = find(x, preds, succs);
      if (!found) {
        return false; // the value was not in the list
      } else {
        Node<T> nodeToRemove = succs.get(bottomLevel);
        int[] stampHolder = { 0 };

        // mark the node for removal at each level (except the bottom)
        for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
          succ = nodeToRemove.next[level].get(stampHolder);
          while (!stampIsMarked(stampHolder[0])) { // ignore marked nodes
            nodeToRemove.next[level].compareAndSet(succ, succ, stampHolder[0], getMarkedStamp());
            succ = nodeToRemove.next[level].get(stampHolder);
          }
        }

        succ = nodeToRemove.next[bottomLevel].get(stampHolder);
        while (true) {
          boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, stampHolder[0],
              getMarkedStamp());
          succ = succs.get(bottomLevel).next[bottomLevel].get(stampHolder);
          if (iMarkedIt) {
            // we removed the node ourselves
            lastSnapshot = collect(); // update the last snapshot
            find(x, preds, succs);
            return true;
          } else if (stampIsMarked(stampHolder[0])) {
            return false; // the node had already been marked by someone else
          }
        }
      }
    }
  }

  public boolean contains(T x) {
    int bottomLevel = 0;
    int v = x.hashCode();
    int[] stampHolder = { 0 };
    Node<T> pred = head, curr = null, succ = null;

    for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
      curr = pred.next[level].getReference(); // move curr forward

      while (true) {
        succ = curr.next[level].get(stampHolder);
        while (stampIsMarked(stampHolder[0])) { // ignore marked nodes
          curr = curr.next[level].getReference();
          succ = curr.next[level].get(stampHolder);
        }

        if (curr.key < v) { // we haven't found it yet, so continue
          pred = curr;
          curr = succ;
        } else { // we're either there or overshot at this level
          break;
        }
      }
    }

    return curr.key == v; // did we find it?
  }

  /**
   * Takes an atomic snapshot of the skiplist.
   * 
   * @return the atomic snapshot
   */
  public SkipListSnapshot<T> takeSnapshot() {
    SkipListSnapshot<T> previous = lastSnapshot;
    SkipListSnapshot<T> first = collect();
    HashSet<Long> moved = new HashSet<>();

    while (true) {
      SkipListSnapshot<T> next = collect();

      if (next.equals(first)) {
        // clean double-collect, so update snapshot and return
        lastSnapshot = next;
        return next;
      } else {
        // see if a new snapshot has been registered
        SkipListSnapshot<T> newPrevious = lastSnapshot;
        if (previous != newPrevious) {
          if (moved.contains(newPrevious.getThread())) {
            // this thread had moved before, so we can use this snapshot
            return newPrevious;
          } else {
            // record the thread movement and start over
            previous = newPrevious;
            moved.add(newPrevious.getThread());
          }
        }

        // make the previous snapshot the next one
        first = next;
      }
    }
  }

  public List<T> getRange(Range<T> range) {
    ArrayList<T> included = new ArrayList<>();

    SkipListSnapshot<T> snap = takeSnapshot();
    for (T value : snap.getValues()) {
      if (range.contains(value)) {
        included.add(value);
      }
    }

    return included;
  }

  protected boolean find(T x, ArrayList<Node<T>> preds, ArrayList<Node<T>> succs) {
    int bottomLevel = 0;
    int key = x.hashCode();

    int[] stampHolder = { 0 };
    boolean snip;

    Node<T> pred = null, curr = null, succ = null;

    retry: while (true) {
      pred = head;
      for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
        curr = pred.next[level].getReference();

        while (true) {
          succ = curr.next[level].get(stampHolder);

          // attempt to remove marked nodes
          while (stampIsMarked(stampHolder[0])) {
            int oldStamp = pred.next[level].getStamp();
            snip = pred.next[level].compareAndSet(curr, succ, oldStamp, getNextStamp(oldStamp));

            // another find removed it, so start over
            if (!snip) {
              continue retry;
            }

            curr = pred.next[level].getReference();
            succ = curr.next[level].get(stampHolder);
          }

          if (curr.key < key) {
            pred = curr;
            curr = succ;
          } else {
            break; // we found either the item or went past it
          }
        }

        preds.set(level, pred);
        succs.set(level, curr);
      }

      return curr.key == key;
    }
  }

  // taken from thread in:
  // http://stackoverflow.com/questions/12067045/random-level-function-in-skip-list
  private int randomLevel() {
    int lvl = (int) (Math.log(1.0 - random.nextDouble(1.0)) / Math.log(1.0 - 0.5));
    return Math.min(lvl, MAX_LEVEL);
  }

  private SkipListSnapshot<T> collect() {
    SkipListSnapshot<T> snap = new SkipListSnapshot<>();

    int[] stampHolder = { 0 };
    Node<T> curr = head.next[0].get(stampHolder);

    while (curr.value != null) {
      if (!stampIsMarked(stampHolder[0])) { // ignore nodes marked for deletion
        snap.addValue(curr.value, curr.key, stampHolder[0]);
      }

      curr = curr.next[0].get(stampHolder);
    }

    return snap;
  }

  private int getFirstStamp() {
    return 0;
  }

  private int getNextStamp(int oldStamp) {
    int newStamp = oldStamp++;

    if (newStamp == MARKED_STAMP) {
      newStamp = getFirstStamp();
    }

    return newStamp;
  }

  private int getMarkedStamp() {
    return MARKED_STAMP;
  }

  private boolean stampIsMarked(int stamp) {
    return stamp == MARKED_STAMP;
  }

  private final class Node<V> {
    public final V value;
    public final int key;
    public final AtomicStampedReference<Node<V>>[] next;
    private int topLevel;

    // constructor for sentinel nodes
    @SuppressWarnings("unchecked")
    public Node(int key) {
      this.value = null;
      this.key = key;

      int capacity = MAX_LEVEL + 1;
      this.next = (AtomicStampedReference<Node<V>>[]) new AtomicStampedReference[capacity];
      for (int i = 0; i < capacity; i++) {
        this.next[i] = new AtomicStampedReference<>(null, getFirstStamp());
      }

      this.topLevel = MAX_LEVEL;
    }

    // constructor for ordinary nodes
    @SuppressWarnings("unchecked")
    public Node(V x, int height) {
      this.value = x;
      this.key = x.hashCode();

      this.next = (AtomicStampedReference<Node<V>>[]) new AtomicStampedReference[height + 1];
      for (int i = 0; i < height + 1; i++) {
        this.next[i] = new AtomicStampedReference<>(null, getFirstStamp());
      }

      this.topLevel = height;
    }
  }
}
