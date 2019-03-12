Mavenized ROS2 for Java
=======================

This project enables usage of ros2_java *without ros2 installed on your computer*.
It was prepared and tested with ros2 Crystal Clemmys on ubuntu 16.04 Xenial.

Using in your maven project
---------------------------

Please look at the example in the folder `ros2-java-maven-example` on how to use ros2_java_maven in your project.
Please add following dependencies to your maven project 

```
    <dependencies>
        <dependency>
            <groupId>org.ros2.java</groupId>
            <artifactId>ros2-java-maven</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.ros2.java</groupId>
            <artifactId>rcljava</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.ros2.java</groupId>
            <artifactId>rcljava_common</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.ros2.java</groupId>
            <artifactId>std_msgs_messages</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
```

as well as add maven repository hosted on github to be able to download those dependencies

```
    <repositories>
        <repository>
            <id>ros2_java_maven_repo</id>
            <url>https://raw.github.com/wmlynar/ros2_java_maven_repo/master/</url>
        </repository>
    </repositories>
```

Build and run the example

```
git clone https://github.com/wmlynar/ros2_java_maven
cd ros2_java_maven/ros2-java-maven-example
mvn install
java -jar target/ros2-java-maven-example-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```

You should see on the screen the information that you need to add folder
with ROS2 libraries to your LD_LIBRARY_PATH. Please execute following command in your terminal
or add it to ~/.barshrc and start a new terminal (where /tmp/ros2_java_libs is the temporary directory
where the libraries are unpacked, which is specific to your system)

```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/tmp/ros2_java_libs
```

Now when executed ros2-java-maven-example you should see ROS2 messages published on the screen

```
~/ros2_java_maven/ros2-java-maven-example# export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/tmp/ros2_java_libs
~/ros2_java_maven/ros2-java-maven-example# java -jar target/ros2-java-maven-example-0.0.20190217-SNAPSHOT-jar-with-dependencies.jar 
225 [main] INFO org.reflections.Reflections - Reflections took 151 ms to scan 11 urls, producing 6230 keys and 6472 values 

Copied ros2_java libraries to: /tmp/ros2_java_libs
Please add to following line to your .bashrc
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/tmp/ros2_java_libs

1831 [main] INFO org.ros2.rcljava.common.JNIUtils - Loading implementation: org_ros2_rcljava_rcl_java__jni
1843 [main] INFO org.ros2.rcljava.RCLJava - Using RMW implementation: rmw_fastrtps_cpp
1855 [main] INFO org.ros2.rcljava.common.JNIUtils - Loading implementation: org_ros2_rcljava_node__node_impl__jni
1860 [main] INFO org.ros2.rcljava.common.JNIUtils - Loading typesupport: std_msgs_msg__string__jni__rosidl_typesupport_c
1862 [main] INFO org.ros2.rcljava.common.JNIUtils - Loading implementation: org_ros2_rcljava_publisher__publisher_impl__jni
Publishing: [Hello, world! 0]
1867 [main] INFO org.ros2.rcljava.common.JNIUtils - Loading implementation: org_ros2_rcljava_executors__base_executor__jni
Publishing: [Hello, world! 1]
Publishing: [Hello, world! 2]
Publishing: [Hello, world! 3]
Publishing: [Hello, world! 4]
Publishing: [Hello, world! 5]
Publishing: [Hello, world! 6]
Publishing: [Hello, world! 7]

```

Building ros2_java_maven libraries yourself
-------------------------------------------

This is the instruction how to build ros2_java_maven on Ubuntu systems.
Let me know if you encounter any issues or have problem with the build.

Step 1. Build ros2_java into folder `~/ros2_java_ws`
----------------------------------------------------

Build ros2_java using the option install_isolated (or change the name of the folder in the scripts)

The best is to follow instructions at https://github.com/wmlynar/ros2_java

Step 2. Clone ros2_java_maven into `~/ros2_java_ws/src`
-------------------------------------------------------

ros2_java_maven build uses relative paths in the scripts so it is important that the folders are correct

```
cd ~/ros2_java_ws/src
git clone https://github.com/wmlynar/ros2_java_maven
```

Step 3. Install ros2_java_maven to your local maven repository
--------------------------------------------------------------

```
./install_jars.sh
./install_maven.sh
```

Step 4. Add LD_LIBRARY_PATH to your .bashrc
---------------------------------------

The name of the folder where libraries will be unpacked is displayed when you first time run the example.

```
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/tmp/ros2_java_libs
```

Step 5a (github). Optionally deploy maven artifacts to github repo
------------------------------------------------------------------

In order to update github repository you need to copy files from ~/.m2/repository/org/ros2/java/*
into the folder where you checked out the github repository.
Then you need to add the copied files to the maven repository, commit and push.

```
cd ~
git clone https://github.com/wmlynar/ros2_java_maven_repo
cd ros2_java_maven_repo
mkdir -p ./org/ros2/java/
cp -r ~/.m2/repository/org/ros2/java/* ./org/ros2/java/
git add *
git commit -m"0.0.1-SNAPSHOT"
git push
```

Step 5b (nexus). Optionally deploy maven artifacts to nexus
-----------------------------------------------------------

Edit the script deploy.sh with your nexus address and repository id. Do not forget to put credentials to your reposotory in ~/.m2/settings.xml file.

```
./deploy.sh
```

Example settings.xml file:
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                          https://maven.apache.org/xsd/settings-1.0.0.xsd">
	<localRepository />
	<interactiveMode />
	<usePluginRegistry />
	<offline />
	<pluginGroups />
	<servers>
		<server>
			<id>XXXX</id>
			<username>LOGIN</username>
			<password>PASSWORD</password>
		</server>
	</servers>
        <mirrors />
	<proxies />
	<profiles />
	<activeProfiles />
</settings>
```

Congratulations your build is ready!

Run some examples
-----------------

Now you can run examples to verify that your build is correct. Again, do not forget to add LD_LIBRARY_PATH to your .bashrc

```
java -jar ros2-java-maven-example/target/ros2-java-maven-example-0.0.20190217-SNAPSHOT-jar-with-dependencies.jar
```

You should see that messages are being published into ros2

Building the example project with libraries installed at your nexus server
--------------------------------------------------------------------------

Please replace github server with your nexus server in the parent pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

...

        <repositories>
                <repository>
                        <id>XXXX</id>
                        <url>https://nexus.YYYY/repository/maven-public/</url>
                </repository>
        </repositories>
</project>

```

Then please cd to the examples folder and execute the build

```
cd ros2-java-maven-example
mvn install
```
