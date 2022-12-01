package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {
    private final int[] histBins;
    private final int minValue;
    private final int maxValue;
    private volatile int numberOfTuples;
    private volatile double avgSelectivity;

    /**
     * Create a new IntHistogram.
     *
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     *
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     *
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't
     * simply store every value that you see in a sorted list.
     *
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
    public IntHistogram(int buckets, int min, int max) {
        // some code goes here
        histBins = new int[buckets];
        this.minValue = min;
        this.maxValue = max;
        this.avgSelectivity=0.0;
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
        //read volatile == synchronized enter, only for data visibility, no mutex guaranteed
        int currNumberOfTuples = numberOfTuples;
        int bucketCounts = ((maxValue - minValue) / histBins.length);
        if (bucketCounts == 0)
            bucketCounts++;

        int buckets = (v - minValue) / bucketCounts;
        if (buckets >= histBins.length) buckets = histBins.length - 1;
        int old= histBins[buckets];
        histBins[buckets]++;

        this.avgSelectivity += 2 * old + 1;
        //write volatile == synchronized leave
        numberOfTuples =currNumberOfTuples+1;
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     *
     * For example, if "op" is "GREATER_THAN" and "v" is 5,
     * return your estimate of the fraction of elements that are greater than 5.
     *
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {

        double selectValue = v;

        // special cases to deal with outliers
        if (op == Predicate.Op.GREATER_THAN_OR_EQ) {
            if (v<= minValue) return 1;
            if (v> maxValue) return 0;
        }
        if (op == Predicate.Op.GREATER_THAN) {
            if (v < minValue) return 1;
            if (v >= maxValue) return 0;
            //return slightly less than full bucket
            if (selectValue < maxValue) selectValue = selectValue+.01;
        }
        if (op == Predicate.Op.LESS_THAN) {
            if (v <= minValue) return 0;
            if (v > maxValue) return 1;
            //return slightly less than full bucket
            if (selectValue > minValue) selectValue = selectValue - .01;
        }
        if (op == Predicate.Op.LESS_THAN_OR_EQ) {
            if (v < minValue) return 0;
            if (v >= maxValue) return 1;

            //computations below return data exclusive of v, so be sure to include v's bin
            if (selectValue < maxValue) selectValue = selectValue + .99;
        }
        if (op == Predicate.Op.EQUALS || op == Predicate.Op.LIKE) {
            if (v < minValue || v > maxValue)
                return 0;
        }
        if (op == Predicate.Op.NOT_EQUALS) {
            if (v < minValue || v > maxValue)
                return 1;
        }


        int bucketCounts = ((maxValue - minValue)/ histBins.length);
        if (bucketCounts == 0)
            bucketCounts++;

        if (selectValue < minValue) selectValue = minValue - bucketCounts;
        if (selectValue > maxValue) selectValue = maxValue + bucketCounts;
        int bucket = (int)((selectValue- minValue) / bucketCounts);
        if (bucket < 0) bucket = 0;
        if (bucket >= histBins.length) bucket = histBins.length -1;
        double bucketMin = (bucket * bucketCounts) + minValue;
        double bucketMax = ((bucket+1) * bucketCounts) + minValue;

        //read volatile == synchronized enter
        int currNumberOfTuples = numberOfTuples;

        switch (op) {
            case NOT_EQUALS: case EQUALS: case LIKE:
                double frac = ((double)(histBins[bucket])/(double) numberOfTuples)/(double)bucketCounts;
                if (frac > 0 && frac < 1.0/(double) numberOfTuples)
                    //if there is some density in bin, then selectivity is at least 1/nups
                    frac = 1.0 / (double) numberOfTuples;
                if (op == Predicate.Op.EQUALS || op == Predicate.Op.LIKE)
                    return frac;
                else
                    return 1-frac;
            case GREATER_THAN_OR_EQ:
            case GREATER_THAN:
                //estimate fraction of this bucket
                double bucketFraction = (bucketMax - (selectValue)) / (double)(bucketMax - bucketMin);

                if (bucketFraction > 1) bucketFraction = 1.0;
                if (bucketFraction < 0) bucketFraction = 0;

                //compute selectivity in this bucket
                double bucketSelect =  ((double) histBins[bucket]/(double) numberOfTuples) * bucketFraction;
                for (int i = bucket+1; i < histBins.length; i++) {
                    bucketSelect +=  ((double) histBins[i]/ numberOfTuples);

                }

                return bucketSelect;

            case LESS_THAN:
            case LESS_THAN_OR_EQ:

                bucketFraction = 1.0 - ((bucketMax - selectValue) / (double)(bucketMax - bucketMin));
                if (bucketFraction < 0) bucketFraction = 0;
                if (bucketFraction > 1) bucketFraction = 1.0;

                //compute selectivity in this bucket
                bucketSelect =  ((double) histBins[bucket]/ numberOfTuples) * bucketFraction;
                for (int i = bucket-1; i >= 0; i--) {
                    bucketSelect +=  ((double) histBins[i]/ numberOfTuples);
                }
                return bucketSelect;
        }

        //write volatile
        numberOfTuples = currNumberOfTuples;
        return 1.0;
    }

    /**
     * @return
     *     the average selectivity of this histogram.
     *
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return this.avgSelectivity / this.numberOfTuples / this.numberOfTuples;
    }

    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
        int buckSteps = ((maxValue - minValue)/ histBins.length);
        if (buckSteps == 0)
            buckSteps++;

        int start = minValue;
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < histBins.length; i++) {
            s.append("BIN ").append(i).append(" START ").append(start).append(" END ").append(start + buckSteps).append(" HEIGHT ").append(histBins[i]).append("\n");
            start += buckSteps;
        }
        return s.toString();
    }
}
