/**
 * This file Copyright (c) 2012-2014 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This file is dual-licensed under both the Magnolia
 * Network Agreement and the GNU General Public License.
 * You may elect to use one or the other of these licenses.
 *
 * This file is distributed in the hope that it will be
 * useful, but AS-IS and WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, or NONINFRINGEMENT.
 * Redistribution, except as permitted by whichever of the GPL
 * or MNA you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or
 * modify this file under the terms of the GNU General
 * Public License, Version 3, as published by the Free Software
 * Foundation.  You should have received a copy of the GNU
 * General Public License, Version 3 along with this program;
 * if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * 2. For the Magnolia Network Agreement (MNA), this file
 * and the accompanying materials are made available under the
 * terms of the MNA which accompanies this distribution, and
 * is available at http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 */
package info.magnolia.services.httputils.app.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.AbstractContainer;

import info.magnolia.services.httputils.HttpService;
import info.magnolia.services.httputils.app.item.HttpResourceItem;
import info.magnolia.services.httputils.app.item.HttpResourceItemImpl;
import info.magnolia.ui.workbench.container.Refreshable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;


public class HttpResourceFlatContainer extends AbstractContainer implements Container, Container.Indexed, Refreshable {

    private static Logger log = LoggerFactory.getLogger(HttpResourceFlatContainer.class);

    private final HttpService service;
    private Map<Object, Class> properties = new HashMap<Object, Class>();
    private Map<String, HttpResourceItem> cache = new LinkedHashMap<String, HttpResourceItem>();


    public HttpResourceFlatContainer(HttpService service) {
        this.service = service;
        configure();
    }

    @Override
    public void refresh() {
        removeAllItems();
        loadAll();
    }


    @Override
    public Item getItem(Object itemId) {
        return cache.get(itemId);
    }

    @Override
    public Collection<?> getItemIds() {
        return cache.keySet();
    }

    @Override
    public Property getContainerProperty(Object itemId, Object propertyId) {
        HttpResourceItem item = (HttpResourceItem) getItem(itemId);

        if (item != null) {
            Property property = item.getItemProperty(propertyId);
            if (property != null) {
                return property;
            }
        }

        return null;
    }

    @Override
    public Class<?> getType(Object propertyId) {
        return HttpResourceItem.IDs.getIDs().get(propertyId);
    }

    @Override
    public int size() {
        return this.cache.size();
    }

    @Override
    public boolean containsId(Object itemId) {
        return cache.containsKey(itemId);
    }

    @Override
    public Item addItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    @Override
    public boolean removeItem(Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    @Override
    public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue) throws UnsupportedOperationException {
        properties.put(propertyId, type);
        return properties.containsKey(propertyId);
    }

    @Override
    public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
        properties.remove(propertyId);
        return !properties.containsKey(propertyId);
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        cache.clear();
        return cache.size() == 0;
    }

    @Override
    public Collection<?> getContainerPropertyIds() {
        return HttpResourceItem.IDs.getIDs().keySet();
    }



    @Override
    public int indexOfId(Object itemId) {
        List<String> keys = new ArrayList<String>(cache.keySet());
        return keys.indexOf(itemId);
    }

    @Override
    public Object getIdByIndex(int index) {
        List<String> keys = new ArrayList<String>(cache.keySet());
        return keys.get(index);
    }

    @Override
    public List<?> getItemIds(int startIndex, int numberOfItems) {
        if (cache == null) {
            loadAll();
        }
        return new ArrayList<String>(cache.keySet());
    }

    @Override
    public Object addItemAt(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    @Override
    public Item addItemAt(int index, Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    @Override
    public Object nextItemId(Object itemId) {
        List<String> keys = new ArrayList<String>(cache.keySet());
        int index = keys.indexOf(itemId);
        if (index < keys.size() - 1) {
            return keys.get(index + 1);
        }
        return null;
    }

    @Override
    public Object prevItemId(Object itemId) {
        List<String> keys = new ArrayList<String>(cache.keySet());
        int index = keys.indexOf(itemId);
        if (index > 0) {
            return keys.get(index - 1);
        }
        return null;
    }

    @Override
    public Object firstItemId() {
        List<String> keys = new ArrayList<String>(cache.keySet());
        return keys.get(0);
    }

    @Override
    public Object lastItemId() {
        List<String> keys = new ArrayList<String>(cache.keySet());
        return keys.get(keys.size() - 1);
    }

    @Override
    public boolean isFirstId(Object itemId) {
        List<String> keys = new ArrayList<String>(cache.keySet());
        return keys.get(0).equals(itemId);
    }

    @Override
    public boolean isLastId(Object itemId) {
        List<String> keys = new ArrayList<String>(cache.keySet());
        return keys.get(keys.size() - 1).equals(itemId);
    }

    @Override
    public Object addItemAfter(Object previousItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    @Override
    public Item addItemAfter(Object previousItemId, Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Class does NOT SUPPORT this method.");
    }

    private void configure() {
        Map<String, Class> props = HttpResourceItem.IDs.getIDs();
        for (String id : props.keySet()) {
            addContainerProperty(id, props.get(id), null);
        }
    }

    private void loadAll() {
        List resourceList = (List) this.service.GET();
        for(Object resource : resourceList ) {
            if(resource instanceof Map){
                Object objectId =  ((Map) resource).get("id");

                // todo : stupid logic which must be changed
                // BTW the complete logic for (un)marshalling object to ui as to be rewritten
                String id;
                if(objectId instanceof Integer) {
                    id = String.valueOf((Integer)objectId);
                } else if (objectId instanceof  String) {
                    id = (String) objectId;
                } else {
                    throw new RuntimeException("Unsupported type for an identifier");
                }

                this.cache.put(id, new HttpResourceItemImpl((Map) resource));
            } else {
                throw new RuntimeException("Unmanaged object type");
            }
        }
    }
}
