package me.kisimple.codeviz4j.buuug.v1.i004;

/**
 * Created by blues on 4/30/16.
 */
public class InnerClazz {

    static class StaticInnerClazz {
        static int staticInt = 7;
        public void saySomething(int i) {
            System.out.println(i);
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
        new StaticInnerClazz().saySomething(StaticInnerClazz.staticInt);
        new InnerClazz().new PublicInnerClazz().saySomething();
        new InnerClazz().new PrivateInnerClazz().saySomething();
        class LocalClass {
            public void saySomething() {
                System.out.println("hello world!");
            }
        }
        new LocalClass().saySomething();
    }

}
