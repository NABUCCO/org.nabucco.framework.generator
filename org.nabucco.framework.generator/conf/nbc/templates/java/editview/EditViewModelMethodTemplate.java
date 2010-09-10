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

import org.nabucco.framework.plugin.base.Activator;

/**
 * EditViewModelMethodTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class EditViewModelMethodTemplate {
    
    public void setField(final String fieldValue) {
        if (datatype.getField() == null) {
            FieldType field = new FieldType();
            field.setValue("");
            datatype.setField(field);
        }
        
        String oldVal = datatype.getField().getValue();
        authorizationUser.getUsername().setValue(aUserName);
        updateProperty(PROPERTY_USER_NAME, 
                        oldVal,
                        aUserName);
        if (!oldVal.equals(aUserName) 
            && authorizationUser.getDatatypeState().equals(DatatypeState.PERSISTENT)) {
            authorizationUser.setDatatypeState(DatatypeState.MODIFIED);
        }
    }
    public void setFieldDatatype(String fieldValue) {
        if (datatype == null) {
            throw new IllegalStateException("ViewModel is inconsistent.");
        } else {
            if (datatype.getDatatype().getField() == null) {
                Field field = new Field();
                field.setValue("");
                datatype.getDatatype().setField(field);
            }
            String oldVal = datatype.getDatatype().getField().getValue();
            datatype.getDatatype().getField().setValue(fieldValue);
            this.updateProperty(PROPERTY, oldVal, fieldValue);
            if (((!oldVal.equals(fieldValue)) && datatype.getDatatype().getDatatypeState().equals(
                    DatatypeState.PERSISTENT))) {
                datatype.getDatatype().setDatatypeState(DatatypeState.MODIFIED);
            }
        }
    }
    
    public String getField() {
        if (null == datatype
            || null == datatype.getField()
            || null == datatype.getField().getValue()) {
            return "";
        }
        return datatype.getField().getValue();
    }
 
}