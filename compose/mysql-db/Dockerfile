FROM mysql:latest

ENV MYSQL_DATABASE='mydb'  \
    MYSQL_ROOT_PASSWORD='mydb123'

ADD dump.sql /docker-entrypoint-initdb.d

EXPOSE 3306/tcp