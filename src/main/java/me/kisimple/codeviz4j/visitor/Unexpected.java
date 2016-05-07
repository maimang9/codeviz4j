package me.kisimple.codeviz4j.visitor;

import com.sun.tools.javac.code.Symbol;

import static com.sun.tools.javac.code.Symbol.*;

/**
 * User: tongyuan.zbs
 * Date: 2016/5/2
 */
public class Unexpected {

    private static void symbol(Symbol symbol) {
        System.err.println("\"kind\":\""+symbol.kind+"\",");
        System.err.println("\"name\":\""+symbol.name+"\",");
        System.err.println("\"type\":\""+symbol.type+"\",");
        System.err.println("\"owner\":\""+symbol.owner+"\",");
    }

    public static void classSymbol(ClassSymbol classSymbol) {
        System.err.println("##Unexpected ClassSymbol");
        System.err.println("{");

        symbol(classSymbol);

        System.err.println("\"type.className\":\""+classSymbol.type.getClass().getName()+"\",");
        System.err.println("\"owner.className\":\""+classSymbol.owner.getClass().getName()+"\",");
        System.err.println("\"owner.flatName()\":\""+classSymbol.owner.flatName()+"\",");
        System.err.println("\"owner.getQualifiedName()\":\""+classSymbol.owner.getQualifiedName()+"\",");

        System.err.println("\"fullname\":\""+classSymbol.fullname+"\",");
        System.err.println("\"flatname\":\""+classSymbol.flatname+"\"");

        System.err.println("}");
    }

}
