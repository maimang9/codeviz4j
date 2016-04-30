package me.kisimple.codeviz4j.buuug.v1.i002;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by blues on 4/30/16.
 */
public class GenericMethod<T extends List> {

    public void rockNroll(Map<String, ? extends Map> map1,
                          Map<String, ? extends Map> map2) {
        System.out.println(map1);
        System.out.println(map2);
    }

    public void rockNroll(T t) {
        System.out.println("hello world!");
    }

    public <E> void rockNroll(E e) {
        System.out.println("hello world!");
    }

    public static void main(String[] args) {
        new GenericMethod().rockNroll(null, null);
        new GenericMethod<ArrayList>().rockNroll(new ArrayList());
        new GenericMethod<ArrayList>().<Map>rockNroll(new ArrayList());
        new GenericMethod<ArrayList>().<Map>rockNroll(new HashMap<String, Map>());
    }

}
