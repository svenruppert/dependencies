#!/bin/bash
#
# Copyright © 2013 Sven Ruppert (sven.ruppert@gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

    echo start building the images
    docker build -t svenruppert/zulu-dev .

    docker tag svenruppert/zulu-dev:latest svenruppert/zulu-dev:21.0.1-zulu
    #docker push svenruppert/zulu-dev:21.0.1-zulu

    #docker image rm svenruppert/zulu-dev:latest
