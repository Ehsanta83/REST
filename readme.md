# LightJason - REST-API

![Circle CI](https://circleci.com/gh/LightJason/REST.svg?style=shield)
[![Coverage Status](https://coveralls.io/repos/github/LightJason/REST/badge.svg?branch=master)](https://coveralls.io/github/LightJason/REST?branch=master)


## Requirements

* [JRE 1.8](http://www.java.com/)
* Java-based webserver [Tomcat](http://tomcat.apache.org/), [Jetty](http://www.eclipse.org/jetty/) or [GlassFish](https://glassfish.java.net/)

### Development

* [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/)
* [Maven 3 or higher](http://maven.apache.org/)
* [Doxygen](http://www.doxygen.org/) with [GraphViz](http://www.graphviz.org/)
* [Source code documentation](http://lightjason.github.io/REST/)
* [Open Hub Code Statistic](https://www.openhub.net/p/LightJason-REST)
* [Libraries.io Statistic](https://libraries.io/github/LightJason/REST)


## Usage

### URL pattern to control a single agent

* ```/agent/list``` (HTTP-GET) returns a list of all registered agent names
* ```/agent/cycle``` (HTTP-GET) executes the cycle of all registered agents
* ```/agent/<agent identifier>/cycle``` (HTTP-GET) executes the agent cycle
* ```/agent/<agent identifier>/view``` (HTTP-GET) returns the current state of the agent
* ```/agent/<agent identifier>/sleep?time=``` (HTTP-GET) pushs the agent into sleeping state for a defined time (time parameter is optional, if is not set the time is unlimited)
* ```/agent/<agent identifier>/wakeup``` (HTTP-GET & POST) wakes the agent up from sleeping state and via post can be passed a semicolon / line-break list with literals as plain-text which are pushed into the wake-up goal
* ```/agent/<agent identifier>/trigger/<action>/<type>``` (HTTP-POST) triggers a goal within the next cycle, the action can be ```add (+)``` or ```delete (-)``` and the type ```goal``` or ```belief```
* ```/agent/<agent identifier>/trigger/<action>/<type>/immediately``` (HTTP-POST) triggers a goal immediately (equal to trigger-call)
* ```/agent/<agent identifier>/belief/<action>``` (HTTP-POST) modifies the beliefbase with action ```add``` or ```delete``` and a literal which is passed by the post plain-text data

### URL pattern to control a group of agents

* ```/agentgroup/list``` (HTTP-GET)  list all groups with the names
* ```/agentgroup/<group name>/list``` (HTTP-GET) list all agents within the group
* ```/agentgroup/<group>/cycle``` (HTTP-GET) executes the agent cycle of a group
* ```/agentgroup/<group>/sleep?time=``` (HTTP-GET) pushs the agent into sleeping state for a defined time (time parameter is optional, if is not set the time is unlimited)
* ```/agentgroup/<group>/wakeup``` (HTTP-GET & POST) runs the wake-up call of all agents within a group (the post content data can contains literals seperated by semicolon or line-break)
* ```/agentgroup/<group>/belief/<action>``` (HTTP-POST) modifies the beliefbase with action ```add``` or ```delete``` and a literal which is passed by the post plain-text data
* ```/agentgroup/<group>/trigger/<action>/<type>``` (HTTP-POST) triggers a goal within the next cycle, the action can be ```add (+)``` or ```delete (-)``` and the type ```goal``` or ```belief```
* ```/agentgroup/<group>/trigger/<action>/<type>/immediately``` (HTTP-POST) triggers a goal immediately (equal to trigger-call)
