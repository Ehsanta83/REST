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

import java.util.stream.Stream;


/**
 * provider interface
 */
public interface IProvider<T>
{

    /**
     * register an object with a name
     *
     * @param p_id id (case-insensitive)
     * @param p_object object
     * @return self reference
     */
    IProvider<T> register( final String p_id, final T p_object );

    /**
     * unregister object by the name
     *
     * @param p_id id (case insensitive )
     * @return stream of removed objects
     */
    Stream<? extends T> unregister( final String p_id );

    /**
     * unregister objects
     *
     * @param p_object objects
     * @return stream of removed objects
     */
    Stream<? extends T> unregister( final T... p_object );

    /**
     * unregister objects
     *
     * @param p_objects object stream
     * @return stream of removed objects
     */
    Stream<? extends T> unregister( final Stream<? extends T> p_objects );

    /**
     * returns a depend provider
     *
     * @return provider stream
     */
    Stream<IProvider<T>> dependprovider();

}
