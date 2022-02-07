## compute

### Flexible Computation Service

Allows one to "collect" data from the external environment via adapters
into a source kafka topic, at regular intervals and then process each item 
from the topic by performing computations on each collected piece of 
information and placing into a destination kafka topic

### Demonstrates use of

* Scala 3
* Cats Effect 3
* fs2/kafka
* Http4s (for checking the contents of both kafka topics)
* sttp (for making http requests to ChemPub)

### Possible future expansions

- ability to dynamically customize adapters and computation rules
- possible integration with espresso library or fork (for typesafe definition of computation expressions)