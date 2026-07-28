FROM icr.io/appcafe/open-liberty:full-java21-openj9-ubi-minimal

COPY src/main/liberty/config/server.xml /config/server.xml

# Usamos * para que tome el archivo sin importar si termina en -1.0-SNAPSHOT o similar
COPY target/*.war /config/apps/jbrew-web.war

# Usamos * para que encuentre el driver de postgresql sea cual sea la subcarpeta
COPY target/dependency/postgresql*.jar /config/resources/jdbc/postgresql.jar

