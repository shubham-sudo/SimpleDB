package simpledb;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 *
 * @author Sam Madden
 * @see simpledb.HeapPage#HeapPage
 */
public class HeapFile implements DbFile {

    private final TupleDesc tupleDesc;
    private final File file;
    private RandomAccessFile raf;

    /**
     * Constructs a heap file backed by the specified file.
     *
     * @param f the file that stores the on-disk backing store for this heap
     *          file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        file = f;
        tupleDesc = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     *
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     *
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        return file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     *
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return tupleDesc;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // This method does the job of reading a page from the disk
        // the use of RandomAccessFile is to randomly access the file
        // from any location using seek and return the next page
        byte[] data = new byte[BufferPool.getPageSize()];
        Page page = null;
        int startAddress = (BufferPool.getPageSize() * pid.pageNumber());
        try {
            raf = new RandomAccessFile(file, "r");
            raf.seek(startAddress);
            raf.read(data);
            page = new HeapPage((HeapPageId) pid, data);
            return page;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return page;
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        int numberOfPages = 0;
        try {
            raf = new RandomAccessFile(file, "r");
            numberOfPages = (int) raf.length() / BufferPool.getPageSize();
            raf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return numberOfPages;
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public ArrayList<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // Iterator which helps to iterate over the tuples of a heap file
        // the idea to implement this is: use the iterator of the page
        // to iterate over the tuples stored on it and whenever the iterator
        // finishes the iteration of one page, assign the iterator of
        // next page from the heap file if exists.
        DbFileIterator it = new DbFileIterator() {
            int currentPage = 0;
            Iterator<Tuple> tuplesIterator;

            @Override
            public void open() throws DbException, TransactionAbortedException {
                HeapPageId pid = new HeapPageId(getId(), currentPage);
                tuplesIterator = getTuplesIterator(pid);
            }

            private Iterator<Tuple> getTuplesIterator(HeapPageId pid) throws TransactionAbortedException, DbException {
                HeapPage page = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
                return page.iterator();
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (tuplesIterator == null) {
                    return false;
                } else if (tuplesIterator.hasNext()) {
                    return true;
                }
                if (currentPage < numPages() - 1) {
                    currentPage++;
                    HeapPageId pid = new HeapPageId(getId(), currentPage);
                    tuplesIterator = getTuplesIterator(pid);
                    return tuplesIterator.hasNext();
                } else {
                    return false;
                }
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("No Such Element Found");
                }
                return tuplesIterator.next();
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                currentPage = 0;
                open();
            }

            @Override
            public void close() {
                currentPage = 0;
                tuplesIterator = null;
            }

        };
        return it;
    }
}
