package skiplist;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LFSkipList<T> {
  final int MAX_LEVEL;
  final Node<T> head;
  final Node<T> tail;

  public LFSkipList(int maxLevel) {
    this.MAX_LEVEL = maxLevel;

    this.head = new Node<T>(Integer.MIN_VALUE);
    this.tail = new Node<T>(Integer.MAX_VALUE);

    for (int i = 0; i < MAX_LEVEL + 1; i++) {
      head.next[i] = new AtomicMarkableReference<>(tail, false);
    }
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

        // linking in the new node
        for (int level = bottomLevel; level <= topLevel; level++) {
          Node<T> succ = succs.get(level);
          newNode.next[level].set(succ, false);
        }

        Node<T> pred = preds.get(bottomLevel);
        Node<T> succ = succs.get(bottomLevel);
        if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
          continue; // we couldn't add the node at the bottom level, so we need
                    // to start over
        }

        for (int level = bottomLevel + 1; level <= topLevel; level++) {
          while (true) {
            pred = preds.get(level);
            succ = succs.get(level);
            if (pred.next[level].compareAndSet(succ, newNode, false, false)) {
              break; // we were able to add this level in, so proceed to next
                     // step of loop
            }

            find(x, preds, succs); // we couldn't add, so we need to refresh the
                                   // preds and succs
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
        boolean[] marked = { false };

        // mark the node for removal at each level (except the bottom)
        for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
          succ = nodeToRemove.next[level].get(marked);
          while (!marked[0]) {
            nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
            succ = nodeToRemove.next[level].get(marked);
          }
        }

        succ = nodeToRemove.next[bottomLevel].get(marked);
        while (true) {
          boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
          succ = succs.get(bottomLevel).next[bottomLevel].get(marked);
          if (iMarkedIt) {
            find(x, preds, succs);
            return true; // we removed the node ourselves
          } else if (marked[0]) {
            return false; // the node had already been marked by someone else
          }
        }
      }
    }
  }

  public boolean contains(T x) {
    int bottomLevel = 0;
    int v = x.hashCode();
    boolean[] marked = { false };
    Node<T> pred = head, curr = null, succ = null;

    for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
      curr = pred.next[level].getReference(); // move curr forward

      while (true) {
        succ = curr.next[level].get(marked);
        while (marked[0]) { // ignore marked nodes
          curr = curr.next[level].getReference();
          succ = curr.next[level].get(marked);
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

  protected boolean find(T x, ArrayList<Node<T>> preds, ArrayList<Node<T>> succs) {
    int bottomLevel = 0;
    int key = x.hashCode();

    boolean[] marked = { false };
    boolean snip;

    Node<T> pred = null, curr = null, succ = null;

    retry: while (true) {
      pred = head;
      for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
        curr = pred.next[level].getReference();

        while (true) {
          succ = curr.next[level].get(marked);

          // attempt to remove marked nodes
          while (marked[0]) {
            snip = pred.next[level].compareAndSet(curr, succ, false, false);

            // another find removed it, so start over
            if (!snip) {
              continue retry;
            }

            curr = pred.next[level].getReference();
            succ = curr.next[level].get(marked);
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
    int lvl = (int) (Math.log(1.0 - Math.random()) / Math.log(1.0 - 0.5));
    return Math.min(lvl, MAX_LEVEL);
  }

  private final class Node<V> {
    public final int key;
    public final AtomicMarkableReference<Node<V>>[] next;
    private int topLevel;

    // constructor for sentinel nodes
    @SuppressWarnings("unchecked")
    public Node(int key) {
      this.key = key;

      int capacity = MAX_LEVEL + 1;
      this.next = (AtomicMarkableReference<Node<V>>[]) new AtomicMarkableReference[capacity];
      for (int i = 0; i < capacity; i++) {
        this.next[i] = new AtomicMarkableReference<>(null, false);
      }

      this.topLevel = MAX_LEVEL;
    }

    // constructor for ordinary nodes
    @SuppressWarnings("unchecked")
    public Node(V x, int height) {
      this.key = x.hashCode();

      this.next = (AtomicMarkableReference<Node<V>>[]) new AtomicMarkableReference[height + 1];
      for (int i = 0; i < height + 1; i++) {
        this.next[i] = new AtomicMarkableReference<>(null, false);
      }

      this.topLevel = height;
    }
  }
}
