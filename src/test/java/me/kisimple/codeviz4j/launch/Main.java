package me.kisimple.codeviz4j.launch;

/**
 * Created by blues on 4/30/16.
 */
public class Main {

    private static final String[] ARGUMENTS = new String[] {
            "-proc:only",

            "-processor",
            "me.kisimple.codeviz4j.processor.MethodInvocationProcessor",

            "me/kisimple/codeviz4j/buuug/v1/i001/StaticClazz.java",
            "me/kisimple/codeviz4j/buuug/v1/i002/GenericMethod.java",
            "me/kisimple/codeviz4j/buuug/v1/i003/AnonymousClazz.java",
            "me/kisimple/codeviz4j/buuug/v1/i004/InnerClazz.java",
    };

    public static void main(String[] args) {
        com.sun.tools.javac.main.Main compiler =
                new com.sun.tools.javac.main.Main("javac");
//        setWorkingDir();
        compiler.compile(ARGUMENTS);
    }

}
