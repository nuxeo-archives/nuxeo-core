package org.nuxeo.ecm.core.api.provider;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class TestMemoryListResultsProvider extends TestCase {
    
    ResultsProvider<String> provider2;
    
    ResultsProvider<String> provider3;
    
    ResultsProvider<String> provider12;
    
    List<String> providerContent;
    
    public void setUp() {
        providerContent = Arrays.asList("zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine", "ten");
        provider2 = new MemoryListResultsProvider<String>("twoByTwo",
                providerContent, 2, null);
        provider3 = new MemoryListResultsProvider<String>("threeByThree",
                providerContent, 3, null);
        provider12 = new MemoryListResultsProvider<String>("twelveBytwelve",
                providerContent, 12, null);
    }
    
    public void testDefaultState() {
        assertEquals("twoByTwo", provider2.getName());
        assertEquals("threeByThree", provider3.getName());
        assertEquals("twelveBytwelve", provider12.getName());
        
        assertEquals(0, provider2.getCurrentPageIndex());
        assertEquals(0, provider3.getCurrentPageIndex());
        assertEquals(0, provider12.getCurrentPageIndex());
        
        assertEquals(0, provider2.getCurrentPageOffset());
        assertEquals(0, provider3.getCurrentPageOffset());
        assertEquals(0, provider12.getCurrentPageOffset());
        
        assertEquals(2, provider2.getCurrentPageSize());
        assertEquals(3, provider3.getCurrentPageSize());
        assertEquals(11, provider12.getCurrentPageSize());
        
        assertEquals(2, provider2.getPageSize());
        assertEquals(3, provider3.getPageSize());
        assertEquals(12, provider12.getPageSize());
        
        assertEquals(11, provider2.getResultsCount());
        assertEquals(11, provider3.getResultsCount());
        assertEquals(11, provider12.getResultsCount());
        
        assertEquals(6, provider2.getNumberOfPages());
        assertEquals(4, provider3.getNumberOfPages());
        assertEquals(1, provider12.getNumberOfPages());
        
        assertEquals(Arrays.asList("zero", "one"), provider2.getCurrentPage());
        assertEquals(Arrays.asList("zero", "one", "two"), provider3.getCurrentPage());
        assertEquals(providerContent, provider12.getCurrentPage());
        
        assertEquals(false, provider2.isSortable());
        assertEquals(false, provider3.isSortable());
        assertEquals(false, provider12.isSortable());
        
        assertEquals(false, provider2.isPreviousPageAvailable());
        assertEquals(false, provider3.isPreviousPageAvailable());
        assertEquals(false, provider12.isPreviousPageAvailable());
        
        assertEquals(true, provider2.isNextPageAvailable());
        assertEquals(true, provider3.isNextPageAvailable());
        assertEquals(false, provider12.isNextPageAvailable());
    }

    public void testNext() throws ResultsProviderException {
        provider2.next();
        provider3.next();
        try {
            provider12.next();
            fail("should have raise ResultsProviderException");
        } catch (ResultsProviderException e) {
            // expected
        }
        
        assertEquals("twoByTwo", provider2.getName());
        assertEquals("threeByThree", provider3.getName());
        assertEquals("twelveBytwelve", provider12.getName());
        
        assertEquals(1, provider2.getCurrentPageIndex());
        assertEquals(1, provider3.getCurrentPageIndex());
        assertEquals(0, provider12.getCurrentPageIndex());
        
        assertEquals(2, provider2.getCurrentPageOffset());
        assertEquals(3, provider3.getCurrentPageOffset());
        assertEquals(0, provider12.getCurrentPageOffset());
        
        assertEquals(2, provider2.getCurrentPageSize());
        assertEquals(3, provider3.getCurrentPageSize());
        assertEquals(11, provider12.getCurrentPageSize());
        
        assertEquals(2, provider2.getPageSize());
        assertEquals(3, provider3.getPageSize());
        assertEquals(12, provider12.getPageSize());
        
        assertEquals(11, provider2.getResultsCount());
        assertEquals(11, provider3.getResultsCount());
        assertEquals(11, provider12.getResultsCount());
        
        assertEquals(6, provider2.getNumberOfPages());
        assertEquals(4, provider3.getNumberOfPages());
        assertEquals(1, provider12.getNumberOfPages());
        
        assertEquals(Arrays.asList("two", "three"), provider2.getCurrentPage());
        assertEquals(Arrays.asList("three", "four", "five"), provider3.getCurrentPage());
        assertEquals(providerContent, provider12.getCurrentPage());
        
        assertEquals(false, provider2.isSortable());
        assertEquals(false, provider3.isSortable());
        assertEquals(false, provider12.isSortable());
        
        assertEquals(true, provider2.isPreviousPageAvailable());
        assertEquals(true, provider3.isPreviousPageAvailable());
        assertEquals(false, provider12.isPreviousPageAvailable());
        
        assertEquals(true, provider2.isNextPageAvailable());
        assertEquals(true, provider3.isNextPageAvailable());
        assertEquals(false, provider12.isNextPageAvailable());
    }
    
    public void testLast() throws ResultsProviderException {
        provider2.last();
        provider3.last();
        provider12.last();
        
        assertEquals("twoByTwo", provider2.getName());
        assertEquals("threeByThree", provider3.getName());
        assertEquals("twelveBytwelve", provider12.getName());
        
        assertEquals(5, provider2.getCurrentPageIndex());
        assertEquals(3, provider3.getCurrentPageIndex());
        assertEquals(0, provider12.getCurrentPageIndex());
        
        assertEquals(10, provider2.getCurrentPageOffset());
        assertEquals(9, provider3.getCurrentPageOffset());
        assertEquals(0, provider12.getCurrentPageOffset());
        
        assertEquals(1, provider2.getCurrentPageSize());
        assertEquals(2, provider3.getCurrentPageSize());
        assertEquals(11, provider12.getCurrentPageSize());
        
        assertEquals(2, provider2.getPageSize());
        assertEquals(3, provider3.getPageSize());
        assertEquals(12, provider12.getPageSize());
        
        assertEquals(11, provider2.getResultsCount());
        assertEquals(11, provider3.getResultsCount());
        assertEquals(11, provider12.getResultsCount());
        
        assertEquals(6, provider2.getNumberOfPages());
        assertEquals(4, provider3.getNumberOfPages());
        assertEquals(1, provider12.getNumberOfPages());
        
        assertEquals(Arrays.asList("ten"), provider2.getCurrentPage());
        assertEquals(Arrays.asList("nine", "ten"), provider3.getCurrentPage());
        assertEquals(providerContent, provider12.getCurrentPage());
        
        assertEquals(false, provider2.isSortable());
        assertEquals(false, provider3.isSortable());
        assertEquals(false, provider12.isSortable());
        
        assertEquals(true, provider2.isPreviousPageAvailable());
        assertEquals(true, provider3.isPreviousPageAvailable());
        assertEquals(false, provider12.isPreviousPageAvailable());
        
        assertEquals(false, provider2.isNextPageAvailable());
        assertEquals(false, provider3.isNextPageAvailable());
        assertEquals(false, provider12.isNextPageAvailable());
    }
    
    
    public void testPrevious() throws ResultsProviderException {
        
        // previous from the first position will trigger errors
        try {
            provider2.previous();
            fail("should have raised ResultsProviderException");
        } catch (ResultsProviderException e) {
        }
        try {
            provider2.previous();
            fail("should have raised ResultsProviderException");
        } catch (ResultsProviderException e) {
        }
        try {
            provider12.previous();
            fail("should have raised ResultsProviderException");
        } catch (ResultsProviderException e) {
        }
        
        // go to page last - 1
        provider2.last();
        provider3.last();
        provider12.last();
        provider2.previous();
        provider3.previous();
        try {
            provider12.previous();
            fail("should have raised ResultsProviderException");
        } catch (ResultsProviderException e) {
        }
        
        assertEquals("twoByTwo", provider2.getName());
        assertEquals("threeByThree", provider3.getName());
        assertEquals("twelveBytwelve", provider12.getName());
        
        assertEquals(4, provider2.getCurrentPageIndex());
        assertEquals(2, provider3.getCurrentPageIndex());
        assertEquals(0, provider12.getCurrentPageIndex());
        
        assertEquals(8, provider2.getCurrentPageOffset());
        assertEquals(6, provider3.getCurrentPageOffset());
        assertEquals(0, provider12.getCurrentPageOffset());
        
        assertEquals(2, provider2.getCurrentPageSize());
        assertEquals(3, provider3.getCurrentPageSize());
        assertEquals(11, provider12.getCurrentPageSize());
        
        assertEquals(2, provider2.getPageSize());
        assertEquals(3, provider3.getPageSize());
        assertEquals(12, provider12.getPageSize());
        
        assertEquals(11, provider2.getResultsCount());
        assertEquals(11, provider3.getResultsCount());
        assertEquals(11, provider12.getResultsCount());
        
        assertEquals(6, provider2.getNumberOfPages());
        assertEquals(4, provider3.getNumberOfPages());
        assertEquals(1, provider12.getNumberOfPages());
        
        assertEquals(Arrays.asList("eight", "nine"), provider2.getCurrentPage());
        assertEquals(Arrays.asList("six", "seven", "eight"),
                provider3.getCurrentPage());
        assertEquals(providerContent, provider12.getCurrentPage());
        
        assertEquals(false, provider2.isSortable());
        assertEquals(false, provider3.isSortable());
        assertEquals(false, provider12.isSortable());
        
        assertEquals(true, provider2.isPreviousPageAvailable());
        assertEquals(true, provider3.isPreviousPageAvailable());
        assertEquals(false, provider12.isPreviousPageAvailable());
        
        assertEquals(true, provider2.isNextPageAvailable());
        assertEquals(true, provider3.isNextPageAvailable());
        assertEquals(false, provider12.isNextPageAvailable());
    }
    
    public void testRewind() throws ResultsProviderException {
        
        // go to page last then back to first
        provider2.last();
        provider3.last();
        provider12.last();
        provider2.rewind();
        provider3.rewind();
        provider12.rewind();
        
        testDefaultState();
    }
    
    public void testGetNextPage() throws ResultsProviderException {
        
        assertEquals(Arrays.asList("two", "three"), provider2.getNextPage());
        assertEquals("2/6", provider2.getCurrentPageStatus());
        assertEquals(Arrays.asList("three", "four", "five"), provider3.getNextPage());
        assertEquals("2/4", provider3.getCurrentPageStatus());
        
        try {
            provider12.getNextPage();
            fail("should have raised ResultsProviderException");
        } catch (ResultsProviderException e) {
        }
        assertEquals("1/1", provider12.getCurrentPageStatus());
        
        assertEquals(Arrays.asList("four", "five"), provider2.getNextPage());
        assertEquals("3/6", provider2.getCurrentPageStatus());
        assertEquals(Arrays.asList("six", "seven", "eight"), provider3.getNextPage());
        assertEquals("3/4", provider3.getCurrentPageStatus());
        
    }
    
    
    
}
