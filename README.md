# Demo - Semantic Analytics (Using AFM short sell register)
=======
[![Build Status](https://travis-ci.org/devette/semantic-analytics.svg?branch=master)](https://travis-ci.org/devette/semantic-analytics)

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

# Building

from the root of the sources
- `mvn clean package`

from the client-angular directory
- `cd client-angular`
- `bower install`

# Running locally

After succesful installations

- `cd configserver/`
    - `mvn test -Plocalhost` *or*
    - `java -jar target/server-1.0-SNAPSHOT.jar`

Then navigate to
    - http://localhost:8080/index.html

# Running on the cloud

### Prepare
- Go to https://run.pivotal.io and sign up.
- open a terminal
- `cf login`

### Server
To run the server application in the cloud.
- `cd server`
- `cf push`

### Client
To run the client application in the cloud.
- update the endpoint in `angular-client/search-service.js` to the given route for the server app.
- `cd angular-client`
- `cf push`
