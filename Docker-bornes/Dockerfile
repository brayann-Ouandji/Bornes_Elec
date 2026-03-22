# On part de l'image officielle Tomcat 11 avec Java 21
FROM tomcat:11-jdk21

# On supprime les apps par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# On copie le fichier WAR de l'application dans le dossier webapps de Tomcat
COPY Bornes_Elec.war /usr/local/tomcat/webapps/ROOT.war

# On expose le port 8080 pour accéder à l'application
EXPOSE 8080