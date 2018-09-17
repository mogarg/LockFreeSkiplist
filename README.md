# Lock-Free Skip List with range query

The abstract data type (ADT) of an ordered set supports three key operations: ```INSERT(key)```, ```DELETE(key)``` and ```FIND(key)```. This implementation add another operation ```RANGEQUERY(a,b)``` ∀a ≤ b which returns all the keys in the closed set [a,b].

While there exist several approaches like Standard Transactional Memory or Coarse-Grained and Fine-Grained Locking these approaches have high overheads a range-query requires the whole data structure to be protected. Snapshots offer another approach for implementing range queries. 

A snapshot of the data structure allows for a sequential range query on the result. The snapshot object is a vector V of data elements supporting two operations: ```Update(i, val)```, which atomically sets V_i to val, and ```Scan```, which atomically reads and returns all of the elements of V. Scan can be implemented by repeatedly performing a pair of Collects (which read each element of V in sequence and return a new vector containing the values it read until the results of the two Collects are equal.

For a range query it is imperative that we know the contents of a data structure in [a,b] at a particular point in time. This is trivial when a data structure is accessed sequentially. However, it is not clear how to do this for non-blocking concurrent data structures. 

A Snapshot interface consists of a method scan() which returns a collection of values, each corresponding to the latest preceding update(), that, it returns a collection of register values


```Java
public interface Snapshot <T>{ public void update(t,v); public T[] scan[];}
```

## RSkipList

For RSkipList a ```collect()``` method takes a non-atomic snapshot of the skiplist. If we perform two collects one after another and the snapshots match then we know that there was an interval where no threads updated the skiplist. So the result of the snapshot is the the snapshot of the system right after the end of the first collect. We can call this pair of two collects as a clean double collect.

The update methods (add or remove) take a snapshot of the list before updating their values. So, if these snapshots do not match, then we an use a snapshot taken by an update method which is stored in the lastsnapshot. We have to however make sure that the lastsnapshot is taken by the thread (let’s say A) who’s update method immediately succeeds the first collect because it is possible that another thread B updated the list after the snapshot was taken by the thread A. We can assure this if we see the thread A update twice during the takeSnapshot() execution.
We achieve this by using a set moved which stores the thread id of the thread that updated once during the takeSnapshot() execution. If the thread updates twice we can be sure that this is the last thread to update the list and we can use the snapshot of taken by this thread which is essentially stored in newPrevious.

```Java 
public
SkipListSnapshot <T>
SkipListSnapshot <T>
SkipListSnapshot <T>
HashSet<Long> moved = new HashSet<>();
}
Listing 3: RSkipList: takeSnapShot
while (true) { SkipListSnapshot <T> next =
if (next.equals(first)) { // clean double -collect ,
return
lastSnapshot = next;
return next; } else {
// see if a new snapshot
collect();
so update snapshot and
has been registered
takeSnapshot() {
previous = lastSnapshot; first = collect();
SkipListSnapshot <T> newPrevious = lastSnapshot; if (previous != newPrevious) {
if (moved.contains(newPrevious.getThread())) { // this thread had moved before, so we can use
this snapshot
return newPrevious;
} else {
// record the thread movement and start over previous = newPrevious; moved.add(newPrevious.getThread());
} }
// make the previous snapshot the next one
first = next; }
```
## R2SkipList 

R2SkipList also uses the collect method. If we perform two collects one after another and the snapshots match then we know that there was an interval where no threads updated the skiplist. So this is a clean double collect and we return the snapshot taken by this thread. We also maintain a copy of this snapshot in an array of SkipListSnapShot objects indexed the the thread ID.
In this case if we are not able to clean double collect then we call the method getMovedThreads() that detects moved threads between two snapshots. In this case the update methods (add
or remove) don’t only increment the stamp but also use it to store the ID of the modifying thread. So we compare these stamps to figure out which thread made the change. If the snapshot taken by this thread is present in the the threadSnapShots then we return the snapshot.

```Java 
private SkipListSnapshot <T>[] threadSnapshots; public SkipListSnapshot <T> takeSnapshot() {
 SkipListSnapshot <T> first = HashSet <Integer > moved = new
while (true) { SkipListSnapshot <T> next =
collect(); HashSet <>();
collect();
if (next.equals(first)) {
// clean double -collect , so update snapshot and return threadSnapshots[getThreadId()] = next;
return next;
} else {
// see if a new snapshot has been registered ArrayList<Integer> movedIds = getMovedThreads(first,
next);
for (Integer id : movedIds) { if (moved.contains(id)) {
if (threadSnapshots[id] != null) { return threadSnapshots[id];
}
} else {
 Page 6 of 17
R2SkipList
Range Queries in a Non-Blocking Skip-List
PROJECT DESCRIPTION (continued)
moved.add(id); }
}
// make the previous snapshot the next one
first = next; }
} }


```
