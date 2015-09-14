# Magnolia HTTP Utils
A module to easily integrate [Magnolia](http://magnolia-cms.com/) with several external services based on the HTTP protocol.

This module is useful in the context of a REST-style architcture such as the ones found in microservices.

This module provides an internal registry which contains the definition of the services. 

This registry is used in Magnolia to:
- Declare the services in a configuration file (YAML).
- Discover the services automatically either using an environment variable, a java property or passing the value directly in the configuration file.
- Instanciate and inject an HTTP client into several components of Magnolia.

# Features
Beside the service registry, this module extends some Magnolia features to ease the interoperability between them and the services:
- A templating function to directly call the services from your template.
- A content connector to easily create content app declaratively.
- A command and an action to quickly map UI interactions to a external service.

# Installation
todo