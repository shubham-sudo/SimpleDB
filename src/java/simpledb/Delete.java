package simpledb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The delete operator. Delete reads tuples from its child operator and removes
 * them from the table they belong to.
 */
public class Delete extends Operator {

    private static final long serialVersionUID = 1L;
    private final TransactionId tid;
    private DbIterator child;
    private final TupleDesc tupleDesc;
    private TupleIterator tupleIterator;

    /**
     * Constructor specifying the transaction that this delete belongs to as
     * well as the child to read from.
     * 
     * @param t
     *            The transaction this delete runs in
     * @param child
     *            The child operator from which to read tuples for deletion
     */
    public Delete(TransactionId t, DbIterator child) {
        // some code goes here
        this.tid = t;
        this.child = child;
        this.tupleDesc = new TupleDesc(new Type[]{ Type.INT_TYPE });
    }

    public TupleDesc getTupleDesc() {
        // some code goes here
        return this.tupleDesc;
    }

    public void open() throws DbException, TransactionAbortedException {
        // some code goes here
        this.child.open();
        super.open();
        deleteTuples();
        this.tupleIterator.open();
    }

    public void close() {
        // some code goes here
        super.close();
        this.child.close();
        this.tupleIterator.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        // some code goes here
        this.tupleIterator.rewind();
    }

    private void deleteTuples() throws TransactionAbortedException, DbException {
        int recordsCount = 0;
        while (this.child.hasNext()){
            Tuple tuple = this.child.next();
            try{
                Database.getBufferPool().deleteTuple(this.tid, tuple);
                recordsCount++;
            } catch (IOException io){
                io.printStackTrace();
            }
        }
        Tuple tuple = new Tuple(this.tupleDesc);
        tuple.setField(0, new IntField(recordsCount));
        this.tupleIterator = new TupleIterator(this.tupleDesc, new ArrayList<Tuple>(Collections.singletonList(tuple)));
    }

    /**
     * Deletes tuples as they are read from the child operator. Deletes are
     * processed via the buffer pool (which can be accessed via the
     * Database.getBufferPool() method.
     * 
     * @return A 1-field tuple containing the number of deleted records.
     * @see Database#getBufferPool
     * @see BufferPool#deleteTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        // some code goes here
        if (this.tupleIterator.hasNext()){
            return tupleIterator.next();
        }
        return null;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        return new DbIterator[]{this.child};
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
        this.child = children[0];
    }

}
