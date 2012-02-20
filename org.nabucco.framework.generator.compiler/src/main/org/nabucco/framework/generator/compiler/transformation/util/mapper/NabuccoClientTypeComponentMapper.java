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
package org.nabucco.framework.generator.compiler.transformation.util.mapper;

import org.nabucco.framework.generator.parser.model.client.NabuccoClientType;

/**
 * NabuccoClientTypeComponentMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoClientTypeComponentMapper {

    /**
     * Maps a client type to the appropriate client project name.
     * 
     * @param clientType
     *            the client type
     * @return the project name as string
     */
    public static String mapClientToProjectString(NabuccoClientType clientType) {

        switch (clientType) {

        case RCP:
            return "ui.rcp";

        case WEB:
            return "ui.web";
        default:
            throw new IllegalArgumentException("Client '" + clientType + "' is not supported.");
        }

    }
}
