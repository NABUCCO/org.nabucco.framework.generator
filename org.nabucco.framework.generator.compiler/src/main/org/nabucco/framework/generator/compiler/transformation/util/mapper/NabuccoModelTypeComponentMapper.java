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

import org.nabucco.framework.generator.parser.model.NabuccoModelConstants;
import org.nabucco.framework.generator.parser.model.NabuccoModelType;

/**
 * NabuccoModelTypeComponentMapper
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class NabuccoModelTypeComponentMapper implements NabuccoModelConstants {

    /**
     * Maps a model type to the appropriate project name.
     * 
     * @param modelType
     *            the model type
     * 
     * @return the project name as string
     */
    public static String mapModelToProjectString(NabuccoModelType modelType) {

        switch (modelType) {

        case APPLICATION:
            return SERVER;

        case CONNECTOR:
            return SERVER;

        case BASETYPE:
            return DATATYPE;
            
        case COMPONENT:
            return COMPONENT;

        case ADAPTER:
            return ADAPTER;

        case DATATYPE:
            return DATATYPE;

        case ENUMERATION:
            return DATATYPE;

        case MESSAGE:
            return MESSAGE;

        case SERVICE:
            return SERVICE;

        case EXCEPTION:
            return EXCEPTION;
        }

        throw new IllegalArgumentException("ModelType '" + modelType + "' is not supported.");
    }

}
