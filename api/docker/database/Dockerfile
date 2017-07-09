FROM postgres

COPY extensionScripts/* /docker-entrypoint-initdb.d

RUN localedef -i en_AU -c -f UTF-8 -A /usr/share/locale/locale.alias en_AU.UTF-8
