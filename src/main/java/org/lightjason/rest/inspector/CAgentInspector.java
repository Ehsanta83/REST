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

package org.lightjason.rest.inspector;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.instantiable.plan.IPlan;
import org.lightjason.agentspeak.language.instantiable.rule.IRule;
import org.lightjason.rest.container.CAgentContainer;
import org.lightjason.rest.container.IAgentContainer;

import java.util.Map;
import java.util.stream.Stream;


/**
 * inspector of an agent
 */
public final class CAgentInspector implements IAgentInspector
{
    /**
     * export agent object
     */
    private final CAgentContainer<String> m_node = new CAgentContainer<>();

    /**
     * ctor
     *
     * @param p_id agent id
     */
    public CAgentInspector( final String p_id )
    {
        m_node.setID( p_id );
    }

    @Override
    public final void inspectsleeping( final long p_value )
    {
        m_node.setSleeping( p_value );
    }

    @Override
    public final void inspectcycle( final long p_value )
    {
        m_node.setCycle( p_value );
    }

    @Override
    public final void inspectbelief( final Stream<ILiteral> p_value )
    {
        p_value.forEach( i -> m_node.setBelief( i.toString() ) );
    }

    @Override
    public final void inspectplans( final Stream<ImmutableTriple<IPlan, Long, Long>> p_value
    )
    {
    }

    @Override
    public final void inspectrules( final Stream<IRule> p_value )
    {
    }

    @Override
    public final void inspectrunningplans( final Stream<ILiteral> p_value )
    {
        p_value.forEach( i -> m_node.setRunningplan( i.toString() ) );
    }

    @Override
    public final void inspectstorage( final Stream<? extends Map.Entry<String, ?>> p_value )
    {
        p_value.forEach( m_node::setStorage );
    }

    @Override
    public final IAgentContainer get()
    {
        return m_node;
    }
}
