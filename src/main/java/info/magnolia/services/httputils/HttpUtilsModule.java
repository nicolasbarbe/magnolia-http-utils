package info.magnolia.services.httputils;

import info.magnolia.config.source.ConfigurationSourceFactory;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import info.magnolia.objectfactory.ComponentFactory;
import info.magnolia.services.httputils.definitions.ServiceDefinition;
import info.magnolia.services.httputils.definitions.ServiceDefinitionRegistry;

import java.util.Map;


import javax.inject.Inject;
import javax.ws.rs.client.Client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;


public class HttpUtilsModule implements ModuleLifecycle {

    private Map<String,ServiceDefinition> services;

    private Client client;
    private ConfigurationSourceFactory cfgSourceFactory;
    private  ServiceDefinitionRegistry registry;

    @Inject
    public HttpUtilsModule(Client client, ConfigurationSourceFactory cfgSourceFactory, ServiceDefinitionRegistry registry) {
        this.client = client;
        this.cfgSourceFactory = cfgSourceFactory;
        this.registry = registry;
    }

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        if(((ResteasyClient) this.client).isClosed()) {
            throw new RuntimeException("Client has been closed and cannot be recreated");
        }

        cfgSourceFactory.jcr().bindWithDefaults(registry);
        cfgSourceFactory.yaml().bindWithDefaults(registry);
    }

    @Override
    public void stop(ModuleLifecycleContext moduleLifecycleContext) {
        if(moduleLifecycleContext.getPhase() == ModuleLifecycleContext.PHASE_SYSTEM_SHUTDOWN) {
            this.client.close();
        }
    }

    public ServiceDefinition getService(String serviceName) {
        return services.get(serviceName);
    }

    public Map<String, ServiceDefinition> getServices() {
        return services;
    }

    public void setServices(Map<String, ServiceDefinition> services) {
        this.services = services;
    }

    static public class ClientProvider implements ComponentFactory<Client> {

        private final static int POOL_SIZE = 10;

        @Override
        public Client newInstance() {
            return new ResteasyClientBuilder().connectionPoolSize(POOL_SIZE).build();
        }
    }

}
