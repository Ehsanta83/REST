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
import java.text.MessageFormat;


/**
 * variable container
 */
@XmlRootElement( name = "variable" )
public final class CVariable implements ITerm
{
    /**
     * functor
     */
    @XmlElement( name = "functor" )
    private final String m_functor;
    /**
     * value
     */
    @XmlElement( name = "value" )
    private final Object m_value;

    /**
     * ctor
     *
     * @param p_functor functor
     * @param p_value value
     */
    public CVariable( final String p_functor, final Object p_value )
    {
        m_functor = p_functor;
        m_value = p_value;
    }

    @Override
    public final String toString()
    {
        return MessageFormat.format( "{0}({1})", m_functor, m_value );
    }
}
