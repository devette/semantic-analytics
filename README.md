# Demo - Semantic Analytics (Using AFM short sell register)

- with server
    - cloudfoundry ready
    - embedded Apache Fuseki2 / Jetty server
    - Apache Jena with RDF datasets
    - Angular based faceted search and query frontend.

# preparation
- Install Intellij (Community)
    - https://www.jetbrains.com/idea/download/
- Install Apache Maven
    - https://maven.apache.org
- Install Node
    - https://nodejs.org/en/
- Install bower (requires node)
    - `npm i -g bower`
- Install Pivotal cloud foundry
    - https://run.pivotal.io

# Installation

from the root of the sources
`mvn clean package`

from the client-angular directory
`cd client-angular`
`bower install`

# Running locally

After succesful installations

`cd configserver/`
`java -jar target/server-1.0-SNAPSHOT.jar`

Then navigate to
    - http://localhost:8080/index.html

# Running on the cloud

Go to https://run.pivotal.io and sign up.
`cf login`
`cf push`
