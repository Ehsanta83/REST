# LightJason - REST-API

![Circle CI](https://circleci.com/gh/LightJason/REST.svg?style=shield)


## Requirements

* [JRE 1.8](http://www.java.com/)
* Java-based webserver [Tomcat](http://tomcat.apache.org/), [Jetty](http://www.eclipse.org/jetty/) or [GlassFish](https://glassfish.java.net/)

### Development

* [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/)
* [Maven 3 or higher](http://maven.apache.org/)
* [Doxygen](http://www.doxygen.org/) with [GraphViz](http://www.graphviz.org/)
* [Source code documentation](http://lightjason.github.io/REST/)
* [Open Hub Code Statistic](https://www.openhub.net/p/LightJason-REST)


## Usage

The component defines a set of URL pattern to control an agent:

* ```/agent/list``` (HTTP-GET) returns a list of all registered agent names
* ```/agent/cycle``` (HTTP-GET) executes the cycle of all registered agents
* ```/agent/<agent identifier>/cycle``` (HTTP-GET) executes the agent cycle
* ```/agent/<agent identifier>/view``` (HTTP-GET) returns the current state of the agent
* ```/agent/<agent identifier>/sleep?time=``` (HTTP-GET) pushs the agent into sleeping state for a defined time (time parameter is optional, if is not set the time is unlimited)
* ```/agent/<agent identifier>/wakeup``` (HTTP-Get & POST) wakes the agent up from sleeping state and via post can be passed a semicolon / line-break list with literals as plain-text which are pushed into the wake-up goal
* ```/agent/<agent identifier>/trigger/<goal trigger>``` (HTTP-POST) triggers a goal within the next cycle, the goal trigger can be ```addgoal (+!)```, ```deletegoal (-!)```, ```addbelief (+)``` or ```deletebelief (-)```
* ```/agent/<agent identifier>/trigger/<goal trigger>/immediately``` (HTTP-POST) triggers a goal immediately (equal to trigger-call)
* ```/agent/<agent identifier>/belief/<belief trigger>``` (HTTP-POST) modifies the beliefbase with belief trigger ```add``` or ```delete``` and a literal which is passed by the post plain-text data

