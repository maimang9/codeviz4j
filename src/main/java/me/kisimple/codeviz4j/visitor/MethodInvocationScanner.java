package me.kisimple.codeviz4j.visitor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Position;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.io.*;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by blues on 12/22/15.
 */
public class MethodInvocationScanner extends TreeScanner {

    private static String FILE_SEPARATOR = System.getProperty("file.separator");
    private static Path CV4J_HOME = Paths.get(System.getProperty("user.home"), "codeviz4j");

    private Position.LineMap lineMap;
    private Symbol.ClassSymbol classSymbol;
    private Path clazzPath;
    private BufferedWriter methodFile;
    private BufferedWriter staticFile;

    public MethodInvocationScanner(Position.LineMap lineMap) {
        this.lineMap = lineMap;
    }

/****************************************************************************
 * Visitor methods
 ****************************************************************************/

    public void visitClassDef(JCTree.JCClassDecl tree) {
        // rock n roll
        classSymbol = tree.sym;
        clazzPath = CV4J_HOME.resolve(
                classSymbol.fullname.toString().replace(".", FILE_SEPARATOR));

        try {
            if(Files.notExists(clazzPath)) {
                Files.createDirectories(clazzPath);
            }
            setStaticFile();

            super.visitClassDef(tree);

            staticFile.flush();
            staticFile.close();
            staticFile = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        // rock n roll
        Symbol.MethodSymbol methodSymbol = tree.sym;
        try {
            setMethodFile(methodSymbol.toString());

            super.visitMethodDef(tree);

            methodFile.flush();
            methodFile.close();
            methodFile = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visitApply(JCTree.JCMethodInvocation tree) {
        // rock n roll
        JCTree.JCExpression meth = tree.meth;
        String sourceCode = tree.toString();
        int lineNumber = -1;
        Symbol symbol = null;

        /////// total hack
        if(meth instanceof JCTree.JCIdent) {
            JCTree.JCIdent ident = (JCTree.JCIdent)meth;
            lineNumber = lineMap.getLineNumber(ident.pos);
            symbol = ident.sym;
        } else if(meth instanceof JCTree.JCFieldAccess) {
            JCTree.JCFieldAccess fieldAccess = (JCTree.JCFieldAccess)meth;
            lineNumber = lineMap.getLineNumber(fieldAccess.pos);
            symbol = fieldAccess.sym;
        } else {
            // TODO-blues
        }

        if(symbol != null) {
            if(methodFile == null) {
                try {
                    staticFile.write(methodSignature(symbol));
                    staticFile.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    methodFile.write(methodSignature(symbol));
                    methodFile.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        super.visitApply(tree);
    }

    private void setMethodFile(String methodName) throws IOException {
        Path methodPath = clazzPath.resolve(methodName);
        Charset charset = Charset.forName("US-ASCII");
        methodFile = Files.newBufferedWriter(
                methodPath, charset, CREATE, WRITE, TRUNCATE_EXISTING);
    }

    private void setStaticFile() throws IOException {
        Path methodPath = clazzPath.resolve("STATIC");
        Charset charset = Charset.forName("US-ASCII");
        staticFile = Files.newBufferedWriter(
                methodPath, charset, CREATE, WRITE, TRUNCATE_EXISTING);
    }

    private String methodSignature(Symbol symbol) {
        return symbol.owner.toString() + "#"
                + symbol.name.toString() + ":"
                + symbol.type.toString();
    }

}
