package org.nuxeo.ecm.core.api.pagination;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.api.pagination.PaginationException;
import org.nuxeo.ecm.core.api.pagination.SelectablePages;
import org.nuxeo.ecm.core.api.pagination.SimpleSelectablePages;
import org.nuxeo.ecm.core.api.pagination.SelectablePages.SelectablePageElement;

public class TestSimpleSelectablePages extends
        TestMemoryPages {

    SelectablePages<String> spagesBy2;

    SelectablePages<String> spagesBy3;

    SelectablePages<String> spagesBy12;

    public void setUp() {
        pagesContent = Arrays.asList("zero", "one", "two", "three", "four",
                "five", "six", "seven", "eight", "nine", "ten");

        spagesBy2 = new SimpleSelectablePages<String>("twoByTwo",
                pagesContent, 2, null);
        pagesBy2 = spagesBy2;

        spagesBy3 = new SimpleSelectablePages<String>(
                "threeByThree", pagesContent, 3, null);
        pagesBy3 = spagesBy3;

        spagesBy12 = new SimpleSelectablePages<String>(
                "twelveBytwelve", pagesContent, 12, null);
        pagesBy12 = spagesBy12;
    }

    public void testSelectOnDefaultPage() throws Exception {

        List<SelectablePageElement<String>> selectableCurrentPage2 = spagesBy2.getSelectableCurrentPage();
        List<SelectablePageElement<String>> selectableCurrentPage3 = spagesBy3.getSelectableCurrentPage();
        List<SelectablePageElement<String>> selectableCurrentPage12 = spagesBy12.getSelectableCurrentPage();

        // selectable rows have same size as regular paged lists
        assertEquals(2, selectableCurrentPage2.size());
        assertEquals(3, selectableCurrentPage3.size());
        assertEquals(11, selectableCurrentPage12.size());

        // nothing selected by default
        assertEquals(Collections.emptyList(),
                spagesBy2.getSelectedElements());
        assertEquals(Collections.emptyList(),
                spagesBy3.getSelectedElements());
        assertEquals(Collections.emptyList(),
                spagesBy12.getSelectedElements());

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

        // select some results from the pagination API
        spagesBy2.select("one");
        try {
            spagesBy2.select("two");
            fail("should not be able to select elements that are not present in the current page");
        } catch (PaginationException e) {
        }
        spagesBy3.select("one");
        spagesBy3.select("two");
        spagesBy12.select("one");
        spagesBy12.select("two");

        assertEquals(Arrays.asList("one"), spagesBy2.getSelectedElements());
        assertEquals(Arrays.asList("one", "two"),
                spagesBy3.getSelectedElements());
        assertEquals(Arrays.asList("one", "two"),
                spagesBy12.getSelectedElements());

        selectableCurrentPage2 = spagesBy2.getSelectableCurrentPage();
        selectableCurrentPage3 = spagesBy3.getSelectableCurrentPage();

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
        spagesBy2.nextPage();
        spagesBy3.nextPage();
        assertEquals(Arrays.asList("one"), spagesBy2.getSelectedElements());
        assertEquals(Arrays.asList("one", "two"),
                spagesBy3.getSelectedElements());

        // we can now select "two" in spagesBy2
        spagesBy2.select("two");
        assertEquals(Arrays.asList("one", "two"),
                spagesBy2.getSelectedElements());

        List<SelectablePageElement<String>> selectableCurrentPage2 = spagesBy2.getSelectableCurrentPage();
        List<SelectablePageElement<String>> selectableCurrentPage3 = spagesBy3.getSelectableCurrentPage();

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
        assertEquals(false, spagesBy2.isCurrentPageSelected());
        spagesBy2.unselectCurrentPage();
        assertEquals("two", selectableCurrentPage2.get(0).getData());
        assertEquals("three", selectableCurrentPage2.get(1).getData());
        assertEquals(false, selectableCurrentPage2.get(0).isSelected());
        assertEquals(false, selectableCurrentPage2.get(1).isSelected());
        // there is still one element selected on the first page of
        // spagesBy2:
        assertEquals(Arrays.asList("one"), spagesBy2.getSelectedElements());

        assertEquals(false, spagesBy3.isCurrentPageSelected());
        spagesBy3.unselectCurrentPage();
        assertEquals("three", selectableCurrentPage3.get(0).getData());
        assertEquals("four", selectableCurrentPage3.get(1).getData());
        assertEquals("five", selectableCurrentPage3.get(2).getData());
        assertEquals(false, selectableCurrentPage3.get(0).isSelected());
        assertEquals(false, selectableCurrentPage3.get(1).isSelected());
        assertEquals(false, selectableCurrentPage3.get(2).isSelected());
        // there are still elements selected on the first page of
        // spagesBy3:
        assertEquals(Arrays.asList("one", "two"),
                spagesBy3.getSelectedElements());

        // select all
        spagesBy2.selectCurrentPage();
        assertEquals("two", selectableCurrentPage2.get(0).getData());
        assertEquals("three", selectableCurrentPage2.get(1).getData());
        assertEquals(true, selectableCurrentPage2.get(0).isSelected());
        assertEquals(true, selectableCurrentPage2.get(1).isSelected());
        assertEquals(true, spagesBy2.isCurrentPageSelected());
        assertEquals(Arrays.asList("one", "two", "three"),
                spagesBy2.getSelectedElements());

        spagesBy3.selectCurrentPage();
        assertEquals("three", selectableCurrentPage3.get(0).getData());
        assertEquals("four", selectableCurrentPage3.get(1).getData());
        assertEquals("five", selectableCurrentPage3.get(2).getData());
        assertEquals(true, selectableCurrentPage3.get(0).isSelected());
        assertEquals(true, selectableCurrentPage3.get(1).isSelected());
        assertEquals(true, selectableCurrentPage3.get(2).isSelected());
        assertEquals(true, spagesBy3.isCurrentPageSelected());
        assertEquals(Arrays.asList("one", "two", "three", "four", "five"),
                spagesBy3.getSelectedElements());
    }

    public void testSelectFromSelectableResultItem() throws Exception {
        List<SelectablePageElement<String>> selectablePage = spagesBy2.getSelectableCurrentPage();
        assertEquals(2, selectablePage.size());
        assertEquals(false, selectablePage.get(0).isSelected());
        assertEquals(false, selectablePage.get(1).isSelected());

        selectablePage.get(0).select();
        assertEquals(true, selectablePage.get(0).isSelected());
        assertEquals(Arrays.asList("zero"), spagesBy2.getSelectedElements());

        selectablePage.get(1).select();
        assertEquals(true, selectablePage.get(1).isSelected());
        assertEquals(Arrays.asList("zero", "one"),
                spagesBy2.getSelectedElements());

        selectablePage.get(0).unselect();
        assertEquals(false, selectablePage.get(0).isSelected());
        assertEquals(Arrays.asList("one"), spagesBy2.getSelectedElements());

        // unselecting twice should not do anything
        selectablePage.get(0).unselect();
        assertEquals(false, selectablePage.get(0).isSelected());
        assertEquals(Arrays.asList("one"), spagesBy2.getSelectedElements());

        spagesBy2.nextPage();
        spagesBy2.getSelectableCurrentPage().get(1).select();
        assertEquals(Arrays.asList("one", "three"),
                spagesBy2.getSelectedElements());

        spagesBy2.previousPage();
        spagesBy2.getSelectableCurrentPage().get(1).unselect();
        assertEquals(false, selectablePage.get(1).isSelected());
        assertEquals(Arrays.asList("three"),
                spagesBy2.getSelectedElements());

        spagesBy2.nextPage();
        spagesBy2.getSelectableCurrentPage().get(1).unselect();
        assertEquals(Collections.emptyList(),
                spagesBy2.getSelectedElements());
    }

    public void testListeners() throws Exception {
        // TODO
    }

}
