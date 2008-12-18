package org.nuxeo.ecm.core.api.provider;

import java.util.List;

import org.nuxeo.ecm.core.api.SortInfo;

public class MemoryListResultsProvider<E> implements ResultsProvider<E> {

    public List<E> getCurrentPage() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getCurrentPageIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getCurrentPageOffset() {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getCurrentPageSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public String getCurrentPageStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<E> getNextPage() throws ResultsProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNumberOfPages() {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<E> getPage(int page) throws ResultsProviderException {
        // TODO Auto-generated method stub
        return null;
    }

    public int getPageSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getResultsCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    public SortInfo getSortInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isNextPageAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isPreviousPageAvailable() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSortable() {
        // TODO Auto-generated method stub
        return false;
    }

    public void last() throws ResultsProviderException {
        // TODO Auto-generated method stub
        
    }

    public void next() throws ResultsProviderException {
        // TODO Auto-generated method stub
        
    }

    public void previous() throws ResultsProviderException {
        // TODO Auto-generated method stub
        
    }

    public void refresh() throws ResultsProviderException {
        // TODO Auto-generated method stub
        
    }

    public void rewind() throws ResultsProviderException {
        // TODO Auto-generated method stub
        
    }

    public void setName(String name) {
        // TODO Auto-generated method stub
        
    }

}
