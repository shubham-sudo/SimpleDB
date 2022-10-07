package simpledb;

import java.util.*;

/**
 * SeqScan is an implementation of a sequential scan access method that reads
 * each tuple of a table in no particular order (e.g., as they are laid out on
 * disk).
 */
public class SeqScan implements DbIterator {

    private static final long serialVersionUID = 1L;
    private TransactionId tId;
    private int tableId;
    private String tableAlias;
    private String tableName;
    private DbFileIterator dbFileIterator;

    /**
     * Creates a sequential scan over the specified table as a part of the
     * specified transaction.
     *
     * @param tid
     *                   The transaction this scan is running as a part of.
     * @param tableid
     *                   the table to scan.
     * @param tableAlias
     *                   the alias of this table (needed by the parser); the
     *                   returned
     *                   tupleDesc should have fields with name tableAlias.fieldName
     *                   (note: this class is not responsible for handling a case
     *                   where
     *                   tableAlias or fieldName are null. It shouldn't crash if
     *                   they
     *                   are, but the resulting name can be null.fieldName,
     *                   tableAlias.null, or null.null).
     */
    public SeqScan(TransactionId tid, int tableid, String tableAlias) {
        // some code goes here
        this.tId = tid;
        this.tableId = tableid;
        this.tableAlias = tableAlias;
        this.tableName = Database.getCatalog().getTableName(tableid);
    }

    /**
     * @return
     *         return the table name of the table the operator scans. This should
     *         be the actual name of the table in the catalog of the database
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * @return Return the alias of the table this operator scans.
     */
    public String getAlias() {
        // some code goes here
        return this.tableAlias;
    }

    /**
     * Reset the tableid, and tableAlias of this operator.
     * 
     * @param tableid
     *                   the table to scan.
     * @param tableAlias
     *                   the alias of this table (needed by the parser); the
     *                   returned
     *                   tupleDesc should have fields with name tableAlias.fieldName
     *                   (note: this class is not responsible for handling a case
     *                   where
     *                   tableAlias or fieldName are null. It shouldn't crash if
     *                   they
     *                   are, but the resulting name can be null.fieldName,
     *                   tableAlias.null, or null.null).
     */
    public void reset(int tableid, String tableAlias) {
        // some code goes here
        this.tableId = tableid;
        this.tableAlias = tableAlias;
    }

    public SeqScan(TransactionId tid, int tableid) {
        this(tid, tableid, Database.getCatalog().getTableName(tableid));
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        dbFileIterator = Database.getCatalog().getDatabaseFile(tableId).iterator(tId);
        dbFileIterator.open();
    }

    /**
     * Returns the TupleDesc with field names from the underlying HeapFile,
     * prefixed with the tableAlias string from the constructor. This prefix
     * becomes useful when joining tables containing a field(s) with the same
     * name. The alias and name should be separated with a "." character
     * (e.g., "alias.fieldName").
     *
     * @return the TupleDesc with field names from the underlying HeapFile,
     *         prefixed with the tableAlias string from the constructor.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        TupleDesc tupleDesc = Database.getCatalog().getTupleDesc(tableId);
        int tdSize = tupleDesc.getSize();
        Type[] typesAr = new Type[tdSize];
        String[] fieldsAr = new String[tdSize];

        for (int i = 0; i < tdSize; i++) {
            String aliasPrefix = getAlias() == null ? "null." : getAlias() + ".";
            String fieldName = tupleDesc.getFieldName(i);
            fieldName = fieldName == null ? "null" : fieldName;
            fieldsAr[i] = aliasPrefix + fieldName;
            typesAr[i] = tupleDesc.getFieldType(i);
        }
        return new TupleDesc(typesAr, fieldsAr);
    }

    public boolean hasNext() throws TransactionAbortedException, DbException {
        // some code goes here
        return dbFileIterator.hasNext();
    }

    public Tuple next() throws NoSuchElementException,
            TransactionAbortedException, DbException {
        // some code goes here
        return dbFileIterator.next();
    }

    public void close() {
        // some code goes here
        dbFileIterator.close();
    }

    public void rewind() throws DbException, NoSuchElementException,
            TransactionAbortedException {
        // some code goes here
        dbFileIterator.rewind();
    }
}
