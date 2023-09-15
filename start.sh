#!/bin/bash

java -jar ../../app.jar &

prometheus --config.file=prometheus.yml &

./grafana server
