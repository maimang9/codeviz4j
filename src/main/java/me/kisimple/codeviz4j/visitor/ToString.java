package me.kisimple.codeviz4j.visitor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Log;

import static com.sun.tools.javac.code.Flags.*;
import static com.sun.tools.javac.code.Kinds.*;
import static com.sun.tools.javac.code.Symbol.*;
import static com.sun.tools.javac.code.TypeTags.*;

/**
 * Created by blues on 4/30/16.
 */
public class ToString {

    public static boolean ROCK = false;

    public static String className(Symbol symbol) {
        if(symbol instanceof ClassSymbol) {
            ClassSymbol classSymbol = (ClassSymbol) symbol;
            return classSymbol.isAnonymous()||classSymbol.isLocal() ?
                    classSymbol.flatname.toString() : classSymbol.fullname.toString();
        }
        return symbol.toString();
    }

    public static String methodName(Symbol symbol) {
        if(symbol instanceof MethodSymbol) {
            MethodSymbol methodSymbol = (MethodSymbol)symbol;
            if(ROCK) return methodSymbol.toString();

            if ((methodSymbol.flags() & BLOCK) != 0) {
                return methodSymbol.owner.name.toString();
            } else {
                String s = (methodSymbol.name == methodSymbol.name.table.names.init)
                        ? methodSymbol.owner.name.toString()
                        : methodSymbol.name.toString();
                if (methodSymbol.type != null) {
//                if (methodSymbol.type.tag == FORALL)
//                    s = "<" + ((Type.ForAll)methodSymbol.type).getTypeArguments() + ">" + s;
                    s += "(" + argTypes(methodSymbol.type, (methodSymbol.flags() & VARARGS) != 0) + ")";
                }
                return s;
            }
        }
        return symbol.toString();
    }



    private static String argTypes(Type type, boolean varargs) {
        List<Type> args = type.getParameterTypes();
        if (!varargs) return toString(args);
        StringBuilder buf = new StringBuilder();
        while (args.tail.nonEmpty()) {
            buf.append(args.head);
            args = args.tail;
            buf.append(',');
        }
        if (args.head.tag == ARRAY) {
            buf.append(((Type.ArrayType)args.head).elemtype);
            buf.append("...");
        } else {
            buf.append(args.head);
        }
        return buf.toString();
    }

    private static String toString(List<Type> typeList) {
        if(typeList == null || typeList.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (Type type : typeList) {
            sb.append(toString(type)).append(",");
        }
        return sb.toString().substring(0, sb.toString().length()-1);
    }

    private static String toString(Type type) {
        if(type instanceof Type.ClassType) {

            Type.ClassType classType = (Type.ClassType)type;
            StringBuilder buf = new StringBuilder();
            if (classType.getEnclosingType().tag == CLASS && classType.tsym.owner.kind == TYP) {
                buf.append(classType.getEnclosingType().toString());
                buf.append(".");
                buf.append(className(classType, classType.tsym, false));
            } else {
                buf.append(className(classType, classType.tsym, true));
            }
//            if (classType.getTypeArguments().nonEmpty()) {
//                buf.append('<');
//                buf.append(classType.getTypeArguments().toString());
//                buf.append(">");
//            }
            return buf.toString();

        }
        return type.toString();
    }

    private static String className(Type.ClassType type, Symbol sym, boolean longform) {
        if (sym.name.isEmpty() && (sym.flags() & COMPOUND) != 0) {
            StringBuilder s = new StringBuilder(type.supertype_field.toString());
            for (List<Type> is=type.interfaces_field; is.nonEmpty(); is = is.tail) {
                s.append("&");
                s.append(is.head.toString());
            }
            return s.toString();
        } else if (sym.name.isEmpty()) {
            String s;
            Type.ClassType norm = (Type.ClassType) type.tsym.type;
            if (norm == null) {
                s = Log.getLocalizedString("anonymous.class", (Object)null);
            } else if (norm.interfaces_field != null && norm.interfaces_field.nonEmpty()) {
                s = Log.getLocalizedString("anonymous.class",
                        norm.interfaces_field.head);
            } else {
                s = Log.getLocalizedString("anonymous.class",
                        norm.supertype_field);
            }
            if (Type.moreInfo)
                s += String.valueOf(sym.hashCode());
            return s;
        } else if (longform) {
            return sym.getQualifiedName().toString();
        } else {
            return sym.name.toString();
        }
    }

}
