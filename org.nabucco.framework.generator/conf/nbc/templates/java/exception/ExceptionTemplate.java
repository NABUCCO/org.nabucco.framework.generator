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

import java.util.HashMap;
import java.util.Map;

import org.nabucco.framework.base.facade.exception.ExceptionSupport;

/**
 * ExceptionTemplate
 * 
 * @author Nicolas Moser, PRODYNA AG
 */
public class ExceptionTemplate extends ExceptionSupport {

    private static final long serialVersionUID = 1L;

    private Map<String, String> parameterMap = new HashMap<String, String>();
    
    public ExceptionTemplate() {
        super();
    }

    public ExceptionTemplate(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ExceptionTemplate(String message) {
        super(message);
    }

    public ExceptionTemplate(Throwable throwable) {
        super(throwable);
    }
    
    public Map<String, String> getParameters() {
        return new HashMap<String, String>(this.parameterMap);
    }
}
