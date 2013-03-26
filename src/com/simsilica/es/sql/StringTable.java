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

package com.simsilica.es.sql;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Logger;



/**
 *  A straight-up string table... for uniquely tracking
 *  strings by ID.
 *
 *  @version   $Revision$
 *  @author    Paul Speed
 */
public class StringTable 
{
    static Logger log = Logger.getLogger(StringTable.class);
 
    private String tableName = "STRINGS";   
    private boolean cached = true;
    private String insertSql;
    private String idForString;
    private String stringForId;
    
    protected StringTable() 
    {
        idForString = "select id from STRINGS where val=?";
        stringForId = "select val from STRINGS where id=?";
        insertSql = "insert into " + tableName + " (id,val) values (default,?)";       
    }

    public static StringTable create( SqlSession session ) throws SQLException
    {
        StringTable result = new StringTable();   
        result.initialize(session);
        return result;   
    }

    /**
     *  Makes sure that the table is created or is of the format we
     *  expect.
     */
    protected void initialize( SqlSession session ) throws SQLException
    {
        // See if the table exists
        DatabaseMetaData md = session.getConnection().getMetaData();
 
        log.info( "Checking for table:" + tableName );           
        ResultSet rs = md.getColumns( null, "PUBLIC", tableName, null );
        Map<String,Integer> dbFields = new HashMap<String,Integer>();
        try
            {
            while( rs.next() )
                {
                dbFields.put( rs.getString( "COLUMN_NAME" ), rs.getInt( "DATA_TYPE" ) );
                }
            }
        finally
            {
            rs.close();
            }

        log.info( "dbFields for " + tableName + " :" + dbFields );
        if( !dbFields.isEmpty() )
            {
            checkStructure( dbFields );
            return;
            }
            
        // Really should have a separate class for this
        StringBuilder sb = new StringBuilder( "CREATE" );
        if( cached )
            sb.append( " CACHED" );
        sb.append( " TABLE" );
 
        sb.append( " " + tableName + "\n" );
        sb.append( "(\n" );
        sb.append( "  id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY" );
        sb.append( ",\n  val VARCHAR(50)" );
        sb.append( ",\n  CONSTRAINT val_key UNIQUE (val)" );
        sb.append( "\n)" );
        
        if( log.isTraceEnabled() )
            log.trace( "Create statement:\n" + sb );
        
        Statement st = session.getConnection().createStatement();    
        int i = st.executeUpdate(sb.toString());
        st.close();    
        
        if( log.isTraceEnabled() )
            log.trace( "Result:" + i );        
    }

    protected void checkStructure( Map<String,Integer> dbFields ) throws SQLException
    {
    }

    protected int lookupString( SqlSession session, String s ) throws SQLException 
    {
        if( log.isTraceEnabled() )
            log.trace( "executing query:" + idForString );    
        PreparedStatement st = session.prepareStatement( idForString );
        st.setObject( 1, s );
        ResultSet rs = st.executeQuery();
        try
            {
            if( rs.next() )
                {
                Integer i = (Integer)rs.getObject(1);
                return i;
                }
            return -1;
            }
        finally
            {
            rs.close();
            }            
    }
    
    protected int addString( SqlSession session, String s ) throws SQLException
    {
        if( log.isTraceEnabled() )
            log.trace( "Executing:" + insertSql );    
        PreparedStatement st = session.prepareStatement( insertSql, Statement.RETURN_GENERATED_KEYS );
        st.setObject( 1, s );
        int count = st.executeUpdate();
        if( log.isTraceEnabled() )
            log.trace( "count:" + count );               
        ResultSet keys = st.getGeneratedKeys();
        try
            {
            if( keys.next() )
                {
                Integer i = (Integer)keys.getObject(1);
                if( log.isTraceEnabled() )
                    log.trace( "Generated key: " + i);        
                return i;
                }
            throw new RuntimeException( "Failed to add string:" + s );
            }
        finally
            {
            keys.close();
            }            
    }
    
    public int getStringId( SqlSession session, String s, boolean add ) throws SQLException
    {
        if( log.isTraceEnabled() )
            log.trace( "db.getStringId(" + s + ", " + add + ")" );    
        // See if the string exists
        int result = lookupString( session, s );
        if( log.isTraceEnabled() )
            log.trace( "Result from lookup:" + result );        
        if( result == -1 && add )
            {
            result = addString(session, s);
            if( log.isTraceEnabled() )
                log.trace( "result from add:" + result );            
            }
    
        return result;
    }
                             
    public String getString( SqlSession session, int id ) throws SQLException
    {
        PreparedStatement st = session.prepareStatement( stringForId );
        st.setObject( 1, id );
        ResultSet rs = st.executeQuery();
        try
            {
            if( rs.next() )
                {
                String s = (String)rs.getObject(1);
                return s;
                }
            return null;
            }
        finally
            {
            rs.close();
            }            
    }    
    
}

