/*
 * $Id$
 *
 * Copyright (c) 2013 jMonkeyEngine
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

package trap.net;

import com.simsilica.es.client.RemoteEntityData;
import com.jme3.network.Client;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.net.ObjectMessageDelegator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import trap.GameClient;
import trap.game.Direction;
import trap.game.Maze;
import trap.game.TimeProvider;
import trap.net.msg.MazeDataMessage;
import trap.net.msg.PlayerInfoMessage;


/**
 *
 *  @author    Paul Speed
 */
public class RemoteGameClient implements GameClient {

    static Logger log = LoggerFactory.getLogger(RemoteGameClient.class);

    private Client client;
    private String playerName;
    private EntityId player;
    private RemoteEntityData ed;
    private Maze maze;
 
    private long frameDelay = 200 * 1000000L; // 200 ms 
    private long renderTime;
    private long serverTimeOffset;
    
    private ObjectMessageDelegator delegator;
    
    public RemoteGameClient( String playerName, Client client, int entityChannel ) {
        this.client = client;
        this.playerName = playerName;
        this.ed = new RemoteEntityData(client, entityChannel);
    
        this.delegator = new ObjectMessageDelegator(this, true);
        client.addMessageListener(delegator, delegator.getMessageTypes());
        
        // Send the player info to the server
        client.send(new PlayerInfoMessage(playerName).setReliable(true));
    }

    public final long getGameTime() {
        return System.nanoTime() + serverTimeOffset;
    }
   
    public final long getRenderTime() {
        return renderTime; 
    }

    public void updateRenderTime() {
        renderTime = getGameTime() - frameDelay;
    }
    
    public TimeProvider getRenderTimeProvider() {
        return new TimeProvider() {
                public final long getTime() {
                    return getRenderTime();
                }
            };
    }

    public EntityId getPlayer() {
        return player;
    }

    public EntityData getEntityData() {
        return ed;
    }

    public Maze getMaze() {
        return maze;
    }

    public void move(Direction dir) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void playerInfo( PlayerInfoMessage msg ) {
        System.out.println( "playerInfo(" + msg + ")" );
        player = msg.getEntityId();
    }

    protected void mazeData( MazeDataMessage msg ) {
        System.out.println( "mazeData(" + msg + ")" );
        maze = new Maze(msg.getWidth(), msg.getHeight(), msg.getCells()); 
    }

    public void close() {
        
        // Close the remote entity data
        log.info("Closing entity connection");
        ed.close();
    
        // Close the network connection
        log.info("Closing client connection");
        if( client.isConnected() ) {
            client.close();
        }
    }
}


