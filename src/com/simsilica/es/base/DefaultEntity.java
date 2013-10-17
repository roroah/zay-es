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

package com.simsilica.es.base;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import java.util.*;

/**
 *  Default entity implementation that caches a subset
 *  of the components in an array.
 *
 *  @author    Paul Speed
 */
public class DefaultEntity implements Entity {

    private EntityData ed;    
    private EntityId id;
    private EntityComponent[] components;

    private Class[] types; // temporarily for validating component types

    public DefaultEntity( EntityData ed, EntityId id, 
                          EntityComponent[] components, Class[] types ) {
        this.ed = ed;
        this.id = id;
        this.components = components;
        this.types = types;
        
        validate();
    }
    
    protected void validate() {
        for( int i = 0; i < types.length; i++ ) {
            if( components[i] == null ) {
                continue;
            }
            if( components[i].getClass() != types[i] ) {
                throw new RuntimeException( "Validation error.  components[" + i + "]:" 
                                            + components[i] + " is not of type:" + types[i] );
            }
        }
    }

    @Override
    public EntityId getId() {
        return id;
    }
 
    @Override
    public EntityComponent[] getComponents() {
        return components;
    }
    
    @Override
    public boolean equals( Object o ) {
        if( o == this )
            return true;
        if( o == null )
            return false;
        if( o.getClass() != getClass() )
            return false;
       
        return id.equals(((DefaultEntity)o).id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public <T extends EntityComponent> T get( Class<T> type ) {
        for( EntityComponent c : components ) {
            if( c != null && c.getClass() == type ) {
                return type.cast(c);
            }
        }
        return null;
    }
     
    @Override
    public void set( EntityComponent c ) {
    
        for( int i = 0; i < components.length; i++ ) {        
            if( components[i].getClass().isInstance(c) ) {               
                ed.setComponent( getId(), c );
                components[i] = c;
                return;
            }             
        }
            
        // We can still pass through the value to the EntityData even
        // if we can't return it
        ed.setComponent( id, c );
    }
 
    @Override
    public boolean isComplete() {
        for( int i = 0; i < components.length; i++ ) {
            if( components[i] == null ) {
                return false;
            }
        }
        return true;            
    }
    
    @Override
    public String toString() {
        return "Entity[" + id + ", values=" + Arrays.asList(components) + "]";
    }
}
