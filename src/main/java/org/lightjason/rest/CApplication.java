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
import org.lightjason.rest.provider.CGroupProvider;
import org.lightjason.rest.provider.IProvider;

import java.text.MessageFormat;


/**
 * application for instance the agent-component
 */
public final class CApplication extends ResourceConfig
{
    /**
     * agent provider
     */
    private final IProvider m_agentsbyname = new CAgentProvider();
    /**
     * agent group provider
     */
    private final IProvider m_agentsbygroup = new CGroupProvider();

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
     * register an agent with a name
     *
     * @param p_id agent name / id (case-insensitive)
     * @param p_agent agent object
     * @return self reference
     */
    public final CApplication register( final String p_id, final IAgent<?> p_agent )
    {
        m_agentsbyname.register( p_id, p_agent );
        return this;
    }

    /**
     * unregister agent by the name
     *
     * @param p_id agent name / id (case insensitive )
     * @return self refrence
     */
    public final CApplication unregister( final String p_id )
    {
        m_agentsbyname.unregister( p_id );
        return this;
    }

    /**
     * unregister agent by the objct
     *
     * @param p_agent agent object
     * @return self refrence
     */
    public final CApplication unregister( final IAgent<?> p_agent )
    {
        m_agentsbyname.unregister( p_agent );
        return this;
    }

}
