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

/**
 * BrowserViewHelperTemplate.
 * 
 * @author Stefanie Feld, PRODYNA AG
 */
public class BrowserViewHelperTemplate{
    
    public void getValues() { 
        result.put(EditViewModel.PROPERTY, viewModel.getProperty());
    }

    protected void createChildrenList() {
        if (datatype.getPropertyList().size() > 0) {
            Datatype datatype[] = datatype.getDatatypeList().toArray(
                    new Datatype[0]);
            super.addBrowserElement(new DatatypeListBrowserElement(datatype));
        }
    }
    
    protected void createChildren() {
        if(datatype.getProperty() != null){
          super.addBrowserElement(new DatatypeBrowserElement(datatype.getProperty()));
        }
    }
}