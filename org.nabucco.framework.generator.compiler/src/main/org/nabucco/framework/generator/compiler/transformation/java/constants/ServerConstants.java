/*
 * Copyright 2012 PRODYNA AG
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/eclipse-1.0.php or
 * http://www.nabucco.org/License.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nabucco.framework.generator.compiler.transformation.java.constants;

/**
 * ServerConstants
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public interface ServerConstants extends JavaConstants {

    final String JNDI_NAME = "JNDI_NAME";

    final String SINGLETON_INSTANCE = "instance";

    final String COMPONENT_FIELD = "componentField";

    final String COMPONENT_INTERFACE_OPERATION = "componentInterfaceOperation";

    final String ADAPTER_INTERFACE_OPERATION = "adapterInterfaceOperation";

    final String CONNECTOR = "Connector";

    final String SERVICE_DELEGATE = SERVICE + DELEGATE;

    final String SERVICE_DELEGATE_FACTORY = SERVICE_DELEGATE + FACTORY;

    final String INJECTION_ID = "ID";

    final String SERVICE_REQUEST = SERVICE + "Request";

    final String SERVICE_RESPONSE = SERVICE + "Response";

    final String SERVICE_MESSAGE = SERVICE + MESSAGE;

    final String SERVICE_HANDLER = SERVICE + HANDLER;

    final String PERSISTENCE_SERVICE_HANDLER = "Persistence" + SERVICE + HANDLER;

    final String PERSISTENCE_SERVICE_HANDLER_SUPPORT = "Persistence" + SERVICE + HANDLER + SUPPORT;

    final String RESOURCE_SERVICE_HANDLER = "Resource" + SERVICE + HANDLER;

    final String RESOURCE_SERVICE_HANDLER_SUPPORT = "Resource" + SERVICE + HANDLER + SUPPORT;

    final String EMPTY_SERVICE_MESSAGE = "Empty" + SERVICE_MESSAGE;

    final String REMOTE = "Remote";

    final String LOCAL = "Local";

    final String PROXY = "Proxy";

    final String RS_MSG_SETTER = "setResponseMessage";

    final String RQ_MSG_SETTER = "setRequestMessage";

    final String ENTITY_MANAGER = "EntityManager";

    final String SESSION_CONTEXT = "SessionContext";

    final String PERSISTENCE_MANAGER = "PersistenceManager";

    final String PERSISTENCE_MANAGER_FACTORY = "PersistenceManagerFactory";

    final String RESOURCE_MANAGER = "ResourceManager";

    final String RESOURCE_MANAGER_FACTORY = "ResourceManagerFactory";

    final String IMPORT_EMPTY_SERVICE_MESSAGE = "org.nabucco.framework.base.facade.message.EmptyServiceMessage";

    final String IMPORT_SERVICE_REQUEST = "org.nabucco.framework.base.facade.message.ServiceRequest";

    final String IMPORT_SERVICE_RESPONSE = "org.nabucco.framework.base.facade.message.ServiceResponse";

    final String IMPORT_SERVICE_EXCEPTION = "org.nabucco.framework.base.facade.exception.service.ServiceException";

    final String IMPORT_ENTITY_MANAGER = "javax.persistence.EntityManager";

    final String IMPORT_SESSION_CONTEXT = "javax.ejb.SessionContext";

    final String IMPORT_SERVICE_HANDLER_SUPPORT = "org.nabucco.framework.base.impl.service.ServiceHandlerSupport";

    final String IMPORT_PERSISTENCE_SERVICE_HANDLER = "org.nabucco.framework.base.impl.service.maintain.PersistenceServiceHandler";

    final String IMPORT_PERSISTENCE_SERVICE_HANDLER_SUPPORT = "org.nabucco.framework.base.impl.service.maintain.PersistenceServiceHandlerSupport";

    final String IMPORT_PERSISTENCE_MANAGER = "org.nabucco.framework.base.impl.service.maintain.PersistenceManager";

    final String IMPORT_PERSISTENCE_MANAGER_FACTORY = "org.nabucco.framework.base.impl.service.maintain.PersistenceManagerFactory";

    final String IMPORT_RESOURCE_SERVICE_HANDLER = "org.nabucco.framework.base.impl.service.resource.ResourceServiceHandler";

    final String IMPORT_RESOURCE_SERVICE_HANDLER_SUPPORT = "org.nabucco.framework.base.impl.service.resource.ResourceServiceHandlerSupport";

    final String IMPORT_RESOURCE_MANAGER = "org.nabucco.framework.base.impl.service.resource.ResourceManager";

    final String IMPORT_RESOURCE_MANAGER_FACTORY = "org.nabucco.framework.base.impl.service.resource.ResourceManagerFactory";

}
