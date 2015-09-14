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
package info.magnolia.services.httputils.filters;

import info.magnolia.cms.filters.AbstractMgnlFilter;
import info.magnolia.services.httputils.HttpService;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Proof of concept of a proxy filter redirecting external requests
 * to an internal service, which is ideally an HTTP endpoint.
 *
 * Note this is a naive implementation not production grade.
 */
public class InternalProxyFilter  extends AbstractMgnlFilter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(InternalProxyFilter.class);

    private static final String FILTER_CONTEXT = ".proxy";

    private HttpService service;

    @Inject
    public InternalProxyFilter(HttpService client) {
        this.service = service;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String[] requestedUrl = request.getRequestURI().split("/", 4);

        if( !requestedUrl[1].equals(FILTER_CONTEXT ))
            throw new ServletException("Requested url does not have the right context, please check the configuration of the filter.");

        if( StringUtils.isBlank(requestedUrl[2]))
            throw new ServletException("Service name is missing.");

        if( StringUtils.isBlank(requestedUrl[3]))
            throw new ServletException("No resources specified in the url");

        // calculate internal url
        String service = requestedUrl[2];
        String resource = requestedUrl[3];

        WebTarget target = null;
        //this.client.target("http://192.168.99.100:3004/").path(resource);


        // add query string
        String queryString = request.getQueryString();
        if(StringUtils.isNotBlank(queryString))
            target = target.path(queryString);


        // set content type
        String contentType = request.getContentType();

        Invocation.Builder requestBuilder;
        if(StringUtils.isNotBlank(contentType))
            requestBuilder = target.request(contentType);
        else
            requestBuilder = target.request();


        // copy the original request body to the internal one
        StringBuilder internalBodyReq = new StringBuilder();
        BufferedReader reader = request.getReader();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                internalBodyReq.append(line);
            }
        } finally {
            reader.close();
        }


        Response internalResp = null;

        // actually do the call based on the method
        String method = request.getMethod();
        switch (method) {
            case "GET":
                internalResp= requestBuilder.get();
                break;
            case "POST":
                internalResp = requestBuilder.post(Entity.entity(internalBodyReq, contentType));
                break;
            case "PUT":
                internalResp = requestBuilder.put(Entity.entity(internalBodyReq, contentType));
                break;
            case "DELETE":
                internalResp = requestBuilder.delete();
                break;
            default:
                log.error("HTTP method not supported: " + method);
                break;
        }

        response.setStatus(internalResp.getStatus());
        response.setContentType(internalResp.getMediaType().toString());
        response.getOutputStream().print(internalResp.readEntity(String.class));
    }
}
