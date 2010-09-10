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

import java.util.Map;
import java.io.Serializable;
import org.nabucco.framework.plugin.base.logging.Loggable;
import org.nabucco.framework.plugin.base.model.ViewModel;

/**
 * CommonViewModelTemplate
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class CommonViewModelTemplate extends ViewModel implements Loggable {

    public CommonViewModelTemplate() {
        // call create for each declared datatype
    }

    // create method for each declared datatype

    public String getID() {
        return "fqn";
    }

    /**
     * Getter for internationalized labels.
     * 
     * @return map of all view model properties for internationalization.
     */
    public Map<String, Serializable> getValues() {
        Map<String, Serializable> result = super.getValues();
        return result;
    }
}
