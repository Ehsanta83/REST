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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.lightjason.rest.CCommon;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.stream.Stream;


/**
 * agent generator provider
 */
@Path( "/agentgenerator" )
public final class CAgentGeneratorProvider implements IProvider<IGeneratorWrapper<?>>
{
    /**
     * generator map
     */
    private final BiMap<String, IGeneratorWrapper<?>> m_generator = Maps.synchronizedBiMap( HashBiMap.create() );


    // --- generator register calls ----------------------------------------------------------------------------------------------------------------------------

    @Override
    public final IProvider<IGeneratorWrapper<?>> register( final String p_id, final IGeneratorWrapper<?> p_object )
    {
        m_generator.putIfAbsent( CCommon.urlformat( p_id ), p_object );
        return this;
    }

    @Override
    public final Stream<? extends IGeneratorWrapper<?>> unregister( final String p_id )
    {
        return Stream.of( m_generator.remove( CCommon.urlformat( p_id ) ) );
    }

    @Override
    public final Stream<? extends IGeneratorWrapper<?>> unregister( final IGeneratorWrapper<?>[] p_object )
    {
        return this.unregister( Arrays.stream( p_object ) );
    }

    @Override
    public final Stream<? extends IGeneratorWrapper<?>> unregister( final Stream<? extends IGeneratorWrapper<?>> p_objects )
    {
        return p_objects
            .map( i -> {
                m_generator.inverse().remove( i );
                return i;
            } );
    }

    @Override
    public final Stream<IProvider<IGeneratorWrapper<?>>> dependprovider()
    {
        return Stream.of();
    }


    // --- api calls -------------------------------------------------------------------------------------------------------------------------------------------

    /**
     * returns a list of all registered generator names
     *
     * @return generator name list
     */
    @GET
    @Path( "/list" )
    @Produces( MediaType.APPLICATION_JSON )
    public final Object view()
    {
        return m_generator.keySet();
    }


    /**
     * runs a single agent creating on all generators
     *
     * @return HTTP response
     */
    @GET
    @Path( "/generate/single" )
    public final Object generatesingle()
    {
        m_generator.values().parallelStream().forEach( IGeneratorWrapper::generate );
        return Response.status( Response.Status.OK ).build();
    }


    /**
     * generates multiple agents on all generators
     *
     * @param p_number number of agents
     * @return HTTP response
     */
    @POST
    @Path( "/generate/multiple" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Object generatemultiple( final int p_number )
    {
        m_generator.values().parallelStream().forEach( i -> i.generate( p_number ) );
        return Response.status( Response.Status.OK ).build();
    }


    /**
     * returns a single agent from a generator
     *
     * @param p_id generator name
     * @return HTTP response
     */
    @GET
    @Path( "/{id}/generate/single" )
    public final Object generatesinglebyid( @PathParam( "id" ) final String p_id )
    {
        final IGeneratorWrapper l_generator = m_generator.get( p_id );
        if ( l_generator == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "generatornotfound", p_id ) ).build();

        l_generator.generate();
        return Response.status( Response.Status.OK ).build();
    }


    /**
     * returns multiple agents from a generator
     *
     * @param p_id generator id
     * @param p_number number of agents
     * @return HTTP response
     */
    @POST
    @Path( "/{id}/generate/multiple" )
    @Consumes( MediaType.TEXT_PLAIN )
    public final Object generatemultiplebyid( @PathParam( "id" ) final String p_id, final int p_number )
    {
        final IGeneratorWrapper l_generator = m_generator.get( p_id );
        if ( l_generator == null )
            return Response.status( Response.Status.NOT_FOUND ).entity( CCommon.languagestring( this, "generatornotfound", p_id ) ).build();

        l_generator.generate( p_number );
        return Response.status( Response.Status.OK ).build();
    }

}
