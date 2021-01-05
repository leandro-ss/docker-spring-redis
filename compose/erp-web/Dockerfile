FROM centos:6

# Variáveis de ambiente
ARG TOMCAT_VERSION=9.0.17

ENV LANG=pt_BR.UTF-8 \
    CLIENTE=beta \
    NEWRELIC=0 \
    JAVA_OPTS="-Xms1g \
    -Xmx1g \
    -Duser.language=pt \
    -Duser.country=BR \
    -Djava.net.preferIPv4Stack=true \
    -Dsun.security.ssl.allowUnsafeRenegotiation=true \
    -Xverify:none \
    -XX:CompileThreshold=1500 \
    -XX:ActiveProcessorCount=1 \
    -Dcom.sun.management.jmxremote=true \
    -Dcom.sun.management.jmxremote.port=30001 \
    -Dcom.sun.management.jmxremote.authenticate=false \
    -Dcom.sun.management.jmxremote.ssl=false \
    -Djava.rmi.server.hostname=192.168.253.210 \
    -Dcom.sun.management.jmxremote.rmi.port=30001 \
    -XX:ReservedCodeCacheSize=192m \
    -Djava.awt.headless=true \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/home/ec2-user/heapdump.hprof \
    -XX:OnOutOfMemoryError=\"/opt/hprof_s3.sh\"" \
    CATALINA_OPTS="-javaagent:/home/ec2-user/elastic-apm-agent.jar \
    -Delastic.apm.service_name=erp \
    -Delastic.apm.application_packages=br.com.bluesoft \
    -Delastic.apm.server_urls=http://apm-server:8200 \
    -Delastic.apm.use_path_as_transaction_name=true \
    -Delastic.apm.enable_log_correlation=true \
    -Delastic.apm.transaction_sample_rate=1.0 \
    -Delastic.apm.transaction_max_spans=500 \
    -Delastic.apm.capture_jmx_metrics=true \
    -Delastic.apm.max_queue_size=512 \
    -Delastic.apm.stack_trace_limit=0 \
    -Delastic.apm.span_frames_min_duration=1ms \
    -Delastic.apm.capture_body=ALL \
    -Delastic.apm.sanitize_field_names=*pass*,*secret*,*senha*,*credit*,*card*,*token*,*session*,authorization,set-cookie,X-* \
    -Delastic.apm.capture_headers=false \
    -Delastic.apm.ignore_urls=*.js,*.css,*.jpg,*.jpeg,*.png,*.gif,*.webp,*.svg,*.ttf,*.woff,*.woff2,*.map,*.html,/seguranca/login/keepalive.action,/healthcheck/status.action,/openServices/agent/status.action"

#instalacao de pacotes
RUN yum install -y epel-release && yum update -y && yum install -y curl java-1.8.0-openjdk jq python python-pip unzip

#configuracao S.O.
RUN rm -f /etc/localtime \
    && ln -s /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime

RUN echo "LANG=pt_BR.UTF-8" > /etc/sysconfig/i18n \
    && localedef -c -i pt_BR -f UTF-8 pt_BR.UTF-8

#Configuracao de usuario
RUN useradd -m -u 500 ec2-user
WORKDIR /home/ec2-user/
USER ec2-user

# Instalacao Tomcat
RUN TOMCAT_MAJOR=${TOMCAT_VERSION%%.*} \
    && curl -sO https://archive.apache.org/dist/tomcat/tomcat-${TOMCAT_MAJOR}/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    && tar -xzf apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    && ln -s /home/ec2-user/apache-tomcat-${TOMCAT_VERSION} /home/ec2-user/tomcat \
    && rm -f apache-tomcat-${TOMCAT_VERSION}.tar.gz \
    && rm -rf /home/ec2-user/tomcat/webapps/* \
    && curl -so  /home/ec2-user/tomcat/lib/ojdbc6-11.2.0.4.0.jar https://s3-sa-east-1.amazonaws.com/bluesoft-sp/install/oracle/ojdbc6-11.2.0.4.0.jar \
    && curl -so  /home/ec2-user/tomcat/lib/postgresql-42.2.5.jar https://s3-sa-east-1.amazonaws.com/bluesoft-sp/install/postgres/postgresql-42.2.5.jar \
    && curl -so  /home/ec2-user/tomcat/lib/rollback-interceptor-1.0.jar https://s3.amazonaws.com/bluesoft/install/rollback-interceptor-1.0.jar

ADD *.xml hprof_s3.sh run.sh prepare.py /opt/
COPY erp-web-1.0.0.war /home/ec2-user/tomcat/webapps/erp-web.war
COPY elastic-apm-agent-1.9.1.jar /home/ec2-user/elastic-apm-agent.jar
COPY elasticapm.properties /home/ec2-user/elasticapm.properties

RUN cp /opt/server.xml /opt/web.xml /opt/context.xml tomcat/conf/

CMD "tomcat/bin/catalina.sh run"