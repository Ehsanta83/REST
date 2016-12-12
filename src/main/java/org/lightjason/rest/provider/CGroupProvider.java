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
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    /**
     * bimap with name and agent object
     */
    private final BiMap<String, IAgent<?>> m_agentsbyname;

    /**
     * ctor
     *
     * @param p_namereference bimap with name and agent object
     */
    public CGroupProvider( final BiMap<String, IAgent<?>> p_namereference )
    {
        m_agentsbyname = p_namereference;
    }

    // --- agent register calls --------------------------------------------------------------------------------------------------------------------------------

    @Override
    public final IProvider register( final String p_id, final IAgent<?> p_agent )
    {
        m_groups.put( m_formater.apply( p_id ), p_agent );
        return this;
    }

    @Override
    public final Stream<IAgent<?>> unregister( final String p_id )
    {
        return m_groups.asMap().remove( m_formater.apply( p_id ) ).stream();
    }

    @Override
    public final Stream<? extends IAgent<?>> unregister( final IAgent<?>... p_agent )
    {
        return this.unregister( Arrays.stream( p_agent ) );
    }

    @Override
    public final Stream<? extends IAgent<?>> unregister( final Stream<? extends IAgent<?>> p_agent )
    {
        return p_agent.map( i -> {
            m_groups.asMap().values().parallelStream().forEach( j -> j.remove( i ) );
            return i;
        } ).filter( i -> !m_groups.containsValue( i ) );
    }

    @Override
    public final Stream<IProvider> dependprovider()
    {
        return Stream.of();
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
        return m_groups.keySet();
    }

    /**
     * returns all agents within a group
     *
     * @param p_group agent-group identifier
     * @return http response
     */
    @GET
    @Path( "/{group}/list" )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object list( @PathParam( "group" ) final String p_group )
    {
        final Collection<IAgent<?>> l_data = this.group( p_group );
        return l_data.isEmpty()
               ? Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentgroupnotfound", p_group ) ).build()
               : l_data.parallelStream()
                       .flatMap( i -> i.inspect( new CAgentInspector( m_agentsbyname.inverse().get( i ) ) ) )
                       .map( CAgentInspector::get )
                       .collect( Collectors.toList() );
    }

    /**
     * executes the cycle for all agents
     *
     * @param p_group agent-group identifier
     * @return response
     */
    @GET
    @Path( "/{group}/cycle" )
    public final Response cycle( @PathParam( "group" ) final String p_group )
    {
        final Set<String> l_result = CExecution.cycle( this.group( p_group ).stream() ).map( Throwable::getMessage ).collect( Collectors.toSet() );
        return l_result.isEmpty()
               ? Response.status( Response.Status.OK ).build()
               : Response.status( Response.Status.CONFLICT ).entity( MessageFormat.format( "{0}", l_result ) ).build();
    }

    /**
     * rest-api call to run wake-up (http get)
     *
     * @param p_group group name
     * @return http response
     */
    @GET
    @Path( "/{group}/wakeup" )
    public final Response wakeup( @PathParam( "group" ) final String p_group )
    {
        return this.wakeup( p_group, "" );
    }

    /**
     * rest-api call to run wake-up (http post)
     *
     * @param p_group agent-group identifier
     * @param p_data data
     * @return http response
     */
    @POST
    @Path( "/{group}/wakeup" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response wakeup( @PathParam( "group" ) final String p_group, final String p_data )
    {
        final Collection<IAgent<?>> l_data = this.group( p_group );
        if ( l_data.isEmpty() )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentgroupnotfound", p_group ) ).build();

        CExecution.wakeup( l_data.stream(), p_data );
        return Response.status( Response.Status.OK ).build();
    }

    /**
     * api call to set sleeping state (http get)
     *
     * @param p_group agent-group identifier
     * @param p_time sleeping time
     * @return http response
     */
    @GET
    @Path( "/{group}/sleep" )
    public final Response sleep( @PathParam( "group" ) final String p_group, @QueryParam( "time" ) final long p_time )
    {
        return this.sleep( p_group, p_time, "" );
    }

    /**
     * rest-api call to set sleeping state (http post)
     *
     * @param p_group agent-group identifier
     * @param p_time sleeping time
     * @param p_data wake-up data
     * @return http response
     */
    @POST
    @Path( "/{group}/sleep" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response sleep( @PathParam( "group" ) final String p_group, @QueryParam( "time" ) final long p_time, final String p_data )
    {
        final Collection<IAgent<?>> l_data = this.group( p_group );
        if ( l_data.isEmpty() )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentgroupnotfound", p_group ) ).build();

        CExecution.sleep( l_data.stream(), p_time, p_data );
        return Response.status( Response.Status.OK ).build();
    }


    /**
     * rest-api call to add a new belief
     *
     * @param p_group agent-group identifier
     * @param p_action action
     * @param p_literal literal
     * @return http response
     */
    @POST
    @Path( "/{group}/belief/{action}" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response belief( @PathParam( "group" ) final String p_group, @PathParam( "action" ) final String p_action, final String p_literal )
    {
        final Collection<IAgent<?>> l_agent = this.group( p_group );
        if ( l_agent.isEmpty() )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentgroupnotfound", p_group ) ).build();

        return CExecution.belief( l_agent.stream(), p_action, p_literal )
                         .map( Throwable::getMessage )
                         .map( i -> Response.status( Response.Status.CONFLICT ).entity( i ).build() )
                         .findAny()
                         .orElseGet( () -> Response.status( Response.Status.OK ).build() );
    }

    /**
     * rest-api call to trigger a plan immediately
     *
     * @param p_group agent-group identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @return http response
     */
    @POST
    @Path( "/{group}/trigger/{action}/{trigger}/immediately" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response goalimmediately( @PathParam( "group" ) final String p_group, @PathParam( "action" ) final String p_action,
                                           @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        final Collection<IAgent<?>> l_agent = this.group( p_group );
        if ( l_agent.isEmpty() )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentgroupnotfound", p_group ) ).build();

        return CExecution.goaltrigger(
            l_agent.stream(),
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
     * @param p_group agent-group identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @return http response
     */
    @POST
    @Path( "/{group}/trigger/{action}/{trigger}" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Response goal( @PathParam( "group" ) final String p_group, @PathParam( "action" ) final String p_action,
                                @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        final Collection<IAgent<?>> l_agent = this.group( p_group );
        if ( l_agent.isEmpty() )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentgroupnotfound", p_group ) ).build();

        return CExecution.goaltrigger(
            l_agent.stream(),
            p_action,
            p_trigger,
            p_literal,
            false
        )
                         .map( Throwable::getMessage )
                         .map( i -> Response.status( Response.Status.CONFLICT ).entity( i ).build() )
                         .findAny()
                         .orElseGet( () -> Response.status( Response.Status.OK ).build() );
    }

    /**
     * returns a group of agents by name
     *
     * @param p_group group name
     * @return agent collection
     */
    private Collection<IAgent<?>> group( final String p_group )
    {
        final Collection<IAgent<?>> l_data = m_groups.get( m_formater.apply( p_group ) );
        return l_data == null
               ? Collections.emptySet()
               : l_data;
    }
}
