package org.nuxeo.ecm.core.api.pagination;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class TestMemoryPages extends TestCase {
    
    Pages<String> pagesBy2;
    
    Pages<String> pagesBy3;
    
    Pages<String> pagesBy12;
    
    List<String> pagesContent;
    
    public void setUp() {
        pagesContent = Arrays.asList("zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine", "ten");
        pagesBy2 = new MemoryPages<String>("twoByTwo",
                pagesContent, 2, null);
        pagesBy3 = new MemoryPages<String>("threeByThree",
                pagesContent, 3, null);
        pagesBy12 = new MemoryPages<String>("twelveBytwelve",
                pagesContent, 12, null);
    }
    
    public void testDefaultState() {
        assertEquals("twoByTwo", pagesBy2.getName());
        assertEquals("threeByThree", pagesBy3.getName());
        assertEquals("twelveBytwelve", pagesBy12.getName());
        
        assertEquals(0, pagesBy2.getCurrentPageIndex());
        assertEquals(0, pagesBy3.getCurrentPageIndex());
        assertEquals(0, pagesBy12.getCurrentPageIndex());
        
        assertEquals(0, pagesBy2.getCurrentPageOffset());
        assertEquals(0, pagesBy3.getCurrentPageOffset());
        assertEquals(0, pagesBy12.getCurrentPageOffset());
        
        assertEquals(2, pagesBy2.getCurrentPageSize());
        assertEquals(3, pagesBy3.getCurrentPageSize());
        assertEquals(11, pagesBy12.getCurrentPageSize());
        
        assertEquals(2, pagesBy2.getPageSize());
        assertEquals(3, pagesBy3.getPageSize());
        assertEquals(12, pagesBy12.getPageSize());
        
        assertEquals(11, pagesBy2.getResultsCount());
        assertEquals(11, pagesBy3.getResultsCount());
        assertEquals(11, pagesBy12.getResultsCount());
        
        assertEquals(6, pagesBy2.getNumberOfPages());
        assertEquals(4, pagesBy3.getNumberOfPages());
        assertEquals(1, pagesBy12.getNumberOfPages());
        
        assertEquals(Arrays.asList("zero", "one"), pagesBy2.getCurrentPage());
        assertEquals(Arrays.asList("zero", "one", "two"), pagesBy3.getCurrentPage());
        assertEquals(pagesContent, pagesBy12.getCurrentPage());
        
        assertEquals(false, pagesBy2.isSortable());
        assertEquals(false, pagesBy3.isSortable());
        assertEquals(false, pagesBy12.isSortable());
        
        assertEquals(false, pagesBy2.isPreviousPageAvailable());
        assertEquals(false, pagesBy3.isPreviousPageAvailable());
        assertEquals(false, pagesBy12.isPreviousPageAvailable());
        
        assertEquals(true, pagesBy2.isNextPageAvailable());
        assertEquals(true, pagesBy3.isNextPageAvailable());
        assertEquals(false, pagesBy12.isNextPageAvailable());
    }

    public void testNext() throws PaginationException {
        pagesBy2.nextPage();
        pagesBy3.nextPage();
        try {
            pagesBy12.nextPage();
            fail("should have raise PaginationException");
        } catch (PaginationException e) {
            // expected
        }
        
        assertEquals("twoByTwo", pagesBy2.getName());
        assertEquals("threeByThree", pagesBy3.getName());
        assertEquals("twelveBytwelve", pagesBy12.getName());
        
        assertEquals(1, pagesBy2.getCurrentPageIndex());
        assertEquals(1, pagesBy3.getCurrentPageIndex());
        assertEquals(0, pagesBy12.getCurrentPageIndex());
        
        assertEquals(2, pagesBy2.getCurrentPageOffset());
        assertEquals(3, pagesBy3.getCurrentPageOffset());
        assertEquals(0, pagesBy12.getCurrentPageOffset());
        
        assertEquals(2, pagesBy2.getCurrentPageSize());
        assertEquals(3, pagesBy3.getCurrentPageSize());
        assertEquals(11, pagesBy12.getCurrentPageSize());
        
        assertEquals(2, pagesBy2.getPageSize());
        assertEquals(3, pagesBy3.getPageSize());
        assertEquals(12, pagesBy12.getPageSize());
        
        assertEquals(11, pagesBy2.getResultsCount());
        assertEquals(11, pagesBy3.getResultsCount());
        assertEquals(11, pagesBy12.getResultsCount());
        
        assertEquals(6, pagesBy2.getNumberOfPages());
        assertEquals(4, pagesBy3.getNumberOfPages());
        assertEquals(1, pagesBy12.getNumberOfPages());
        
        assertEquals(Arrays.asList("two", "three"), pagesBy2.getCurrentPage());
        assertEquals(Arrays.asList("three", "four", "five"), pagesBy3.getCurrentPage());
        assertEquals(pagesContent, pagesBy12.getCurrentPage());
        
        assertEquals(false, pagesBy2.isSortable());
        assertEquals(false, pagesBy3.isSortable());
        assertEquals(false, pagesBy12.isSortable());
        
        assertEquals(true, pagesBy2.isPreviousPageAvailable());
        assertEquals(true, pagesBy3.isPreviousPageAvailable());
        assertEquals(false, pagesBy12.isPreviousPageAvailable());
        
        assertEquals(true, pagesBy2.isNextPageAvailable());
        assertEquals(true, pagesBy3.isNextPageAvailable());
        assertEquals(false, pagesBy12.isNextPageAvailable());
    }
    
    public void testLast() throws PaginationException {
        pagesBy2.lastPage();
        pagesBy3.lastPage();
        pagesBy12.lastPage();
        
        assertEquals("twoByTwo", pagesBy2.getName());
        assertEquals("threeByThree", pagesBy3.getName());
        assertEquals("twelveBytwelve", pagesBy12.getName());
        
        assertEquals(5, pagesBy2.getCurrentPageIndex());
        assertEquals(3, pagesBy3.getCurrentPageIndex());
        assertEquals(0, pagesBy12.getCurrentPageIndex());
        
        assertEquals(10, pagesBy2.getCurrentPageOffset());
        assertEquals(9, pagesBy3.getCurrentPageOffset());
        assertEquals(0, pagesBy12.getCurrentPageOffset());
        
        assertEquals(1, pagesBy2.getCurrentPageSize());
        assertEquals(2, pagesBy3.getCurrentPageSize());
        assertEquals(11, pagesBy12.getCurrentPageSize());
        
        assertEquals(2, pagesBy2.getPageSize());
        assertEquals(3, pagesBy3.getPageSize());
        assertEquals(12, pagesBy12.getPageSize());
        
        assertEquals(11, pagesBy2.getResultsCount());
        assertEquals(11, pagesBy3.getResultsCount());
        assertEquals(11, pagesBy12.getResultsCount());
        
        assertEquals(6, pagesBy2.getNumberOfPages());
        assertEquals(4, pagesBy3.getNumberOfPages());
        assertEquals(1, pagesBy12.getNumberOfPages());
        
        assertEquals(Arrays.asList("ten"), pagesBy2.getCurrentPage());
        assertEquals(Arrays.asList("nine", "ten"), pagesBy3.getCurrentPage());
        assertEquals(pagesContent, pagesBy12.getCurrentPage());
        
        assertEquals(false, pagesBy2.isSortable());
        assertEquals(false, pagesBy3.isSortable());
        assertEquals(false, pagesBy12.isSortable());
        
        assertEquals(true, pagesBy2.isPreviousPageAvailable());
        assertEquals(true, pagesBy3.isPreviousPageAvailable());
        assertEquals(false, pagesBy12.isPreviousPageAvailable());
        
        assertEquals(false, pagesBy2.isNextPageAvailable());
        assertEquals(false, pagesBy3.isNextPageAvailable());
        assertEquals(false, pagesBy12.isNextPageAvailable());
    }
    
    
    public void testPrevious() throws PaginationException {
        
        // previous from the first position will trigger errors
        try {
            pagesBy2.previousPage();
            fail("should have raised PaginationException");
        } catch (PaginationException e) {
        }
        try {
            pagesBy2.previousPage();
            fail("should have raised PaginationException");
        } catch (PaginationException e) {
        }
        try {
            pagesBy12.previousPage();
            fail("should have raised PaginationException");
        } catch (PaginationException e) {
        }
        
        // go to page last - 1
        pagesBy2.lastPage();
        pagesBy3.lastPage();
        pagesBy12.lastPage();
        pagesBy2.previousPage();
        pagesBy3.previousPage();
        try {
            pagesBy12.previousPage();
            fail("should have raised PaginationException");
        } catch (PaginationException e) {
        }
        
        assertEquals("twoByTwo", pagesBy2.getName());
        assertEquals("threeByThree", pagesBy3.getName());
        assertEquals("twelveBytwelve", pagesBy12.getName());
        
        assertEquals(4, pagesBy2.getCurrentPageIndex());
        assertEquals(2, pagesBy3.getCurrentPageIndex());
        assertEquals(0, pagesBy12.getCurrentPageIndex());
        
        assertEquals(8, pagesBy2.getCurrentPageOffset());
        assertEquals(6, pagesBy3.getCurrentPageOffset());
        assertEquals(0, pagesBy12.getCurrentPageOffset());
        
        assertEquals(2, pagesBy2.getCurrentPageSize());
        assertEquals(3, pagesBy3.getCurrentPageSize());
        assertEquals(11, pagesBy12.getCurrentPageSize());
        
        assertEquals(2, pagesBy2.getPageSize());
        assertEquals(3, pagesBy3.getPageSize());
        assertEquals(12, pagesBy12.getPageSize());
        
        assertEquals(11, pagesBy2.getResultsCount());
        assertEquals(11, pagesBy3.getResultsCount());
        assertEquals(11, pagesBy12.getResultsCount());
        
        assertEquals(6, pagesBy2.getNumberOfPages());
        assertEquals(4, pagesBy3.getNumberOfPages());
        assertEquals(1, pagesBy12.getNumberOfPages());
        
        assertEquals(Arrays.asList("eight", "nine"), pagesBy2.getCurrentPage());
        assertEquals(Arrays.asList("six", "seven", "eight"),
                pagesBy3.getCurrentPage());
        assertEquals(pagesContent, pagesBy12.getCurrentPage());
        
        assertEquals(false, pagesBy2.isSortable());
        assertEquals(false, pagesBy3.isSortable());
        assertEquals(false, pagesBy12.isSortable());
        
        assertEquals(true, pagesBy2.isPreviousPageAvailable());
        assertEquals(true, pagesBy3.isPreviousPageAvailable());
        assertEquals(false, pagesBy12.isPreviousPageAvailable());
        
        assertEquals(true, pagesBy2.isNextPageAvailable());
        assertEquals(true, pagesBy3.isNextPageAvailable());
        assertEquals(false, pagesBy12.isNextPageAvailable());
    }
    
    public void testRewind() throws PaginationException {
        
        // go to page last then back to first
        pagesBy2.lastPage();
        pagesBy3.lastPage();
        pagesBy12.lastPage();
        pagesBy2.firstPage();
        pagesBy3.firstPage();
        pagesBy12.firstPage();
        
        testDefaultState();
    }
    
    public void testGetNextPage() throws PaginationException {
        
        assertEquals(Arrays.asList("two", "three"), pagesBy2.getNextPage());
        assertEquals("2/6", pagesBy2.getCurrentPageStatus());
        assertEquals(Arrays.asList("three", "four", "five"), pagesBy3.getNextPage());
        assertEquals("2/4", pagesBy3.getCurrentPageStatus());
        
        try {
            pagesBy12.getNextPage();
            fail("should have raised PaginationException");
        } catch (PaginationException e) {
        }
        assertEquals("1/1", pagesBy12.getCurrentPageStatus());
        
        assertEquals(Arrays.asList("four", "five"), pagesBy2.getNextPage());
        assertEquals("3/6", pagesBy2.getCurrentPageStatus());
        assertEquals(Arrays.asList("six", "seven", "eight"), pagesBy3.getNextPage());
        assertEquals("3/4", pagesBy3.getCurrentPageStatus());
        
    }
    
    
    
}
