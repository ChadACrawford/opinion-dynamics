# Opinion Dynamics on a Social Network

This is a simple tool that tracks changes of opinions on asocial network using 
models described in the paper [Opposites repel: the effect of incorporating 
repulsion on opinion dynamics in the bounded confidence model](http://dl.acm.org/citation.cfm?id=2485154).

Dynamics are constrained by a static social network, which can be 
configured by a [`.properties` file](default.properties). Currently supported
network generation algorithms are:

* Barabasi-Albert preferential attachment

* Watts-Strogatz small world

* Erdos-Renyi random network

* (semi-complete) A group-based algorithm that generates strongly connected clusters.

There are also some statistics-tracking tools as well, which can be extended in 
an ugly fashion. Those tools call GNUPlot on the outputted data using a `.plt` file.

## Installation

Install [GNUPlot](http://www.gnuplot.info/) on your machine, and make sure that the
`PATH` is adjusted so that it can be called by running

    gnuplot
    
in the command line. Then simply run

    ./gradlew build
    
to install the dependencies. It should run out-of-the-box in IDEA or Eclipse, but
no guarantees.
