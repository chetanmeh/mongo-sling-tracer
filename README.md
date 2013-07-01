Mongo and Apache Sling Request Progress Tracker
===============================================


This module enables logging of queries made to Mongo DB while perfoming any
request processing in Sling to [Request Progress Tracker] [1]. It builds up on
on [Query Logging Enhancement] [2] patch for Mongo Java Driver. This patch enables
logging of calls made to Mongo server.

To make use of this in your Sling instance follow steps below

1. Download and install patched [Mongo Java Driver] [3]. Or build it from [JAVA-374-2.9.x][4] branch
2. Download and install [Mongo Sling Tracer] [5]. Or build it from https://github.com/chetanmeh/mongo-sling-tracer
3. Enable the tracer from Web Console via Configurations. http://localhost:4502/system/console/configMgr/com.github.chetanmeh.mongo.tracer.internal.MongoQueryTracer. Or look for Configuration named `Mongo SLing Request Progress Tracer`
4. Check out the [recent requests][6] tab to see the progress of last couple of request. Below is a sample output
   from a Sling instance running Oak backed by Mongo MK

        250 (2013-07-01 12:36:50) TIMER_START{/libs/foundation/components/primary/cq/Page/Page.jsp#0}
        251 (2013-07-01 12:36:50) LOG Mongo 0 Request : Id: 1637346 (0 ms) OP_QUERY, [nodes] { "_id" : { "$gte" : "3:/var/classes/" , "$lt" : "3:/var/classes0"}}
        252 (2013-07-01 12:36:50) LOG Mongo 1 Request : Id: 1637347 (0 ms) OP_QUERY, [nodes] { "_id" : { "$gte" : "4:/var/classes/org/" , "$lt" : "4:/var/classes/org0"}}
        252 (2013-07-01 12:36:50) LOG Mongo 2 Request : Id: 1637348 (0 ms) OP_QUERY, [nodes] { "_id" : { "$gte" : "5:/var/classes/org/apache/" , "$lt" : "5:/var/classes/org/apache0"}}
        252 (2013-07-01 12:36:50) LOG Mongo 3 Request : Id: 1637349 (0 ms) OP_QUERY, [nodes] { "_id" : { "$gte" : "6:/var/classes/org/apache/jsp/" , "$lt" : "6:/var/classes/org/apache/jsp0"}}
        303 (2013-07-01 12:36:50) LOG Mongo 4 Request : Id: 1637350 (51 ms) OP_QUERY, [nodes] { "_id" : { "$gte" : "7:/var/classes/org/apache/jsp/libs/" , "$lt" : "7:/var/classes/org/apache/jsp/libs0"}}


[1]: http://dev.day.com/content/ddc/blog/2008/06/requestprogresstracker.html
[2]: https://github.com/mongodb/mongo-java-driver/pull/107
[3]: http://chetanmeh.github.io/binary/mongo-java-driver-2.9.4-SNAPSHOT.jar
[4]: https://github.com/chetanmeh/mongo-java-driver/tree/JAVA-374-2.9.x
[5]: http://chetanmeh.github.io/binary/com.github.chetanmeh.mongo-sling-tracer-0.0.1-SNAPSHOT.jar
[6]: http://localhost:4502/system/console/requests