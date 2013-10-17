/*
 * $Id$
 *
 * Copyright (c) 2011-2013 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.simsilica.es;


/**
 *  Represents a single component change for some entity and is used
 *  internally to the framework for requery optimization and by
 *  ObservableEntityData implementions to provide change notification.
 *  In general, applications should not need to use this as it's a framework
 *  level feature, ie: useful for framework extensions more than application
 *  code.
 *
 *  @author    Paul Speed
 */
public class EntityChange {

    private EntityId entityId;
    private EntityComponent component;
    private Class type;
        
    public EntityChange( EntityId entityId, Class type, EntityComponent component ) {
        this.entityId = entityId;
        this.type = component == null ? type : component.getClass();
        this.component = component;
    }
        
    public EntityChange( EntityId entityId, EntityComponent component ) {
        this(entityId, null, component);
    }
        
    public EntityChange( EntityId entityId, Class type ) {
        this( entityId, type, null );
    }
        
    public EntityId getEntityId() {
        return entityId;
    }
 
    public Class getComponentType() {
        return type;
    }
        
    public EntityComponent getComponent() {
        return component;
    }
        
    @Override
    public String toString() {
        return "EntityChange[" + entityId + ", " + component + ", " + type + "]";
    }     
}

