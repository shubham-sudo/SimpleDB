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
 * @see simpledb.HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    private final TupleDesc tupleDesc;
    private final File file;
    private RandomAccessFile raf;

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *          the file that stores the on-disk backing store for this heap
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
        // some code goes here
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
        // some code goes here
        DbFileIterator it = new DbFileIterator() {
            int currentPage = 0;
            Iterator<Tuple> tIterator;

            @Override
            public void open() throws DbException, TransactionAbortedException {
                HeapPageId pid = new HeapPageId(getId(), currentPage);
                tIterator = getTuplesIterator(pid);
            }

            private Iterator<Tuple> getTuplesIterator(HeapPageId pid) throws TransactionAbortedException, DbException {
                HeapPage page = (HeapPage) Database.getBufferPool().getPage(tid, pid, Permissions.READ_ONLY);
                return page.iterator();
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (tIterator == null) {
                    return false;
                } else if (tIterator.hasNext()) {
                    return true;
                }
                if (currentPage < numPages() - 1) {
                    currentPage++;
                    HeapPageId pid = new HeapPageId(getId(), currentPage);
                    tIterator = getTuplesIterator(pid);
                    return tIterator.hasNext();
                } else {
                    return false;
                }
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (!hasNext()) {
                    throw new NoSuchElementException("No Such Element Found");
                }
                return tIterator.next();
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                currentPage = 0;
                open();
            }

            @Override
            public void close() {
                currentPage = 0;
                tIterator = null;
            }

        };
        return it;
    }
}
