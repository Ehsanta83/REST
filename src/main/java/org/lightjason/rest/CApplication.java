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

package org.lightjason.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.lightjason.agentspeak.agent.IAgent;
import org.lightjason.rest.provider.CAgentProvider;
import org.lightjason.rest.provider.IProvider;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Stream;


/**
 * application for instance the agent-component
 */
public final class CApplication extends ResourceConfig
{
    /**
     * agent provider
     */
    private final IProvider<IAgent<?>> m_agentsbyname = new CAgentProvider();
    /**
     * agent group provider
     */
    private final IProvider<IAgent<?>> m_agentsbygroup = m_agentsbyname.dependprovider()
                                                            .findFirst()
                                                            .orElseThrow( () -> new RuntimeException(
                                                                CCommon.languagestring( this, "nogroupprovider" )
                                                            ) );

    /**
     * ctor
     */
    public CApplication()
    {
        this.register( m_agentsbyname );
        this.register( m_agentsbygroup );
        this.packages(
            true,
            MessageFormat.format( "{0}.{1}", CCommon.PACKAGEROOT, "container" ),
            "com.fasterxml.jackson.jaxrs.json"
        );
    }


    /**
     * register an agent with a name and optional groups
     *
     * @param p_id agent name / id (case-insensitive)
     * @param p_agent agent object
     * @param p_group group names
     * @return self reference
     */
    public final CApplication register( final String p_id, final IAgent<?> p_agent, final Stream<String> p_group )
    {
        m_agentsbyname.register( p_id, p_agent );
        p_group.forEach( i -> m_agentsbygroup.register( i, p_agent ) );
        return this;
    }


    /**
     * register an agent with a name and optional groups
     *
     * @param p_id agent name / id (case-insensitive)
     * @param p_agent agent object
     * @param p_group group names
     * @return self reference
     */
    public final CApplication register( final String p_id, final IAgent<?> p_agent, final String... p_group )
    {
        return this.register(
            p_id,
            p_agent,
            ( p_group != null ) && ( p_group.length > 0 )
            ? Arrays.stream( p_group )
            : Stream.of()
        );
    }


    /**
     * unregister agent by the name
     *
     * @param p_id agent name / id (case-insensitive)
     * @return self refrence
     */
    public final CApplication unregisterbyname( final Stream<String> p_id )
    {
        p_id.forEach( i -> m_agentsbygroup.unregister( m_agentsbyname.unregister( i ) ) );
        return this;
    }


    /**
     * unregister agent by the name
     *
     * @param p_id agent name / id (case-insensitive)
     * @return self refrence
     */
    public final CApplication unregisterbyname( final String... p_id )
    {
        return this.unregisterbyname( Arrays.stream( p_id ) );
    }


    /**
     * unregister agent by the objct
     *
     * @param p_agent agent object
     * @return self refrence
     */
    public final CApplication unregisterbyobject( final Stream<IAgent<?>> p_agent )
    {
        m_agentsbygroup.unregister( m_agentsbyname.unregister( p_agent ) );
        return this;
    }

    /**
     * unregister agent by the objct
     *
     * @param p_agent agent object
     * @return self refrence
     */
    public final CApplication unregisterbyobject( final IAgent<?>... p_agent )
    {
        return this.unregisterbyobject( Arrays.stream( p_agent ) );
    }


    /**
     * unregister all agents from a group
     *
     * @param p_group group name (case-insensitive)
     * @return self refrence
     */
    public final CApplication unregisterbygroup( final Stream<String> p_group )
    {
        p_group.forEach( i -> m_agentsbyname.unregister( m_agentsbygroup.unregister( i ) ) );
        return this;
    }


    /**
     * unregister all agents from a group
     *
     * @param p_group group name (case-insensitive)
     * @return self refrence
     */
    public final CApplication unregisterbygroup( final String... p_group )
    {
        return this.unregisterbygroup( Arrays.stream( p_group ) );
    }

}
