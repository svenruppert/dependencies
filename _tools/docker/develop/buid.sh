#!/bin/bash
    echo start building the images
    podman build -t svenruppert/zulu-dev .

    podman tag svenruppert/zulu-dev:latest svenruppert/zulu-dev:21.0.1-zulu
    #docker push svenruppert/zulu-dev:21.0.1-zulu

    #docker image rm svenruppert/zulu-dev:latest
