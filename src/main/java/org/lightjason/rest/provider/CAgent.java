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
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.CTrigger;
import org.lightjason.agentspeak.language.instantiable.plan.trigger.ITrigger;
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
import java.util.Locale;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * singleton webservice provider to control
 * an agent as XML and JSON request
 */
@Path( "/agent" )
public final class CAgent implements IProvider
{
    /**
     * function to format agent identifier
     */
    private final Function<String, String> m_formater = (i) -> i.trim().toLowerCase( Locale.ROOT );
    /**
     * map with agents
     **/
    private final BiMap<String, IAgent<?>> m_agents = Maps.synchronizedBiMap( HashBiMap.create() );


    // --- agent register calls --------------------------------------------------------------------------------------------------------------------------------


    @Override
    public final IProvider register( final String p_id, final IAgent<?> p_agent )
    {
        m_agents.put( m_formater.apply( p_id ), p_agent );
        return this;
    }

    @Override
    public final IProvider unregister( final String p_id )
    {
        m_agents.remove( m_formater.apply( p_id ) );
        return this;
    }

    @Override
    public final IProvider unregister( final IAgent<?> p_agent )
    {
        m_agents.inverse().remove( p_agent );
        return this;
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
        final IAgent<?> l_agent = m_agents.get( m_formater.apply( p_id ) );
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
        final IAgent<?> l_agent = m_agents.get( m_formater.apply( p_id ) );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        return CExecution.cycle( Stream.of( l_agent ) )
                  .map( Throwable::getMessage )
                  .filter( i -> !i.isEmpty() )
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
        final IAgent<?> l_agent = m_agents.get( m_formater.apply( p_id ) );
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
        final IAgent<?> l_agent = m_agents.get( m_formater.apply( p_id ) );
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
        // find agent
        final IAgent<?> l_agent = m_agents.get( m_formater.apply( p_id ) );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        // parse literal
        final ILiteral l_literal;
        try
        {
            l_literal = CLiteral.parse( p_literal );
        }
        catch ( final Exception l_exception )
        {
            return Response.status( Response.Status.BAD_REQUEST ).entity( CCommon.languagestring( this, "literalparse" ) ).build();
        }

        // execute belief action
        switch ( p_action.toLowerCase( Locale.ROOT ) )
        {
            case "delete" :
                l_agent.beliefbase().remove( l_literal );
                break;

            case "add" :
                l_agent.beliefbase().add( l_literal );
                break;

            default:
                return Response.status( Response.Status.BAD_REQUEST ).entity( CCommon.languagestring( this, "actionnotfound", p_action ) ).build();
        }

        return Response.status( Response.Status.OK ).build();
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
    public final Response goalimmediately( @PathParam( "id" ) final String p_id, @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        return this.executetrigger( p_id, p_trigger, p_literal, ( i, j ) -> i.trigger( j, true ) );
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
    public final Response goal( @PathParam( "id" ) final String p_id, @PathParam( "trigger" ) final String p_trigger, final String p_literal )
    {
        return this.executetrigger( p_id, p_trigger, p_literal, ( i, j ) -> i.trigger( j ) );
    }

    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * triggers the agent based on input data
     *
     * @param p_id agent identifier
     * @param p_trigger trigger type
     * @param p_literal literal data
     * @param p_execute consumer function
     * @return response
     */
    private Response executetrigger( final String p_id, final String p_trigger, final String p_literal, final BiConsumer<IAgent<?>, ITrigger> p_execute )
    {
        // find agent
        final IAgent<?> l_agent = m_agents.get( m_formater.apply( p_id ) );
        if ( l_agent == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "agentnotfound", p_id ) ).build();

        // parse literal
        final ILiteral l_literal;
        try
        {
            l_literal = CLiteral.parse( p_literal );
        }
        catch ( final Exception l_exception )
        {
            return Response.status( Response.Status.BAD_REQUEST ).entity( CCommon.languagestring( this, "literalparse" ) ).build();
        }

        // parse trigger
        final ITrigger l_trigger;
        switch ( p_trigger.toLowerCase( Locale.ROOT ) )
        {
            case "addgoal"      :
                l_trigger = CTrigger.from( ITrigger.EType.ADDGOAL, l_literal );
                break;

            case "deletegoal"   :
                l_trigger = CTrigger.from( ITrigger.EType.DELETEGOAL, l_literal );
                break;

            case "addbelief"    :
                l_trigger = CTrigger.from( ITrigger.EType.ADDBELIEF, l_literal );
                break;

            case "deletebelief" :
                l_trigger = CTrigger.from( ITrigger.EType.DELETEBELIEF, l_literal );
                break;

            default:
                return Response.status( Response.Status.BAD_REQUEST ).entity( CCommon.languagestring( this, "triggernotfound", p_trigger ) ).build();
        }

        // execute trigger
        p_execute.accept( l_agent, l_trigger );
        return Response.status( Response.Status.OK ).build();
    }

}
