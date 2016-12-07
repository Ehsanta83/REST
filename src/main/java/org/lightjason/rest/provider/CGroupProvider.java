/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason REST                                           #
 * # Copyright (c) 2015-16, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

package org.lightjason.rest.provider;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.lightjason.agentspeak.agent.IAgent;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Locale;
import java.util.function.Function;


/**
 * agent group provider
 */
@Path( "/agentgroup" )
public final class CGroupProvider implements IProvider
{
    /**
     * function to format agent identifier
     */
    private final Function<String, String> m_formater = ( i ) -> i.trim().toLowerCase( Locale.ROOT );
    /**
     * group map
     */
    private final Multimap<String, IAgent<?>> m_groups = Multimaps.synchronizedMultimap( HashMultimap.create() );

    // --- agent register calls --------------------------------------------------------------------------------------------------------------------------------

    @Override
    public final IProvider register( final String p_id, final IAgent<?> p_agent )
    {
        m_groups.put( m_formater.apply( p_id ), p_agent );
        return this;
    }

    @Override
    public final IProvider unregister( final String p_id )
    {
        m_groups.asMap().remove( m_formater.apply( p_id ) );
        return this;
    }

    @Override
    public final IProvider unregister( final IAgent<?> p_agent )
    {
        m_groups.asMap().values().parallelStream().forEach( i -> i.remove( p_agent ) );
        return this;
    }

    // --- api calls -------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * returns a list of all agents in all groups
     *
     * @return http response
     */
    @GET
    @Path( "/list" )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object list()
    {
        return null;
    }

    /**
     * returns all agents within a group
     *
     * @return http response
     */
    @GET
    @Path( "/listgroup/{group}" )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object list( @PathParam( "group" ) final String p_group )
    {
        return null;
    }

}
