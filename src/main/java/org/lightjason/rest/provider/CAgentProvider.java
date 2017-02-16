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


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.rest.CCommon;
import org.lightjason.rest.inspector.CAgentInspector;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * agent provider
 */
@Path( "/agent" )
public final class CAgentProvider implements IProvider<IAgent<?>>
{
    /**
     * map with agents
     **/
    private final BiMap<String, IAgent<?>> m_agents = Maps.synchronizedBiMap( HashBiMap.create() );


    // --- agent register calls --------------------------------------------------------------------------------------------------------------------------------

    @Override
    public final IProvider<IAgent<?>> register( final String p_id, final IAgent<?> p_agent )
    {
        m_agents.putIfAbsent( CCommon.urlformat( p_id ), p_agent );
        return this;
    }

    @Override
    public final Stream<? extends IAgent<?>> unregister( final String p_id )
    {
        return Stream.of( m_agents.remove( CCommon.urlformat( p_id ) ) );
    }

    @Override
    public final Stream<? extends IAgent<?>> unregister( final IAgent<?>... p_agent )
    {
        return this.unregister( Arrays.stream( p_agent ) );
    }

    @Override
    public final Stream<? extends IAgent<?>> unregister( final Stream<? extends IAgent<?>> p_agent )
    {
        return p_agent
            .map( i -> {
                m_agents.inverse().remove( i );
                return i;
            } );
    }

    @Override
    public final Stream<IProvider<IAgent<?>>> dependprovider()
    {
        return Stream.of( new CGroupProvider( m_agents ) );
    }


    // --- api calls -------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * returns a list of all registered agent names
     *
     * @return agent name list
     */
    @GET
    @Path( "/list" )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object view()
    {
        return m_agents.keySet();
    }

    /**
     * rest-api call to get current state of the agent
     *
     * @param p_id agent identifier
     * @return agent object or http error
     */
    @GET
    @Path( "/{id}/view" )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object view( @PathParam( "id" ) final String p_id )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        return l_agent == null
               ? Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build()
               : l_agent.inspect( new CAgentInspector( p_id ) ).findFirst().orElseThrow( RuntimeException::new ).get();
    }

    /**
     * executes the cycle for all agents
     * @return response
     */
    @GET
    @Path( "/cycle" )
    public final Response cycle()
    {
        final Set<String> l_result = CExecution.cycle( m_agents.values().stream() ).map( Throwable::getMessage ).collect( Collectors.toSet() );
        return l_result.isEmpty()
              ? Response.status( Response.Status.OK ).build()
              : Response.status( Response.Status.CONFLICT ).entity( MessageFormat.format( "{0}", l_result ) ).build();
    }


    /**
     * rest-api call to run a cycle
     *
     * @return response
     */
    @GET
    @Path( "/{id}/cycle" )
    public final Response cycle( @PathParam( "id" ) final String p_id )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        return CExecution.cycle( Stream.of( l_agent ) )
                  .map( Throwable::getMessage )
                  .map( i -> Response.status( Response.Status.CONFLICT ).entity( i ).build() )
                  .findAny()
                  .orElseGet( () -> Response.status( Response.Status.OK ).build() );
    }

    /**
     * api call to set sleeping state (http get)
     *
     * @param p_id agent identifier
     * @param p_time sleeping time
     * @return http response
     */
    @GET
    @Path( "/{id}/sleep" )
    public final Response sleep( @PathParam( "id" ) final String p_id, @QueryParam( "time" ) final long p_time )
    {
        return this.sleep( p_id, p_time, "" );
    }

    /**
     * rest-api call to set sleeping state (http post)
     *
     * @param p_id agent identifier
     * @param p_time sleeping time
     * @param p_data wake-up data
     * @return http response
     */
    @POST
    @Path( "/{id}/sleep" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response sleep( @PathParam( "id" ) final String p_id, @QueryParam( "time" ) final long p_time, final String p_data )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        CExecution.sleep( Stream.of( l_agent ), p_time, p_data );
        return Response.status( Response.Status.OK ).build();
    }

    /**
     * rest-api call to run wake-up (http get)
     *
     * @param p_id agent identifier
     * @return http response
     */
    @GET
    @Path( "/{id}/wakeup" )
    public final Response wakeup( @PathParam( "id" ) final String p_id )
    {
        return this.wakeup( p_id, "" );
    }

    /**
     * rest-api call to run wake-up (http post)
     *
     * @param p_id agent identifier
     * @param p_data data
     * @return http response
     */
    @POST
    @Path( "/{id}/wakeup" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response wakeup( @PathParam( "id" ) final String p_id, final String p_data )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        CExecution.wakeup( Stream.of( l_agent ), p_data );
        return Response.status( Response.Status.OK ).build();
    }

    /**
     * rest-api call to add a new belief
     *
     * @param p_id agent identifier
     * @param p_action action
     * @param p_literal literal
     * @return http response
     */
    @POST
    @Path( "/{id}/belief/{action}" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response belief( @PathParam( "id" ) final String p_id, @PathParam( "action" ) final String p_action, final String p_literal )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        return CExecution.belief( Stream.of( l_agent ), p_action, p_literal )
                         .map( Throwable::getMessage )
                         .map( i -> Response.status( Response.Status.CONFLICT ).entity( i ).build() )
                         .findAny()
                         .orElseGet( () -> Response.status( Response.Status.OK ).build() );
    }

    /**
     * rest-api call to trigger a plan immediately
     *
     * @param p_id agent identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @return http response
     */
    @POST
    @Path( "/{id}/trigger/{action}/{trigger}/immediately" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response goalimmediately( @PathParam( "id" ) final String p_id, @PathParam( "action" ) final String p_action,
                                           @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        return CExecution.goaltrigger(
            Stream.of( l_agent ),
            p_action,
            p_trigger,
            p_literal,
            true
        )
        .map( Throwable::getMessage )
        .map( i -> Response.status( Response.Status.CONFLICT ).entity( i ).build() )
        .findAny()
        .orElseGet( () -> Response.status( Response.Status.OK ).build() );
    }

    /**
     * rest-api call to trigger a plan
     *
     * @param p_id agent identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @return http response
     */
    @POST
    @Path( "/{id}/trigger/{action}/{trigger}" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response goal( @PathParam( "id" ) final String p_id, @PathParam( "action" ) final String p_action,
                                @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        final IAgent<?> l_agent = m_agents.get( p_id );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        return CExecution.goaltrigger(
            Stream.of( l_agent ),
            p_action,
            p_trigger,
            p_literal,
            true
        )
        .map( Throwable::getMessage )
        .map( i -> Response.status( Response.Status.CONFLICT ).entity( i ).build() )
        .findAny()
        .orElseGet( () -> Response.status( Response.Status.OK ).build() );
    }

}
