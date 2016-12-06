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


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * plan container
 */
@XmlRootElement( name = "plan" )
public final class CPlan implements IPlan
{
    /**
     * name / type of the trigger
     */
    @XmlElement( name = "trigger" )
    private final String m_trigger;
    /**
     * plan literal
     */
    @XmlElement( name = "literal" )
    private final ITerm m_literal;
    /**
     * successfully runs
     */
    @XmlElement( name = "sucess" )
    private final long m_successful;
    /**
     * failed runs
     */
    @XmlElement( name = "fail" )
    private final long m_fail;

    /**
     * ctor
     *
     * @param p_plan plan
     * @param p_successful successfully runs
     * @param p_fail failed runs
     */
    public CPlan( final org.lightjason.agentspeak.language.instantiable.plan.IPlan p_plan, final long p_successful, final long p_fail )
    {
        m_trigger = p_plan.getTrigger().getType().toString();
        m_literal = new CLiteral( p_plan.getTrigger().getLiteral() );
        m_successful = p_successful;
        m_fail = p_fail;
    }
}
