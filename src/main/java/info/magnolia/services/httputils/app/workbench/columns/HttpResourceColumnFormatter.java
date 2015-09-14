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
package info.magnolia.services.httputils.app.workbench.columns;


import info.magnolia.ui.workbench.column.AbstractColumnFormatter;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Table;
import com.vaadin.data.Item;
import com.vaadin.data.Property;


public class HttpResourceColumnFormatter extends AbstractColumnFormatter<HttpResourceColumnDefinition> {

    private final Logger log = LoggerFactory.getLogger(HttpResourceColumnFormatter.class);

    public HttpResourceColumnFormatter(HttpResourceColumnDefinition definition) {
        super(definition);
    }

    @Override
    public Object generateCell(Table source, Object itemId, Object columnId) {
        Property property = null;
        String display = "";
        Item item = source.getItem(itemId);
        if (item != null) {
            property = item.getItemProperty(columnId);
            if (property != null) {
                Object propertyValue = property.getValue();
                if (propertyValue != null) {
                    if (property.getType().equals(Integer.class)) {
                        int amount = (Integer) property.getValue();
                        if (amount > 0) {
                            display = String.valueOf(propertyValue);
                        }
                    } else if (property.getType().equals(Date.class)) {
                        display = propertyValue.toString();
                    } else if (property.getType().equals(String.class)) {
                        display = (String) propertyValue;
                    } else {
                        display = propertyValue.toString();
                    }
                }
            }
        }
        boolean isNumber = property != null && property.getType().equals(Integer.class);
        String style = isNumber ? " style=\"text-align: right; display:block; width:100%;\"" : "";
        return "<span" + style + ">" + display.toString() + "</span>";
    }
}