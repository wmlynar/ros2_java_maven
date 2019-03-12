#!/bin/sh

VERSION=0.0.1-SNAPSHOT
REPOSITORY_ID=XXXX
REPOSITORY_URL=https://nexus.YYYY/repository/maven-snapshots/

# deploy rcljava and rcljava_common libraries

for file in `find ~/ros2_java_ws/build_isolated/ -name *rcl*.jar`
do
  echo "Deploying $file as $(basename ${file%.*})"
  mvn deploy:deploy-file -Dfile="$file" -DgroupId=org.ros2.java -DartifactId="$(basename ${file%.*})" -Dversion=$VERSION -Dpackaging=jar -Durl=$REPOSITORY_URL -DrepositoryId=$REPOSITORY_ID -DretryFailedDeploymentCount=5
done

# deploy all messages

for file in `find ~/ros2_java_ws/build_isolated/ -name *messages.jar`
do
  echo "Deploying $file as $(basename ${file%.*})"
  mvn deploy:deploy-file -Dfile="$file" -DgroupId=org.ros2.java -DartifactId="$(basename ${file%.*})" -Dversion=$VERSION -Dpackaging=jar -Durl=$REPOSITORY_URL -DrepositoryId=$REPOSITORY_ID -DretryFailedDeploymentCount=5
done

# deploy ros2-java-maven

mvn deploy -DaltDeploymentRepository=$REPOSITORY_ID::default::$REPOSITORY_URL -DretryFailedDeploymentCount=5
