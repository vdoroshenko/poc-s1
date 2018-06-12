FROM postgres:9.6
COPY ./init/*.sql /docker-entrypoint-initdb.d/
COPY ./docker-healthcheck.sh /
HEALTHCHECK --interval=10s --timeout=3s CMD /docker-healthcheck.sh
