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
import org.lightjason.agentspeak.generator.IAgentGenerator;
import org.lightjason.agentspeak.generator.IGenerator;

import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * generator wrapper
 */
public final class CAgentGeneratorWrapper implements IGeneratorWrapper<IAgentGenerator<?>>
{
    /**
     * optional supplier of the generator data
     */
    private final Supplier<Object[]> m_supplier;
    /**
     * consumer of the agents
     */
    private final Consumer<IAgent<?>> m_consumer;
    /**
     * agent generator
     */
    private final IAgentGenerator<?> m_generator;

    /**
     * ctor
     *
     * @param p_consumer consumer for agents
     * @tparam T agent type
     */
    public CAgentGeneratorWrapper( final IAgentGenerator<?> p_generator, final Consumer<IAgent<?>> p_consumer )
    {
        this( p_generator, null, p_consumer );
    }

    /**
     * ctor
     *
     * @param p_supplier supplier for agent generating call
     * @param p_consumer consumer for agents
     * @tparam T agent type
     */
    public CAgentGeneratorWrapper( final IAgentGenerator<?> p_generator, final Supplier<Object[]> p_supplier, final Consumer<IAgent<?>> p_consumer )
    {
        m_generator = p_generator;
        m_supplier = p_supplier;
        m_consumer = p_consumer;
    }


    @Override
    public void generate()
    {
        m_consumer.accept( m_generator.generatesingle( m_supplier == null ? null : m_supplier.get() ) );
    }

    @Override
    public final void generate( final int p_number )
    {
        m_generator.generatemultiple( p_number, m_supplier == null ? null : m_supplier.get() ).forEach( m_consumer );
    }

    @Override
    public final IAgentGenerator<?> generator()
    {
        return m_generator;
    }

    @Override
    public final int hashCode()
    {
        return m_generator.hashCode();
    }

    @Override
    public final boolean equals( final Object p_object )
    {
        return ( p_object != null ) && ( ( p_object instanceof IGeneratorWrapper ) || ( p_object instanceof IGenerator<?> ) ) && ( this.hashCode() == p_object.hashCode() );
    }
}
