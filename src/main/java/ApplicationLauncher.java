/*
command to compile .protoFile:
protoc snakes.proto --java_out=../java/protobufGenerated
 */

public class ApplicationLauncher {
    public static void main(String[] args) {
        JavaFXRunner.main(args);
    }
}
