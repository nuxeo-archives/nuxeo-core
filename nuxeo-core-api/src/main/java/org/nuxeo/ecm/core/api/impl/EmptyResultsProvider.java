/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 *
 * $Id: JOOoConvertPluginImpl.java 18651 2007-05-13 20:28:53Z sfermigier $
 */

package org.nuxeo.ecm.core.api.impl;

import java.util.Collections;
import java.util.List;

import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.provider.ResultsProvider;
import org.nuxeo.ecm.core.api.provider.ResultsProviderException;

public class EmptyResultsProvider<E> implements ResultsProvider<E> {

    private static final long serialVersionUID = 1L;

    public List<E> getCurrentPage() {
        return Collections.emptyList();
    }

    public int getCurrentPageIndex() {
        return 0;
    }

    public int getCurrentPageOffset() {
        return 0;
    }

    public int getCurrentPageSize() {
        return 0;
    }

    public String getCurrentPageStatus() {
        return "";
    }

    public List<E> getNextPage() {
        return null;
    }

    public int getNumberOfPages() {
        return 0;
    }

    public List<E> getPage(int page) {
        return null;
    }

    public long getResultsCount() {
        return 0;
    }

    public boolean isNextPageAvailable() {
        return false;
    }

    public boolean isPreviousPageAvailable() {
        return false;
    }

    public void last() {
    }

    public void next() {
    }

    public void previous() {
    }

    public void refresh() throws ResultsProviderException {
    }

    public void rewind() {
    }

    public int getPageSize() {
        return 0;
    }

    public String getName() {
        return null;
    }

    public SortInfo getSortInfo() {
        return null;
    }

    public boolean isSortable() {
        return false;
    }

    public void setName(String name) {
    }

}
