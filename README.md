# Questions for lab/Piaaza

- What should happen when a tuples tupleDesc is reset. Do we need to reset the fields to empty array because this could be something like a update table columns kind of process. If thats the case then we might need to add all the previous fields into new fields.

## **CS 660 Fall-2022 (Programming Assignment #1)**

### **Student Details**

_**Name**_ - Shubham Kaushik

_**Email**_ - kaushiks@bu.edu

---

### **Write Up**

1. Describe any design decisions you made. These may be minimal for pa1

    - I assumed that `resetTupleDesc` method is something for update/drop column of table and because of that I am creating a new `Field[]` array with new length and all the previous fields are also copied to this new field array. _**Note**_ - copying only happens for `min(oldTupleDesc.numFields(), newTupleDesc.numFields())`.

2. Discuss and justify any changes you made to the API

3. Describe any missing or incomplete elements of your code

    - As per my knowledge, all the elements required for lab1 are complete in my assignment. The only part I could guess is the use of transactionId and Permissions are not in use for anything as of now.

4. Describe how long you spent on the assignment, and whether there was anything you found particularly difficult or confusing

    - For this assignment, I've spent around 2-3 days to understand and start myself writing code. There were two things which were bit tricky for me, first the use of bit manipulation for the `HeapPage.isSlotUsed` method to compute if the slot is in use with the help of header array of bytes, second was the linking of Iterators from `SeqScan` to `DbFileIterator` of `HeapFile` class. Things are quite clear now.

5. If you collaborate with someone else, you have to discribe how you split the workload

    - I worked alone on this assignment but yes the discussion in labs, javadoc and slide for simpledb helped a lot to speed up.

---