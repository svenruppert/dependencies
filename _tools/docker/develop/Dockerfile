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

FROM debian:trixie-slim

LABEL maintainer="Sven Ruppert sven.ruppert@gmail.com" description="zulu-dev-fx:21.0.1-zulu"

# Defining default Java and Maven version
ARG JAVA_VERSION="21.0.1-zulu"
ARG MAVEN_VERSION="3.9.6"

# Defining default non-root user UID, GID, and name
ARG USER_UID="4000"
ARG USER_GID="4000"
ARG USER_NAME="worker"

# Creating default non-user
RUN groupadd -g $USER_GID $USER_NAME && \
	useradd -m -g $USER_GID -u $USER_UID $USER_NAME

# Installing basic packages
RUN apt-get update && \
	apt-get install -y zip unzip curl && \
	rm -rf /var/lib/apt/lists/* && \
	rm -rf /tmp/*

# Switching to non-root user to install SDKMAN!
# USER $USER_UID:$USER_GID

# Downloading SDKMAN!
RUN curl -s "https://get.sdkman.io" | bash

# Installing Java and Maven, removing some unnecessary SDKMAN files
RUN bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && \
    yes | sdk install java $JAVA_VERSION && \
    yes | sdk install maven $MAVEN_VERSION && \
    rm -rf $HOME/.sdkman/archives/* && \
    rm -rf $HOME/.sdkman/tmp/*"

#ENV PATH=/home/$USER_NAME/.sdkman/candidates/java/current/bin:$PATH
#ENV PATH=/home/$USER_NAME/.sdkman/candidates/maven/current/bin:$PATH
ENV PATH=/root/.sdkman/candidates/java/current/bin:$PATH
ENV PATH=/root/.sdkman/candidates/maven/current/bin:$PATH

CMD ["mvn"]