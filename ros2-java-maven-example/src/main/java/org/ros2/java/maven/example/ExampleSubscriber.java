package org.ros2.java.maven.example;

import org.ros2.java.maven.Ros2JavaLibraries;
import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.subscription.Subscription;

public class ExampleSubscriber {

	public static void topicCallback(final std_msgs.msg.String msg) {
		System.out.println("I heard: [" + msg.getData() + "]");
	}

	public static void main(String[] args) throws Exception {
		Ros2JavaLibraries.unpack();
		long contextHandle = RCLJava.rclJavaInit(args);

		// Let's create a Node
		Node node = RCLJava.createNode("example_subscriber", args, true, contextHandle);

		Subscription<std_msgs.msg.String> sub = node.<std_msgs.msg.String>createSubscription(std_msgs.msg.String.class,
				"topic", ExampleSubscriber::topicCallback);

		RCLJava.spin(node, contextHandle);

	}

}
