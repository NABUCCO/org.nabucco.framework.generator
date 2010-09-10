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

import org.nabucco.framework.base.facade.datatype.DatatypeState;
import org.nabucco.framework.base.facade.datatype.Name;

/**
 * CommonViewModelMethodTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class CommonViewModelMethodTemplate {

    public void setField(String fieldValue) {
        if (datatype != null || datatype.getField() == null) {
            FieldType field = new FieldType();
            datatype.setField(field);
        }

        String oldVal = datatype.getField().getValue();
        authorizationUser.getUsername().setValue(aUserName);
        updateProperty(PROPERTY_USER_NAME, oldVal, aUserName);
        if (!oldVal.equals(aUserName)
                && authorizationUser.getDatatypeState().equals(DatatypeState.PERSISTENT)) {
            authorizationUser.setDatatypeState(DatatypeState.MODIFIED);
        }
    }

    public String getField() {
        if (datatype == null
                || datatype.getField() == null || datatype.getField().getValue() == null) {
            return "";
        }
        return datatype.getField().getValue();
    }

    public void setFieldDatatype(String fieldValue) {
        if (datatype == null || datatype.getDatatype() == null) {
            throw new IllegalStateException("ViewModel is inconsistent.");
        }
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
    
    public void setDatatypeSet(Set<Datatype> set) {
        if (set == null) {
            set = new HashSet<Datatype>();
        }
        this.field = set;

        StringBuilder result = new StringBuilder();
        Iterator<Datatype> iterator = set.iterator();
        
        while (iterator.hasNext()) {
            Datatype datatype = iterator.next();
            
            if (datatype == null || datatype.getBasetype() == null) {
                result.append("n/a");
            } else {
                result.append(datatype.getBasetype().getValue());
            }
            
            if (iterator.hasNext()) {
                result.append(", ");
            }
        }
        this.setDatatypeField(result.toString());
    }

    public String getFieldDatatype() {
        if ((((datatype == null) || (datatype.getDatatype() == null))
                || (datatype.getDatatype().getField() == null) || (datatype.getDatatype()
                .getField().getValue()) == null)) {
            return "";
        }
        return datatype.getDatatype().getField().getValue();
    }

    public void setDatatype(Datatype newValue) {
        Datatype oldValue = this.newValue;
        this.newValue = newValue;
    }

    public void setDatatypeDatatype(final Datatype newValue) {
        Datatype oldValue = this.localField.getProperty();
        getLocalField().setProperty(newValue);
        this.localField.setProperty(newValue);
        String oldValueString = "";
        if (oldValue != null && oldValue.getProperty() != null) {
            oldValueString = oldValue.getProperty().getValue();
        }
        String newValueString = "";
        if (newValue != null && newValue.getProperty() != null) {
            newValueString = newValue.getProperty().getValue();
        }
        this.updateProperty(PROPERTY, oldValueString, newValueString);

    }

    public void setFieldCombo(final String fieldValue) {
        String oldVal = this.datatype.getField().name();
        this.datatype.setField(fieldValue);
        this.updateProperty(PROPERTY, oldVal, fieldValue);
        if (((!oldVal.equals(fieldValue)) && datatype.getDatatypeState().equals(
                DatatypeState.PERSISTENT))) {
            datatype.setDatatypeState(DatatypeState.MODIFIED);
        }
    }

    public String getFieldCombo() {
        if ((datatype == null) || (datatype.getField() == null)) {
            return "";
        }
        return datatype.getField().name();
    }
}