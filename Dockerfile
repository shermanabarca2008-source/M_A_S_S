FROM icr.io/appcafe/open-liberty:full-java25-openj9-ubi-minimal
COPY src/main/liberty/config/server.xml /config/server.xml
COPY target/M_A_S_S.war /config/apps/
