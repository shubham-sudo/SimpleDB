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
        // throw new UnsupportedOperationException("implement this");
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return tupleDesc;
        // throw new UnsupportedOperationException("implement this");
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        byte[] data = new byte[BufferPool.getPageSize()];
        Page page;
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
        return null;
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
            BufferPool bufferPool;
            RandomAccessFile rf;
            int currentPage = 0;
            Iterator<Tuple> tIterator;
            boolean isClosed = true;
            Page page;

            @Override
            public void open() throws DbException, TransactionAbortedException {
                try {
                    rf = new RandomAccessFile(file, "r");
                    bufferPool = new BufferPool(BufferPool.DEFAULT_PAGES);
                    isClosed = false;
                } catch (IOException e) {
                    throw new DbException(e.getMessage());
                }
            }

            @Override
            public boolean hasNext() throws DbException, TransactionAbortedException {
                if (rf == null || (currentPage == numPages() && !tIterator.hasNext())) {
                    return false;
                }
                return true;
            }

            @Override
            public Tuple next() throws DbException, TransactionAbortedException, NoSuchElementException {
                if (rf == null || isClosed) {
                    throw new NoSuchElementException();
                }
                if (page == null || tIterator == null || !tIterator.hasNext()) {
                    page = bufferPool.getPage(tid, new HeapPageId(getId(), currentPage), null);
                    try {
                        tIterator = (new HeapPage(new HeapPageId(getId(), currentPage), page.getPageData())).iterator();
                    } catch (IOException e) {
                        throw new DbException(e.getMessage());
                    }
                    currentPage++;
                }
                try {
                    return tIterator.next();
                } catch (Exception e) {
                    throw new NoSuchElementException(e.getMessage());
                }
            }

            @Override
            public void rewind() throws DbException, TransactionAbortedException {
                // TODO Auto-generated method stub
                page = null;
                tIterator = null;
                currentPage = 0;
                open();
            }

            @Override
            public void close() {
                try {
                    rf.close();
                    isClosed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
        return it;
    }
}
