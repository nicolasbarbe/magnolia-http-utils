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
package info.magnolia.services.httputils.app.contentconnector;


import info.magnolia.services.httputils.HttpService;
import info.magnolia.services.httputils.HttpUtilsModule;
import info.magnolia.services.httputils.app.container.HttpResourceFlatContainer;
import info.magnolia.services.httputils.definitions.ServiceDefinition;
import info.magnolia.services.httputils.app.item.HttpResourceItem;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.client.Client;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

public class HttpContentConnectorImpl implements HttpContentConnector {

    private HttpResourceFlatContainer container;

    @Inject
    public HttpContentConnectorImpl(HttpService.HttpServiceFactory factory, HttpContentConnectorDefinition definition) {
        HttpService service = factory.newInstance(definition.getServiceName());
        this.container = new HttpResourceFlatContainer(service);
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public String getItemUrlFragment(Object itemId) {
        return canHandleItem(itemId) ? (String) itemId : null;
    }

    @Override
    public Object getItemIdByUrlFragment(String urlFragment) {
        return canHandleItem(urlFragment) ? urlFragment : null;
    }

    @Override
    public Object getDefaultItemId() {
      return null;
    }

    @Override
    public Item getItem(Object itemId) {
        return canHandleItem(itemId) ? container.getItem(itemId) : null;
    }

    @Override
    public Object getItemId(Item item) {
        return item instanceof HttpResourceItem ? item.getItemProperty(HttpResourceItem.PROPERTY_ID) : null;
    }

    @Override
    public boolean canHandleItem(Object itemId) {
        return itemId instanceof String && ((String) itemId).matches("^[0-9]");
    }
}
