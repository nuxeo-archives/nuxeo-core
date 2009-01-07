package org.nuxeo.ecm.core.api.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.api.provider.SelectableResultsProvider.SelectableResultItem;

public class TestSimpleSelectableResultsProvider extends
        TestMemoryListResultsProvider {

    SelectableResultsProvider<String> sprovider2;

    SelectableResultsProvider<String> sprovider3;

    SelectableResultsProvider<String> sprovider12;

    public void setUp() {
        providerContent = Arrays.asList("zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine", "ten");

        sprovider2 = new SimpleSelectableResultsProvider<String>("twoByTwo",
                providerContent, 2, null);
        provider2 = sprovider2;

        sprovider3 = new SimpleSelectableResultsProvider<String>(
                "threeByThree", providerContent, 3, null);
        provider3 = sprovider3;

        sprovider12 = new SimpleSelectableResultsProvider<String>(
                "twelveBytwelve", providerContent, 12, null);
        provider12 = sprovider12;
    }

    public void testSelectOnDefaultPage() throws Exception {

        List<SelectableResultItem<String>> selectableCurrentPage2 = sprovider2.getSelectableCurrentPage();
        List<SelectableResultItem<String>> selectableCurrentPage3 = sprovider3.getSelectableCurrentPage();
        List<SelectableResultItem<String>> selectableCurrentPage12 = sprovider12.getSelectableCurrentPage();

        // selectable rows have same size as regular paged lists
        assertEquals(2, selectableCurrentPage2.size());
        assertEquals(3, selectableCurrentPage3.size());
        assertEquals(11, selectableCurrentPage12.size());

        // nothing selected by default
        assertEquals(Collections.emptyList(),
                sprovider2.getSelectedResultItems());
        assertEquals(Collections.emptyList(),
                sprovider3.getSelectedResultItems());
        assertEquals(Collections.emptyList(),
                sprovider12.getSelectedResultItems());

        assertEquals("zero", selectableCurrentPage2.get(0).getData());
        assertEquals("one", selectableCurrentPage2.get(1).getData());
        assertEquals(false, selectableCurrentPage2.get(0).isSelected());
        assertEquals(false, selectableCurrentPage2.get(1).isSelected());

        assertEquals("zero", selectableCurrentPage3.get(0).getData());
        assertEquals("one", selectableCurrentPage3.get(1).getData());
        assertEquals("two", selectableCurrentPage3.get(2).getData());
        assertEquals(false, selectableCurrentPage3.get(0).isSelected());
        assertEquals(false, selectableCurrentPage3.get(1).isSelected());
        assertEquals(false, selectableCurrentPage3.get(2).isSelected());

        // select some results from the provider API
        sprovider2.select("one");
        try {
            sprovider2.select("two");
            fail("should not be able to select elements that are not present in the current page");
        } catch (ResultsProviderException e) {
        }
        sprovider3.select("one");
        sprovider3.select("two");
        sprovider12.select("one");
        sprovider12.select("two");

        assertEquals(Arrays.asList("one"), sprovider2.getSelectedResultItems());
        assertEquals(Arrays.asList("one", "two"),
                sprovider3.getSelectedResultItems());
        assertEquals(Arrays.asList("one", "two"),
                sprovider12.getSelectedResultItems());

        selectableCurrentPage2 = sprovider2.getSelectableCurrentPage();
        selectableCurrentPage3 = sprovider3.getSelectableCurrentPage();

        assertEquals("zero", selectableCurrentPage2.get(0).getData());
        assertEquals("one", selectableCurrentPage2.get(1).getData());
        assertEquals(false, selectableCurrentPage2.get(0).isSelected());
        assertEquals(true, selectableCurrentPage2.get(1).isSelected());

        assertEquals("zero", selectableCurrentPage3.get(0).getData());
        assertEquals("one", selectableCurrentPage3.get(1).getData());
        assertEquals("two", selectableCurrentPage3.get(2).getData());
        assertEquals(false, selectableCurrentPage3.get(0).isSelected());
        assertEquals(true, selectableCurrentPage3.get(1).isSelected());
        assertEquals(true, selectableCurrentPage3.get(2).isSelected());
        
    }

    public void testSelectOnMultiplePages() throws Exception {

        // reproduce the previous selection state on the first page
        testSelectOnDefaultPage();
        
        // navigation should not affect the global selection but will change the
        // current row
        sprovider2.next();
        sprovider3.next();
        assertEquals(Arrays.asList("one"), sprovider2.getSelectedResultItems());
        assertEquals(Arrays.asList("one", "two"),
                sprovider3.getSelectedResultItems());

        // we can now select "two" in sprovider2
        sprovider2.select("two");
        assertEquals(Arrays.asList("one", "two"),
                sprovider2.getSelectedResultItems());

        List<SelectableResultItem<String>> selectableCurrentPage2 = sprovider2.getSelectableCurrentPage();
        List<SelectableResultItem<String>> selectableCurrentPage3 = sprovider3.getSelectableCurrentPage();

        assertEquals("two", selectableCurrentPage2.get(0).getData());
        assertEquals("three", selectableCurrentPage2.get(1).getData());
        assertEquals(true, selectableCurrentPage2.get(0).isSelected());
        assertEquals(false, selectableCurrentPage2.get(1).isSelected());

        assertEquals("three", selectableCurrentPage3.get(0).getData());
        assertEquals("four", selectableCurrentPage3.get(1).getData());
        assertEquals("five", selectableCurrentPage3.get(2).getData());
        assertEquals(false, selectableCurrentPage3.get(0).isSelected());
        assertEquals(false, selectableCurrentPage3.get(1).isSelected());
        assertEquals(false, selectableCurrentPage3.get(2).isSelected());

        // let use unselect the current page elements
        assertEquals(false, sprovider2.isAllSelected());
        sprovider2.unselectAll();
        assertEquals("two", selectableCurrentPage2.get(0).getData());
        assertEquals("three", selectableCurrentPage2.get(1).getData());
        assertEquals(false, selectableCurrentPage2.get(0).isSelected());
        assertEquals(false, selectableCurrentPage2.get(1).isSelected());
        // there is still one element selected on the first page of
        // sprovider2:
        assertEquals(Arrays.asList("one"), sprovider2.getSelectedResultItems());

        assertEquals(false, sprovider3.isAllSelected());
        sprovider3.unselectAll();
        assertEquals("three", selectableCurrentPage3.get(0).getData());
        assertEquals("four", selectableCurrentPage3.get(1).getData());
        assertEquals("five", selectableCurrentPage3.get(2).getData());
        assertEquals(false, selectableCurrentPage3.get(0).isSelected());
        assertEquals(false, selectableCurrentPage3.get(1).isSelected());
        assertEquals(false, selectableCurrentPage3.get(2).isSelected());
        // there are still elements selected on the first page of
        // sprovider3:
        assertEquals(Arrays.asList("one", "two"),
                sprovider3.getSelectedResultItems());

        // select all
        sprovider2.selectAll();
        assertEquals("two", selectableCurrentPage2.get(0).getData());
        assertEquals("three", selectableCurrentPage2.get(1).getData());
        assertEquals(true, selectableCurrentPage2.get(0).isSelected());
        assertEquals(true, selectableCurrentPage2.get(1).isSelected());
        assertEquals(true, sprovider2.isAllSelected());
        assertEquals(Arrays.asList("one", "two", "three"),
                sprovider2.getSelectedResultItems());

        sprovider3.selectAll();
        assertEquals("three", selectableCurrentPage3.get(0).getData());
        assertEquals("four", selectableCurrentPage3.get(1).getData());
        assertEquals("five", selectableCurrentPage3.get(2).getData());
        assertEquals(true, selectableCurrentPage3.get(0).isSelected());
        assertEquals(true, selectableCurrentPage3.get(1).isSelected());
        assertEquals(true, selectableCurrentPage3.get(2).isSelected());
        assertEquals(true, sprovider3.isAllSelected());
        assertEquals(Arrays.asList("one", "two", "three", "four", "five"),
                sprovider3.getSelectedResultItems());
    }

    public void testSelectFromSelectableResultItem() throws Exception {
        List<SelectableResultItem<String>> selectablePage = sprovider2.getSelectableCurrentPage();
        assertEquals(2, selectablePage.size());
        assertEquals(false, selectablePage.get(0).isSelected());
        assertEquals(false, selectablePage.get(1).isSelected());

        selectablePage.get(0).select();
        assertEquals(true, selectablePage.get(0).isSelected());
        assertEquals(Arrays.asList("zero"), sprovider2.getSelectedResultItems());

        selectablePage.get(1).select();
        assertEquals(true, selectablePage.get(1).isSelected());
        assertEquals(Arrays.asList("zero", "one"),
                sprovider2.getSelectedResultItems());

        selectablePage.get(0).unselect();
        assertEquals(false, selectablePage.get(0).isSelected());
        assertEquals(Arrays.asList("one"), sprovider2.getSelectedResultItems());

        // unselecting twice should not do anything
        selectablePage.get(0).unselect();
        assertEquals(false, selectablePage.get(0).isSelected());
        assertEquals(Arrays.asList("one"), sprovider2.getSelectedResultItems());

        sprovider2.next();
        sprovider2.getSelectableCurrentPage().get(1).select();
        assertEquals(Arrays.asList("one", "three"),
                sprovider2.getSelectedResultItems());

        sprovider2.previous();
        sprovider2.getSelectableCurrentPage().get(1).unselect();
        assertEquals(false, selectablePage.get(1).isSelected());
        assertEquals(Arrays.asList("three"),
                sprovider2.getSelectedResultItems());

        sprovider2.next();
        sprovider2.getSelectableCurrentPage().get(1).unselect();
        assertEquals(Collections.emptyList(),
                sprovider2.getSelectedResultItems());
    }

    public void testListeners() throws Exception {
        // TODO
    }

}
