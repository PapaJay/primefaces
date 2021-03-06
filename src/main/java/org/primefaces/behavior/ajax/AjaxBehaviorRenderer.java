/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.behavior.ajax;

import java.util.Collection;
import java.util.List;
import javax.faces.component.ActionSource;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import javax.faces.render.ClientBehaviorRenderer;
import org.primefaces.component.api.ClientBehaviorRenderingMode;

import org.primefaces.context.RequestContext;
import org.primefaces.util.AjaxRequestBuilder;

public class AjaxBehaviorRenderer extends ClientBehaviorRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component, ClientBehavior behavior) {
        AjaxBehavior ajaxBehavior = (AjaxBehavior) behavior;

        if(!ajaxBehavior.isDisabled()) {
            AjaxBehaviorEvent event = new AjaxBehaviorEvent(component, behavior);

            PhaseId phaseId = isImmediate(component, ajaxBehavior) ? PhaseId.APPLY_REQUEST_VALUES : PhaseId.INVOKE_APPLICATION;

            event.setPhaseId(phaseId);

            component.queueEvent(event);
        }
    }

    @Override
    public String getScript(ClientBehaviorContext behaviorContext, ClientBehavior behavior) {
        AjaxBehavior ajaxBehavior = (AjaxBehavior) behavior;
        if(ajaxBehavior.isDisabled()) {
            return null;
        }
        
        UIComponent component = behaviorContext.getComponent();
        Collection<ClientBehaviorContext.Parameter> behaviorParameters = behaviorContext.getParameters();
        ClientBehaviorRenderingMode renderingMode = (behaviorParameters == null || behaviorParameters.isEmpty()) ? ClientBehaviorRenderingMode.OBSTRUSIVE : 
                                    (ClientBehaviorRenderingMode) ((List<ClientBehaviorContext.Parameter>) behaviorParameters).get(0).getValue();
        String source = behaviorContext.getSourceId();
        String process = ajaxBehavior.getProcess();
        if(process == null) {
            process = "@this";
        }
      
        AjaxRequestBuilder builder = RequestContext.getCurrentInstance().getAjaxRequestBuilder();

        String request = builder.init()
        				.source(source)
                        .event(behaviorContext.getEventName())
                        .process(component, process)
                        .update(component, ajaxBehavior.getUpdate())
                        .async(ajaxBehavior.isAsync())
                        .global(ajaxBehavior.isGlobal())
                        .delay(ajaxBehavior.getDelay())
                        .partialSubmit(ajaxBehavior.isPartialSubmit(), ajaxBehavior.isPartialSubmitSet())
                        .resetValues(ajaxBehavior.isResetValues(), ajaxBehavior.isResetValuesSet())
                        .ignoreAutoUpdate(ajaxBehavior.isIgnoreAutoUpdate())
                        .onstart(ajaxBehavior.getOnstart())
                        .onerror(ajaxBehavior.getOnerror())
                        .onsuccess(ajaxBehavior.getOnsuccess())
                        .oncomplete(ajaxBehavior.getOncomplete())
                        .params(component)
                        .buildBehavior(renderingMode);

        return request;
    }

    private boolean isImmediate(UIComponent component, AjaxBehavior ajaxBehavior) {
        boolean immediate = false;

        if(ajaxBehavior.isImmediateSet()) {
            immediate = ajaxBehavior.isImmediate();
        } else if(component instanceof EditableValueHolder) {
            immediate = ((EditableValueHolder)component).isImmediate();
        } else if(component instanceof ActionSource) {
            immediate = ((ActionSource)component).isImmediate();
        }

        return immediate;
    }
}
