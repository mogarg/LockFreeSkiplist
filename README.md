# Lock-Free Skip List with range query

The abstract data type (ADT) of an ordered set supports three key operations: INSERT(key), DELETE(key) and FIND(key). We aim to add another operation RANGEQUERY(a,b) ∀a ≤ b which returns all the keys in the closed set [a,b].
While there are several approaches like Standard Transactional Memory or Coarse-Grained and Fine-Grained Locking these approaches have high overheads because for a range-query we have to protect the whole data structure. Snapshots offer another approach for implementing range queries. If we could quickly take a snapshot of the data structure, then we could simply perform a sequential range query on the result. The snapshot object is a vector V of data elements supporting two operations: ```Update(i, val)```, which atomically sets V_i to val, and ```Scan```, which atomically reads and returns all of the elements of V.1. Scan can be implemented by repeatedly performing a pair of Collects (which read each element of V in sequence and return a new vector containing the values it read) until the results of the two Collects are equal.

For a range query it is imperative that we know the contents of a data structure in [a,b] at a particular point in time. This is trivial when a data structure is accessed sequentially. However, it is not clear how to do this for non-blocking concurrent data structures. A sequentially consistent global view of a data structure can be obtained by a snapshot. 

A Snapshot interface consists of a method scan() which returns a collection of values, each corresponding to the latest preceding update(), that, it returns a collection of register values


```Java
public interface Snapshot <T>{ public void update(t,v); public T[] scan[];}
```

