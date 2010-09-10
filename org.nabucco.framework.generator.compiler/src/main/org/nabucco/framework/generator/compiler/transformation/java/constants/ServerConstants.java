/*
* Copyright 2010 PRODYNA AG
*
* Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.opensource.org/licenses/eclipse-1.0.php or
* http://www.nabucco-source.org/nabucco-license.html
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

    final String SINGLETON_GETTER = "getInstance";

    final String COMPONENT_FIELD = "componentField";

    final String COMPONENT_INTERFACE_OPERATION = "componentInterfaceOperation";

    final String COMPONENT_IMPLEMENTATION_OPERATION = "componentImplementationOperation";

    final String SERVICE_DELEGATE = SERVICE + DELEGATE;

    final String SERVICE_DELEGATE_FACTORY = SERVICE_DELEGATE + FACTORY;
    
    final String INJECTION_ID = "ID";

    final String SERVICE_REQUEST = SERVICE + "Request";

    final String SERVICE_MESSAGE = SERVICE + MESSAGE;

    final String SERVICE_HANDLER = SERVICE + HANDLER;
    
    final String EMPTY_SERVICE_MSG = "Empty" + SERVICE_MESSAGE;
}
