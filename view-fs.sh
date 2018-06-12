#!/bin/sh
# create image (snapshot) from container filesystem
#docker commit $@ qsnapshot

# explore this filesystem using bash (for example)
#docker run -t -i qsnapshot /bin/bash

docker exec -it $@ bash