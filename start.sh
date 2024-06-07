#!/bin/bash
# IMPORTANT - this file is inspired by file https://github.com/zalando/patroni/blob/master/docker/entrypoint.sh

DOCKER_IP=$(hostname --ip-address)
PATRONI_SCOPE=${PATRONI_SCOPE:-batman}

ttl=${ttl:-30}
loop_wait=${loop_wait:-10}
retry_timeout=${retry_timeout:-10}
maximum_lag_on_failover=${maximum_lag_on_failover:-1048576}
master_start_timeout=${master_start_timeout:-300}
synchronous_mode=${synchronous_mode:-false}
synchronous_mode_strict=${synchronous_mode_strict:-false}
use_pg_rewind=${use_pg_rewind:-true}
use_slots=${use_slots:-true}
wal_level=${wal_level:-hot_standby}
nofailover=${nofailover:-false}
noloadbalance=${noloadbalance:-false}
clonefrom=${clonefrom:-false}
nosync=${nosync:-false}
post_init_script=${post_init_script:-""}

export PATRONI_SCOPE
export PATRONI_NAME="${PATRONI_NAME:-${HOSTNAME}}"
export PATRONI_RESTAPI_CONNECT_ADDRESS="${DOCKER_IP}:8008"
export PATRONI_RESTAPI_LISTEN="0.0.0.0:8008"
export PATRONI_admin_PASSWORD="${PATRONI_admin_PASSWORD:=admin}"
export PATRONI_admin_OPTIONS="${PATRONI_admin_OPTIONS:-createdb, createrole}"
export PATRONI_POSTGRESQL_CONNECT_ADDRESS="${DOCKER_IP}:5432"
export PATRONI_POSTGRESQL_LISTEN="0.0.0.0:5432"
export PATRONI_POSTGRESQL_DATA_DIR="data/${PATRONI_SCOPE}"
export PATRONI_REPLICATION_USERNAME="${PATRONI_REPLICATION_USERNAME:-replicator}"
export PATRONI_REPLICATION_PASSWORD="${PATRONI_REPLICATION_PASSWORD:-abcd}"
export PATRONI_SUPERUSER_USERNAME="${PATRONI_SUPERUSER_USERNAME:-postgres}"
export PATRONI_SUPERUSER_PASSWORD="${PATRONI_SUPERUSER_PASSWORD:-postgres}"
export PATRONI_POSTGRESQL_PGPASS="$HOME/.pgpass"

# psql logging
POSTGRES_LOG_DIR=${POSTGRES_LOG_DIR:-/tmp}
POSTGRES_LOG_FILENAME=${POSTGRES_LOG_FILENAME:-postgresql.log}
POSTGRES_LOG_MIN_MESSAGES=${POSTGRES_LOG_MIN_MESSAGES:-INFO}
POSTGRES_LOG_LOG_CONNECTIONS=${POSTGRES_LOG_LOG_CONNECTIONS:-true}
POSTGRES_LOG_LOG_DISCONNECTIONS=${POSTGRES_LOG_LOG_DISCONNECTIONS:-true}

cat > /post_init.sh <<__EOF__
#!/bin/bash
${post_init_script}
__EOF__

# create patroni.yml which is used for patroni/postgresql configuration
cat > /patroni.yml <<__EOF__
bootstrap:
  post_init: /post_init.sh
  dcs:
    ttl: ${ttl}
    loop_wait: ${loop_wait}
    retry_timeout: ${retry_timeout}
    maximum_lag_on_failover: ${maximum_lag_on_failover}
    master_start_timeout: ${master_start_timeout}
    synchronous_mode: ${synchronous_mode}
    synchronous_mode_strict: ${synchronous_mode_strict}

    postgresql:
      use_pg_rewind: ${use_pg_rewind}
      use_slots: ${use_slots}
      wal_level: ${wal_level}
      parameters:
        logging_collector: on
        log_directory: ${POSTGRES_LOG_DIR}
        log_filename: ${POSTGRES_LOG_FILENAME}
        log_min_messages: ${POSTGRES_LOG_MIN_MESSAGES}
        log_connections: ${POSTGRES_LOG_LOG_CONNECTIONS}
        log_disconnections: ${POSTGRES_LOG_LOG_DISCONNECTIONS}
        shared_preload_libraries: timescaledb

  pg_hba:
  - host all all 0.0.0.0/0 md5
  - host replication replicator ${DOCKER_IP}/16    md5
tags:
  nofailover: ${nofailover}
  noloadbalance: ${noloadbalance}
  clonefrom: ${clonefrom}
  nosync  : ${nosync}

__EOF__

mkdir -p "$HOME/.config/patroni"
[ -h "$HOME/.config/patroni/patronictl.yaml" ] || ln -s /patroni.yml "$HOME/.config/patroni/patronictl.yaml"

patroni /patroni.yml

while true; do
    sleep 60
done
