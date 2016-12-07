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

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.lightjason.agentspeak.language.ILiteral;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * container for the restful export
 */
@XmlRootElement( name = "agent" )
public final class CAgentContainer<T> implements IAgentContainer
{
    /**
     * storage element
     */
    private final Map<String, Object> m_storage = new HashMap<>();
    /**
     * existing plans
     */
    private final List<IPlan> m_plans = new ArrayList<>();
    /**
     * running plans
     */
    private final List<ITerm> m_runningplan = new ArrayList<>();
    /**
     * beliefs
     */
    private final List<ITerm> m_belief = new ArrayList<>();
    /**
     * rules
     */
    private final List<ITerm> m_rules = new ArrayList<>();

    /**
     * cycle
     */
    private long m_cycle;
    /**
     * sleeping count
     */
    private long m_sleeping;
    /**
     * agent id
     */
    private T m_id;

    /**
     * get cycle
     * @return cycle
     */
    @XmlElement( name = "cycle" )
    public final long getCycle()
    {
        return m_cycle;
    }

    @Override
    public final IAgentContainer setCycle( final long p_cycle )
    {
        m_cycle = p_cycle;
        return this;
    }

    /**
     * get sleeping
     * @return sleeping count
     */
    @XmlElement( name = "sleeping" )
    public final long getSleeping()
    {
        return m_sleeping;
    }

    @Override
    public final IAgentContainer setSleeping( final long p_sleeping )
    {
        m_sleeping = p_sleeping < 0 ? 0 : p_sleeping;
        return this;
    }

    /**
     * get name / id
     * @return name / id
     */
    @XmlElement( name = "id" )
    public final T getID()
    {
        return m_id;
    }

    /**
     * set name / id
     * @param p_id name / id
     * @return self reference
     */
    public final CAgentContainer<T> setID( final T p_id )
    {
        m_id = p_id;
        return this;
    }

    /**
     * returns belief list
     *
     * @return beliefs
     */
    @XmlElementWrapper( name = "beliefs" )
    @XmlElement( name = "belief" )
    public final List<ITerm> getBelief()
    {
        return m_belief;
    }

    /**
     * sets a belief
     *
     * @param p_belief belief as string
     * @return self reference
     */
    public final CAgentContainer<T> setBelief( final ILiteral p_belief )
    {
        m_belief.add( new CLiteral( p_belief ) );
        return this;
    }

    /**
     * returns the running plans
     *
     * @return list with running plans
     */
    @XmlElementWrapper( name = "runningplans" )
    @XmlElement( name = "runningplan" )
    public final List<ITerm> getRunningplan()
    {
        return m_runningplan;
    }

    /**
     * sets the running plans
     *
     * @param p_plan plan
     * @return self reference
     * @bug fix
     */
    public final CAgentContainer<T> setRunningplan( final ILiteral p_plan )
    {
        m_runningplan.add( new CLiteral( p_plan ) );
        return this;
    }

    /**
     * all existings plans
     *
     * @return plan list
     */
    @XmlElementWrapper( name = "plans" )
    @XmlElement( name = "plan" )
    public final List<IPlan> getPlan()
    {
        return m_plans;
    }

    /**
     * adds a plan
     *
     * @param p_plan plan object
     * @return self reference
     */
    public final CAgentContainer<T> setPlan( final ImmutableTriple<org.lightjason.agentspeak.language.instantiable.plan.IPlan, Long, Long> p_plan )
    {
        m_plans.add( new CPlan( p_plan.getLeft(), p_plan.getMiddle(), p_plan.getMiddle() ) );
        return this;
    }

    /**
     * returns the storage map
     * @return storage map
     */
    @XmlElement( name = "storage" )
    public final Map<String, ?> getStorage()
    {
        return m_storage;
    }

    /**
     * sets a storage item
     *
     * @param p_value storage entry
     * @return self reference
     */
    public final CAgentContainer<T> setStorage( final Map.Entry<String, ?> p_value )
    {
        m_storage.put( p_value.getKey(), p_value.getValue() );
        return this;
    }

}
