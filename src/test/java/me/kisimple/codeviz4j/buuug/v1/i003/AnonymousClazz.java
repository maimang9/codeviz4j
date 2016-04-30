package me.kisimple.codeviz4j.buuug.v1.i003;

/**
 * Created by blues on 4/30/16.
 */
public class AnonymousClazz {

    public static void main(String[] args) {
        Interfaaace i = new Interfaaace() {
            @Override
            public void saySomething() {
                System.out.println("hello world!");
            }
        };
        i.saySomething();
        AbstractClazz ac = new AbstractClazz() {
            @Override
            protected void saySomething() {
                System.out.println("hello world!");
            }
        };
        ac.saySomething();
    }

}
