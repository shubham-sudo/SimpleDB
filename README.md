## **CS 660 Fall-2022 (Programming Assignment #1,2,3)**

### **Student Details**

_**Name**_ - Shubham Kaushik

_**Email**_ - kaushiks@bu.edu

---

### **Write Up**

1. Describe any design decisions you made. These may be minimal for pa1

    - Use of `ConcurrentHashMap` for defining private variable in `class Catalog` to make it thread safe.
    - Defined variable `pages` in `class BufferPool` using `ConcurrentHashMap` for thread safety.
    - For `Join`, a simple nested loop join is used, in which it will iterate over one child only once and compare with each tuple of another child.
      So, if relation R has N records and S has M records it will run do IO for (N + (N*M)).
    - For `HashEquiJoin`, partitions are only done for one of the child and stored in-memory. The hasNext will pull one tuple from another child and
      do hashing over the join value and pull that particular partition for first child. This way it will save some comparison assuming the data is
      uniformly distributed

2. Discuss and justify any changes you made to the API

    - _**class Tuple**_ : Added few bunch of private variable like tupleDesc, recordId, fields and implemented an _Iterator_ for fields.
    - _**class TupleDesc**_ : Added `TDItem[] tItems` array for holding TDItem objects. The typeAr should have some item otherwise constructor will throw Exception.
    - _**class Catalog**_ : Defined bunch of new private `ConcurrentHashMap` variables, which are thread safe as per the java documentation.
    - _**class BufferPool**_ : Defined variable `pages` using `ConcurrentHashMap` for thread safe. Implemented `getPage` to get the page from buffer if exists or if not read it from disk(`DbFile/HeapFile`).
    - _**class HeapPageId**_ : Defined few private final variables and also updated `hashCode` & `equals` methods.
    - _**class RecordId**_ : Defined few private final variabled and also updated `hashCode` & `equals` methods.
    - _**class HeapPage**_ : Updated `getNumTuples` & `getHeaderSize` with the help of given doc on Lab webpage. Also, implemented the `iterator` method for returning an iterator over the `Tuple` objects stored on this page.
    - _**class HeapFile**_ : Updated the `readPage` method to read a page from `HeapFile`. I am using `RandomAccessFile` to seek over the file randomly and read the `BufferPool.getPageSize` bytes data each time. The `pageNumber` is used to calculate the address of the page in the disk. I have also implemented `DbFileIterator` to iterate over all of the tuples we have in this `HeapFile`.
    - _**SeqScan**_ : This was easy as we just have to link this with the `HeapFile` and `DbFileIterator`.
    - _**class BTreeFile**_ :
       + _**findLeafPage()**_ : This method is used to pull leaf page as per the given Field key value. This is implemented
         using recursion. If the Field value is passed as 'null', it will pull the left most page at leaf level.
       + _**splitLeafPage()**_ : This method help in splitting the leaf page when we insert more values and the leaf page is full.
         The middle node is copied to the internal nodes after splitting and parent & sibling pointer are updated.
       + _**splitInternalPage()**_ : The method do the splitting part for the internal node. This also increases the height
         of the BTree and push the middle node towards the parent node.
       + _**stealFromLeafPage()**_ : The method is used for the Deletion purpose, if the tuples are less than half full in any leaf node.
         It will try to steal nodes from its siblings.
       + _**stealFromLeftInternalPage()**_ : This method is for stealing nodes from the sibling when the internal node
         is out of its half full capacity.
       + _**stealFromRightInternalPage()**_ : This is similar to the upper method just be used when stealing nodes from
         right internal node, instead of left.
       + _**mergeLeafPages()**_ : While deletion if the stealing doesn't work out than merging of two nodes happens in BTree.
         The mergeLeafPages merges the leaf pages whenever required.
       + _**mergeInternalPages()**_ : This is same as above just to merge the internal nodes.

    - _**class IndexPredicate**_ : Updated to compare the field values using the given operators. The operators are defined as enums
      The Comparison is done based in the given '>=', '<=', '==' operators.
    - _**class Predicate**_ : Defined the required class variables to support IndexPredicate class operations.
    - _**Filter, Join & HashEquiJoin**_ : Added required class variables and implemented the hasNext() method call to return the results after filter,
      join and hashJoin. The strategy used for join is simple nested loop join and
      for hashEquiJoin I have used hashing and making partition for one child.
    - _**IntegerAggregator, StringAggregator, Aggregate**_ : Implemented integer & string aggregate classes for aggregating on the required column
      based on groupby if given.
    - _**HeapPage, HeapFile**_ : Implemented insert and delete tuples method to add mutability of tuples in pages or file. Also, eviction is checked for both operations.
    - _**Insert, Delete**_ : Insert and delete operators are implemented for inserting the new record or deleting the existing one.

3. Describe any missing or incomplete elements of your code

    - As per my knowledge, all the elements required for lab1 are complete in my assignment. The only part I could guess is the use of transactionId and Permissions are not in use for anything as of now.

4. Describe how long you spent on the assignment, and whether there was anything you found particularly difficult or confusing

    - For this assignment, I've spent around 2-3 days to understand and start myself writing code. There were two things which were bit tricky for me, first the use of bit manipulation for the `HeapPage.isSlotUsed` method to compute if the slot is in use with the help of header array of bytes, second was the linking of Iterators from `SeqScan` to `DbFileIterator` of `HeapFile` class. Things are quite clear now.

5. If you collaborate with someone else, you have to discribe how you split the workload

    - I worked alone on this assignment but yes the discussion in labs, javadoc and slide for simpledb helped a lot to speed up.

---
