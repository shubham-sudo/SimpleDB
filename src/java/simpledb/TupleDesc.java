package simpledb;

import java.io.Serializable;
import java.util.*;

/**
 * TupleDesc describes the schema of a tuple.
 */
public class TupleDesc implements Serializable {

    /**
     * A help class to facilitate organizing the information of each field
     */
    public static class TDItem implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * The type of the field
         */
        public final Type fieldType;

        /**
         * The name of the field
         */
        public final String fieldName;

        public TDItem(Type t, String n) {
            this.fieldName = n;
            this.fieldType = t;
        }

        public String toString() {
            return fieldName + "(" + fieldType + ")";
        }
    }

    private TDItem[] tItems;

    /**
     * @return
     *         An iterator which iterates over all the field TDItems
     *         that are included in this TupleDesc
     */
    public Iterator<TDItem> iterator() {
        // some code goes here
        Iterator<TDItem> it = new Iterator<TupleDesc.TDItem>() {

            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < tItems.length && tItems[currentIndex] != null;
            }

            @Override
            public TDItem next() {
                return tItems[currentIndex++];
            }

        };

        return it;
    }

    private static final long serialVersionUID = 1L;

    /**
     * Create a new TupleDesc with typeAr.length fields with fields of the
     * specified types, with associated named fields.
     * 
     * @param typeAr
     *                array specifying the number of and types of fields in this
     *                TupleDesc. It must contain at least one entry.
     * @param fieldAr
     *                array specifying the names of the fields. Note that names may
     *                be null.
     */
    public TupleDesc(Type[] typeAr, String[] fieldAr) {
        // some code goes here
        if (typeAr.length == 0) {
            throw new IllegalArgumentException("Must contain at least one entry");
        }
        if (typeAr.length != fieldAr.length) {
            throw new IllegalArgumentException("Length should be same for Type and Field arrays");
        }
        this.tItems = new TDItem[typeAr.length];

        for (int i = 0; i < typeAr.length; i++) {
            this.tItems[i] = new TDItem(typeAr[i], fieldAr[i]);
        }
    }

    /**
     * Constructor. Create a new tuple desc with typeAr.length fields with
     * fields of the specified types, with anonymous (unnamed) fields.
     * 
     * @param typeAr
     *               array specifying the number of and types of fields in this
     *               TupleDesc. It must contain at least one entry.
     */
    public TupleDesc(Type[] typeAr) {
        // some code goes here
        this(typeAr, new String[typeAr.length]);
    }

    /**
     * @return the number of fields in this TupleDesc
     */
    public int numFields() {
        // some code goes here
        return tItems.length;
    }

    /**
     * Gets the (possibly null) field name of the ith field of this TupleDesc.
     * 
     * @param i
     *          index of the field name to return. It must be a valid index.
     * @return the name of the ith field
     * @throws NoSuchElementException
     *                                if i is not a valid field reference.
     */
    public String getFieldName(int i) throws NoSuchElementException {
        // some code goes here
        if (i < 0 || i >= this.tItems.length) {
            throw new NoSuchElementException("No such element found");
        }
        return this.tItems[i].fieldName;
    }

    /**
     * Gets the type of the ith field of this TupleDesc.
     * 
     * @param i
     *          The index of the field to get the type of. It must be a valid
     *          index.
     * @return the type of the ith field
     * @throws NoSuchElementException
     *                                if i is not a valid field reference.
     */
    public Type getFieldType(int i) throws NoSuchElementException {
        // some code goes here
        if (i >= this.tItems.length || this.tItems[i] == null) {
            throw new NoSuchElementException("No such element found");
        }
        return this.tItems[i].fieldType;
    }

    /**
     * Find the index of the field with a given name.
     * 
     * @param name
     *             name of the field.
     * @return the index of the field that is first to have the given name.
     * @throws NoSuchElementException
     *                                if no field with a matching name is found.
     */
    public int fieldNameToIndex(String name) throws NoSuchElementException {
        // some code goes here
        if (name != null) {
            for (int i = 0; i < this.numFields(); i++) {
                if (this.getFieldName(i) != null && this.getFieldName(i).equals(name)) {
                    return i;
                }
            }
        }
        throw new NoSuchElementException("No such element found");
    }

    /**
     * @return The size (in bytes) of tuples corresponding to this TupleDesc.
     *         Note that tuples from a given TupleDesc are of a fixed size.
     */
    public int getSize() {
        // some code goes here
        int totalSize = 0;
        for (TDItem tItem : tItems) {
            totalSize += tItem.fieldType.getLen();
        }
        return totalSize;
    }

    /**
     * Merge two TupleDescs into one, with td1.numFields + td2.numFields fields,
     * with the first td1.numFields coming from td1 and the remaining from td2.
     * 
     * @param td1
     *            The TupleDesc with the first fields of the new TupleDesc
     * @param td2
     *            The TupleDesc with the last fields of the TupleDesc
     * @return the new TupleDesc
     */
    public static TupleDesc merge(TupleDesc td1, TupleDesc td2) {
        // basically merging two tuple desc is done by creating new TupleDesc
        Type[] newtypeAr = new Type[td1.numFields() + td2.numFields()];
        String[] newfieldAr = new String[td1.numFields() + td2.numFields()];
        int i = 0;

        Iterator<TDItem> it1 = td1.iterator();
        Iterator<TDItem> it2 = td2.iterator();

        while (it1.hasNext()) {
            TDItem tItem = it1.next();
            newtypeAr[i] = tItem.fieldType;
            newfieldAr[i] = tItem.fieldName;
            i++;
        }

        while (it2.hasNext()) {
            TDItem tItem = it2.next();
            newtypeAr[i] = tItem.fieldType;
            newfieldAr[i] = tItem.fieldName;
            i++;
        }

        return new TupleDesc(newtypeAr, newfieldAr);
    }

    /**
     * Compares the specified object with this TupleDesc for equality. Two
     * TupleDescs are considered equal if they are the same size and if the n-th
     * type in this TupleDesc is equal to the n-th type in td.
     * 
     * @param o
     *          the Object to be compared for equality with this TupleDesc.
     * @return true if the object is equal to this TupleDesc.
     */
    public boolean equals(Object o) {
        // some code goes here
        if (!(o instanceof TupleDesc) || ((TupleDesc) o).numFields() != this.numFields()) {
            return false;
        } else {
            TupleDesc other = (TupleDesc) o;
            for (int i = 0; i < this.numFields(); i++) {
                if (this.getFieldType(i) != other.getFieldType(i)) {
                    return false;
                }
            }
        }

        return true;
    }

    public int hashCode() {
        // If you want to use TupleDesc as keys for HashMap, implement this so
        // that equal objects have equals hashCode() results
        return tItems.hashCode();
    }

    /**
     * Returns a String describing this descriptor. It should be of the form
     * "fieldType[0](fieldName[0]), ..., fieldType[M](fieldName[M])", although
     * the exact format does not matter.
     * 
     * @return String describing this descriptor.
     */
    public String toString() {
        // some code goes here
        String[] toStringName = new String[getSize()];

        for (int i = 0; i < getSize(); i++) {
            toStringName[i] = tItems[i].fieldType + "(" + tItems[i].fieldName + ")";
        }
        return String.join(",", toStringName);
    }
}
