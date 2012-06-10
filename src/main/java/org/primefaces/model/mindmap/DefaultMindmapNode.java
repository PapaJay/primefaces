/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.model.mindmap;

import java.util.ArrayList;
import java.util.List;

public final class DefaultMindmapNode implements MindmapNode {
    
    private MindmapNode parent;
    
    private List<MindmapNode> children;
    
    private Object data;
    
    private String fill;
    
    public DefaultMindmapNode(Object data, MindmapNode parent, String fill) {
        this.data = data;
        this.children = new ArrayList<MindmapNode>();
        this.setParent(parent);
        this.fill = fill;
    }
    
    public DefaultMindmapNode(Object data, MindmapNode parent) {
        this.data = data;
        this.children = new ArrayList<MindmapNode>();
        this.setParent(parent);
    }

    public List<MindmapNode> getChildren() {
        return this.children;
    }

    public MindmapNode getParent() {
        return this.parent;
    }

    public Object getData() {
        return this.data;
    }
    
    public void setParent(MindmapNode parent) {
        if(this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        
        this.parent = parent;
        
        if(this.parent != null) {
            this.parent.getChildren().add(this);
        }
    }

    public String getFill() {
        return this.fill;
    }
}