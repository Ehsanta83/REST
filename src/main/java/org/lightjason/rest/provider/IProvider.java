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

import org.lightjason.agentspeak.agent.IAgent;

import java.util.stream.Stream;


/**
 * provider interface
 */
public interface IProvider
{

    /**
     * register an agent with a name
     *
     * @param p_id agent name / id (case-insensitive)
     * @param p_agent agent object
     * @return self reference
     */
    IProvider register( final String p_id, final IAgent<?> p_agent );

    /**
     * unregister agent by the name
     *
     * @param p_id agent name / id (case insensitive )
     * @return stream of removed agents
     */
    Stream<? extends IAgent<?>> unregister( final String p_id );

    /**
     * unregister agent by the objct
     *
     * @param p_agent agent object
     * @return stream of removed agents
     */
    Stream<? extends IAgent<?>> unregister( final IAgent<?>... p_agent );

    /**
     * unregister agent by the objct
     *
     * @param p_agent agent stream
     * @return stream of removed agents
     */
    Stream<? extends IAgent<?>> unregister( final Stream<? extends IAgent<?>> p_agent );

    /**
     * returns a depend provider
     *
     * @return provider stream
     */
    Stream<IProvider> dependprovider();

}
