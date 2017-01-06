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

package org.lightjason.rest.container;

import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.variable.IVariable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.stream.Collectors;


/**
 * literal container
 */
@XmlRootElement( name = "literal" )
public final class CLiteral implements ITerm
{
    /**
     * functor
     */
    @XmlElement( name = "functor" )
    private final String m_functor;
    /**
     * parallel flag
     */
    @XmlElement( name = "parallel" )
    private final boolean m_parallel;
    /**
     * negated flag
     */
    @XmlElement( name = "negated" )
    private final boolean m_negated;
    /**
     * literal values
     */
    @XmlElement( name = "value" )
    private final List<ITerm> m_value;
    /**
     * annotation values
     */
    @XmlElement( name = "annotation" )
    private final List<ITerm> m_annotation;

    /**
     * ctor
     *
     * @param p_literal literal
     */
    public CLiteral( final ILiteral p_literal )
    {
        m_functor = p_literal.functor();
        m_parallel = p_literal.hasAt();
        m_negated = p_literal.negated();
        m_value = p_literal.values().map( CLiteral::generate ).collect( Collectors.toList() );
        m_annotation = p_literal.annotations().map( CLiteral::new ).collect( Collectors.toList() );
    }

    /**
     * generates the container elements
     *
     * @param p_term term
     * @return container term element
     */
    private static ITerm generate( final org.lightjason.agentspeak.language.ITerm p_term )
    {
        if ( p_term instanceof ILiteral )
            return new CLiteral( p_term.raw() );

        if ( p_term instanceof IVariable<?> )
            return new CVariable( p_term.functor(), p_term.raw() );

        return new CRaw<>( p_term.raw() );
    }
}
