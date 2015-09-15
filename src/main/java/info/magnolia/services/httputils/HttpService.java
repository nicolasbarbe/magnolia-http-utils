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
package info.magnolia.services.httputils;


import info.magnolia.services.httputils.definitions.ConnectionDefinition;
import info.magnolia.services.httputils.definitions.ServiceDefinition;
import info.magnolia.services.httputils.definitions.ServiceDefinitionRegistry;

import java.io.IOException;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.ObjectMapper;

public class HttpService {


    private WebTarget target;


    @Inject
    public HttpService(ServiceDefinitionRegistry registry, Client client, String service) {
        ServiceDefinition definition = registry.query().named(service).findSingle().get();
        String cs = resolveConnectionString(definition);
        // todo put this logic elsewhere
        String resource = definition.getResource();

        this.target = client.target(cs).path("api").path(definition.getApiVersion()).path(resource);
    }


    public Object GET() {
        return this.GET("");
    }

    // todo remove parameter, it must be defined in the registry instead
    public Object GET(String id) {

        Response response;
        if(StringUtils.isNotBlank(id)) {
            response =  this.target.path(id).request(MediaType.APPLICATION_JSON).get();
        } else {
            response =  this.target.request(MediaType.APPLICATION_JSON).get();
        }


        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }

        String entity = response.readEntity(String.class);

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
//        CollectionType types = mapper.getTypeFactory().constructCollectionType(List.class, Map.class);

        Object object;
        try {
            object = mapper.readValue(entity, Object.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read response.", e);
        }
        return object;

//        TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
//
//        Map<String, Object> map = new HashMap<>();
//        try {
//            map = mapper.readValue(entity, typeRef);
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read response.", e);
//        }
//        return map;
    }


    private String resolveHost(ConnectionDefinition definition){
        return resolve(definition.getType(), () -> definition.getHost());
    }

    private String resolvePort(ConnectionDefinition definition){
        return resolve(definition.getType(), () -> definition.getPort());
    }

    private String resolveConnectionString(ServiceDefinition definition){
        return "http://"+this.resolveHost(definition.getConnection()) + ":" + this.resolvePort(definition.getConnection());
    }

    private String resolve(String type, Supplier<String> supp) {
        switch(type) {
        case ConnectionDefinition.TYPE_ENV:
            return System.getenv(supp.get());
        case ConnectionDefinition.TYPE_VALUE:
            return supp.get();
        case ConnectionDefinition.TYPE_PROP:
            return System.getProperty(supp.get());
        default:
            throw new RuntimeException("Invalid value for property.");
        }
    }

    public static class HttpServiceFactory {
        private ServiceDefinitionRegistry registry;
        private Client client;

        @Inject
        public HttpServiceFactory(ServiceDefinitionRegistry registry, Client client) {
            this.registry = registry;
            this.client = client;
        }

        public HttpService newInstance(String service) {
            return new HttpService(registry, client, service);
        }
    }

}
