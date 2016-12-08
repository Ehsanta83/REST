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
import org.lightjason.agentspeak.language.CLiteral;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ILiteral;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.rest.CCommon;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * execution class of api calls
 */
public final class CExecution
{
    /**
     * ctor
     */
    private CExecution()
    {}

    /**
     * runs on an agent stream the cycle
     *
     * @param p_agents agent stream
     * @return stream with execption objects
     */
    public static Stream<Exception> cycle( final Stream<IAgent<?>> p_agents )
    {
        return p_agents
            .parallel()
            .map( i -> {
                try
                {
                    i.call();
                    return null;
                }
                catch ( final Exception l_exception )
                {
                    return l_exception;
                }
            } )
            .filter( Objects::nonNull );
    }

    /**
     * runs sleeping call
     *
     * @param p_agents agent stream
     * @param p_time sleeping time
     * @param p_data any optional data
     */
    public static void sleep( final Stream<IAgent<?>> p_agents, final long p_time, final String p_data )
    {
        final long l_time = p_time <= 0 ? Long.MAX_VALUE : p_time;
        final Set<ITerm> l_terms = parsestringterm( p_data ).collect( Collectors.toSet() );
        p_agents.parallel().forEach( i -> i.sleep( l_time, l_terms.stream() ) );
    }

    /**
     * runs wake-up calls
     *
     * @param p_agents agent stream
     * @param p_data any optional data
     */
    public static void wakeup( final Stream<IAgent<?>> p_agents, final String p_data )
    {
        final Set<ITerm> l_term = parsestringterm( p_data ).collect( Collectors.toSet() );
        p_agents.parallel().forEach( i -> i.wakeup( l_term.stream() ) );
    }

    /**
     * add / deletes an belief
     *
     * @param p_agents agent stream
     * @param p_action action
     * @param p_data belief data
     * @return exception stream
     */
    public static Stream<Exception> belief( final Stream<IAgent<?>> p_agents, final String p_action, final String p_data )
    {
        final Set<ILiteral> l_literal = parsestringterm( p_data ).filter( i -> i instanceof ILiteral )
                                                                 .map( ITerm::<ILiteral>raw )
                                                                 .collect( Collectors.toSet() );
        return p_agents
            .parallel()
            .map( i -> action(
                p_action, i, l_literal.stream(),
                ( n, m ) -> n.beliefbase().add( m ),
                ( n, m ) -> n.beliefbase().remove( m )
            ) )
            .filter( Objects::nonNull );
    }

    /**
     * trigger a goal
     * @param p_agents agent stream
     * @param p_action action
     * @param p_trigger trigger type
     * @param p_data trigger data
     * @param p_immediately immediately execution
     * @return error stream
     */
    public static Stream<Exception> goaltrigger( final Stream<IAgent<?>> p_agents, final String p_action,
                                                 final String p_trigger, final String p_data, final boolean p_immediately )
    {
        final Set<ILiteral> l_literal = parsestringterm( p_data ).filter( i -> i instanceof ILiteral )
                                                                 .map( ITerm::<ILiteral>raw )
                                                                 .collect( Collectors.toSet() );
        final String l_trigger = p_trigger.trim().toLowerCase( Locale.ROOT );
        /*
        switch ( l_trigger )
        {
            case "belief" :
                p_agents
                    .parallel()
                    .map( i -> action(
                        p_action, i, l_literal.stream(),
                        ( n, m ) -> m.forEach( u -> n.trigger( CTrigger.from( ITrigger.EType.ADDBELIEF, u ) ) ),
                        ( n, m ) -> n.beliefbase().remove( m )
                    ) )
                    .filter( Objects::nonNull );


            default:
        }
        */
        return Stream.of();
    }

    /**
     *
     * @param p_action action name
     * @param p_value term value
     * @param p_add add function
     * @param p_delete delete function
     * @tparam T term type
     * @return exception on error null on successfull
     */
    private static <T> Exception action( final String p_action, final IAgent<?> p_agent, final Stream<T> p_value,
                                                       final BiConsumer<IAgent<?>, Stream<T>> p_add, final BiConsumer<IAgent<?>, Stream<T>> p_delete
    )
    {
        try
        {
            final String l_action = p_action.trim().toLowerCase( Locale.ROOT );
            switch ( l_action )
            {
                case "add":
                    p_add.accept( p_agent, p_value );
                    return null;

                case "delete":
                    p_delete.accept( p_agent, p_value );
                    return null;

                default:
                    return new RuntimeException( CCommon.languagestring( CExecution.class, "actionunknown", l_action ) );
            }
        }
        catch ( final Exception l_exception )
        {
            return l_exception;
        }
    }

    /**
     * parses a string to a term set
     *
     * @param p_data string
     * @return term stream
     */
    private static Stream<ITerm> parsestringterm( final String p_data )
    {
        return p_data.isEmpty()
            ? Stream.of()
            : Arrays.stream( p_data.split( ";|\\n" ) )
                    .map( String::trim )
                    .map( i -> parseterm(
                        i,
                        ( j ) -> CRawTerm.from( j ).raw(),
                        CLiteral::parse,
                        ( j ) -> CRawTerm.from( Long.parseLong( j ) ),
                        ( j ) -> CRawTerm.from( Double.parseDouble( j ) )
                    ) );
    }


    /**
     * parse any string data into a term
     *
     * @param p_data string data
     * @param p_return return argument if all parsing functions are failing
     * @param p_parse parsing function
     * @return term
     */
    @SafeVarargs
    private static ITerm parseterm( final String p_data, final Function<String, ITerm> p_return, final IFunction<String, ITerm>... p_parse )
    {
        return Arrays.stream( p_parse )
                     .map( i -> {
                         try
                         {
                             return i.apply( p_data );
                         }
                         catch ( final Exception l_exception )
                         {
                             return null;
                         }
                     } )
                     .filter( Objects::nonNull )
                     .findFirst()
                     .orElseGet( () -> p_return.apply( p_data ) );
    }

}
