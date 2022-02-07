## compute

### Flexible Computation Service

Allows one to "collect" data from the external environment via adapters
into a source kafka topic, at regular intervals and then process each item 
from the topic by performing computations on each collected piece of 
information and placing into a destination kafka topic.

We have a few very simple sample adapters that generate test values of different types, but
the more interesting adapter queries ChemPub for a compound name and randomly selects
a synonym from the list of results that we get back.  We could also add additional adapters that
check for output from a running scripts in the local file system, for example.

### Demonstrates use of

* Scala 3
* Cats Effect 3
* fs2/kafka
* Http4s (for checking the contents of both kafka topics)
* sttp (for making http requests to ChemPub)

### Possible future expansions

- ability to dynamically customize adapters and computation rules
- possible integration with espresso library or fork (for typesafe definition of computation expressions)
- supporting aggregation of data read from adapters for computation purposes
- while hard-coded values are fine for a POC, an actual service should load from a configuration