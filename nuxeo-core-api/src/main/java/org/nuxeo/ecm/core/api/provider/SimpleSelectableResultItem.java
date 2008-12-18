/*
 * (C) Copyright 2008 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 * $Id $
 */
package org.nuxeo.ecm.core.api.provider;

import org.nuxeo.ecm.core.api.provider.SelectableResultsProvider.SelectableResultItem;

/**
 * Basic implementation of the SelectableResultItem that relies upon the
 * SelectableResultsProvider to hold the selection state of each item.
 * 
 * @author ogrisel
 * 
 * @param <E>
 */
public class SimpleSelectableResultItem<E> implements SelectableResultItem<E> {

    protected final E item;
    
    protected final SelectableResultsProvider<E> provider;

    public SimpleSelectableResultItem(SelectableResultsProvider<E> provider, E item) {
        this.item = item;
        this.provider = provider;
    }
    
    public E getData() {
        return item;
    }

    public SelectableResultsProvider<E> getProvider() {
        return provider;
    }

    public boolean isSelected() {
        return provider.isSelected(item);
    }

    public void select() {
        provider.select(item);
    }

    public void unselect() {
        provider.unselect(item);
    }

}
