package simpledb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Knows how to compute some aggregate over a set of IntFields.
 */
public class IntegerAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private final int groupByField;
    private final Type groupByFieldType;
    private final int aggregatorField;
    private final Op aggregatorOp;
    private final ConcurrentHashMap<Field, Integer> fieldAggregateMap;
    private final ConcurrentHashMap<Field, Integer> fieldCountMap;
    private static final Field DEFAULT_GROUP_FIELD = new IntField(0);

    /**
     * Aggregate constructor
     * 
     * @param gbfield
     *            the 0-based index of the group-by field in the tuple, or
     *            NO_GROUPING if there is no grouping
     * @param gbfieldtype
     *            the type of the group by field (e.g., Type.INT_TYPE), or null
     *            if there is no grouping
     * @param afield
     *            the 0-based index of the aggregate field in the tuple
     * @param what
     *            the aggregation operator
     */

    public IntegerAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        this.groupByField = gbfield;
        this.groupByFieldType = gbfieldtype;
        this.aggregatorField = afield;
        this.aggregatorOp = what;
        this.fieldAggregateMap = new ConcurrentHashMap<>();
        this.fieldCountMap = new ConcurrentHashMap<>();

        if (this.groupByField == Aggregator.NO_GROUPING){
            this.fieldCountMap.put(DEFAULT_GROUP_FIELD, 0);
            this.fieldAggregateMap.put(DEFAULT_GROUP_FIELD, 0);
        }
    }

    private int getAggregateValue(int oldValue, int newValue){

        switch (this.aggregatorOp){
            case MIN:
                return Math.min(oldValue, newValue);
            case MAX:
                return Math.max(oldValue, newValue);
            case SUM:
            case AVG:
                return oldValue + newValue;
            case COUNT:
                return oldValue + 1;
            default:
                return 0;
        }
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the
     * constructor
     * 
     * @param tup
     *            the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        Field key = DEFAULT_GROUP_FIELD;
        if (this.groupByField != Aggregator.NO_GROUPING){
            key = tup.getField(this.groupByField);
        }

        int value = ((IntField) tup.getField(this.aggregatorField)).getValue();

        if (this.fieldAggregateMap.containsKey(key) && this.fieldCountMap.containsKey(key)){
            this.fieldAggregateMap.put(key, this.getAggregateValue(this.fieldAggregateMap.get(key), value));
            this.fieldCountMap.put(key, this.fieldCountMap.get(key) + 1);
        } else {
            this.fieldAggregateMap.put(key, this.aggregatorOp != Op.COUNT ? value : 1);
            this.fieldCountMap.put(key, 1);
        }
    }

    /**
     * Create a DbIterator over group aggregate results.
     * 
     * @return a DbIterator whose tuples are the pair (groupVal, aggregateVal)
     *         if using group, or a single (aggregateVal) if no grouping. The
     *         aggregateVal is determined by the type of aggregate specified in
     *         the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        return new DbIterator() {
            Iterator<Field> fieldIterator = fieldAggregateMap.keySet().iterator();
            @Override
            public void open() throws DbException, TransactionAbortedException {
                fieldIterator = fieldAggregateMap.keySet().iterator();
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                return fieldIterator.hasNext();
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                Field key = fieldIterator.next();
                if (groupByField == Aggregator.NO_GROUPING){
                    Tuple tuple = new Tuple(new TupleDesc(new Type[]{ Type.INT_TYPE}));
                    int value = fieldAggregateMap.get(key);
                    if (aggregatorOp == Op.AVG){
                        value /= fieldCountMap.get(key);
                    }
                    tuple.setField(0, new IntField(value));
                    return tuple;
                } else {
                    Tuple tuple = new Tuple(new TupleDesc(new Type[] { groupByFieldType, Type.INT_TYPE}));
                    int value = fieldAggregateMap.get(key);
                    if (aggregatorOp == Op.AVG){
                        value /= fieldCountMap.get(key);
                    }
                    tuple.setField(0, key);
                    tuple.setField(1, new IntField(value));
                    return tuple;
                }
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                fieldIterator = fieldAggregateMap.keySet().iterator();
            }

            @Override
            public TupleDesc getTupleDesc() {
                return new TupleDesc(new Type[] { groupByFieldType, Type.INT_TYPE});
            }

            @Override
            public void close() {
                fieldIterator = null;
            }
        };
    }

}
