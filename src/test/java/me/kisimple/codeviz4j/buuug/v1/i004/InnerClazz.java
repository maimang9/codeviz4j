package me.kisimple.codeviz4j.buuug.v1.i004;

/**
 * Created by blues on 4/30/16.
 */
public class InnerClazz {

    static class StaticInnerClazz {
        public void saySomething() {
            System.out.println("hello world!");
        }
    }
    private class PublicInnerClazz {
        public void saySomething() {
            System.out.println("hello world!");
        }
    }
    private class PrivateInnerClazz {
        public void saySomething() {
            System.out.println("hello world!");
        }
    }

    public static void main(String[] args) {
        new StaticInnerClazz().saySomething();
        new InnerClazz().new PublicInnerClazz().saySomething();
        new InnerClazz().new PrivateInnerClazz().saySomething();
    }

}
