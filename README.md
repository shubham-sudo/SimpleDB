## **CS 660 Fall-2022 (Programming Assignment #1)**

### **Student Details**

_**Name**_ - Shubham Kaushik

_**Email**_ - kaushiks@bu.edu

---

### **Write Up**

1. Describe any design decisions you made. These may be minimal for pa1

    - I assumed that `resetTupleDesc` method is something for update/drop column of table so, because of that I am creating a new `Field[]` array with new length and all the previous fields are also copied to this new field array. _**Note**_ - copying only happens for `min(oldTupleDesc.numFields(), newTupleDesc.numFields())`.
    - Defined variable `pages` in `class BufferPool` using `ConcurrentHashMap` for thread safety.

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

3. Describe any missing or incomplete elements of your code

    - As per my knowledge, all the elements required for lab1 are complete in my assignment. The only part I could guess is the use of transactionId and Permissions are not in use for anything as of now.

4. Describe how long you spent on the assignment, and whether there was anything you found particularly difficult or confusing

    - For this assignment, I've spent around 2-3 days to understand and start myself writing code. There were two things which were bit tricky for me, first the use of bit manipulation for the `HeapPage.isSlotUsed` method to compute if the slot is in use with the help of header array of bytes, second was the linking of Iterators from `SeqScan` to `DbFileIterator` of `HeapFile` class. Things are quite clear now.

5. If you collaborate with someone else, you have to discribe how you split the workload

    - I worked alone on this assignment but yes the discussion in labs, javadoc and slide for simpledb helped a lot to speed up.

---
