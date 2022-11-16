package simpledb;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Knows how to compute some aggregate over a set of StringFields.
 */
public class StringAggregator implements Aggregator {

    private static final long serialVersionUID = 1L;
    private final int groupByField;
    private final Type groupByFieldType;
    private final int aggregatorField;
    private final Aggregator.Op aggregatorOp;
    private final ConcurrentHashMap<Field, Integer> fieldCountMap;

    /**
     * Aggregate constructor
     * @param gbfield the 0-based index of the group-by field in the tuple, or NO_GROUPING if there is no grouping
     * @param gbfieldtype the type of the group by field (e.g., Type.INT_TYPE), or null if there is no grouping
     * @param afield the 0-based index of the aggregate field in the tuple
     * @param what aggregation operator to use -- only supports COUNT
     * @throws IllegalArgumentException if what != COUNT
     */

    public StringAggregator(int gbfield, Type gbfieldtype, int afield, Op what) {
        // some code goes here
        if (what != Op.COUNT){
            throw new IllegalArgumentException("Invalid Operation");
        }

        this.groupByField = gbfield;
        this.groupByFieldType = gbfieldtype;
        this.aggregatorField = afield;
        this.aggregatorOp = what;
        this.fieldCountMap = new ConcurrentHashMap<>();
    }

    /**
     * Merge a new tuple into the aggregate, grouping as indicated in the constructor
     * @param tup the Tuple containing an aggregate field and a group-by field
     */
    public void mergeTupleIntoGroup(Tuple tup) {
        // some code goes here
        Field key = tup.getField(this.groupByField);

        if (!this.fieldCountMap.containsKey(key)){
            this.fieldCountMap.put(key, 0);
        }
        this.fieldCountMap.put(key, this.fieldCountMap.get(key) + 1);
    }

    /**
     * Create a DbIterator over group aggregate results.
     *
     * @return a DbIterator whose tuples are the pair (groupVal,
     *   aggregateVal) if using group, or a single (aggregateVal) if no
     *   grouping. The aggregateVal is determined by the type of
     *   aggregate specified in the constructor.
     */
    public DbIterator iterator() {
        // some code goes here
        TupleDesc tupleDesc = new TupleDesc(new Type[] {this.groupByFieldType, Type.INT_TYPE});
        List<Tuple> TupleList = new ArrayList<Tuple>();
        Enumeration<Field> keys = this.fieldCountMap.keys();
        while(keys.hasMoreElements()) {
            Tuple t  = new Tuple(tupleDesc);
            Field key = keys.nextElement();
            t.setField(0, key);
            t.setField(1, new IntField(this.fieldCountMap.get(key)));
            TupleList.add(t);
        }
        return new TupleIterator(tupleDesc, TupleList);
    }
}
