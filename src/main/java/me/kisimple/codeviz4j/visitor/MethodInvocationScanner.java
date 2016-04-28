package me.kisimple.codeviz4j.visitor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Position;

import java.nio.charset.Charset;
import java.nio.file.*;
import java.io.*;
import java.util.Stack;

import static java.nio.file.StandardOpenOption.*;

/**
 * Created by blues on 12/22/15.
 */
public class MethodInvocationScanner extends TreeScanner {

    private static String FILE_SEPARATOR = System.getProperty("file.separator");
    private static Path CV4J_HOME = Paths.get(System.getProperty("user.home"), "codeviz4j");

    private static Charset CHARSET = Charset.forName("US-ASCII");

    private Position.LineMap lineMap;

    private Stack<Path> clazzPaths = new Stack<>();
    private Stack<BufferedWriter> staticFiles = new Stack<>();
    private Stack<BufferedWriter> methodFiles = new Stack<>();

    public MethodInvocationScanner(Position.LineMap lineMap) {
        this.lineMap = lineMap;
    }

/****************************************************************************
 * Visitor methods
 ****************************************************************************/

    public void visitClassDef(JCTree.JCClassDecl tree) {
        // rock n roll
        Symbol.ClassSymbol classSymbol = tree.sym;

        if(classSymbol.isInterface()) {
            return;
        }
        if(classSymbol.isAnonymous()) {
            // TODO-blues
            return;
        }
        if(classSymbol.isInner()) {
            // TODO-blues
        }

        Path clazzPath = CV4J_HOME.resolve(
                classSymbol.fullname.toString().replace(".", FILE_SEPARATOR));
        clazzPaths.push(clazzPath);

        try {
            if(Files.notExists(clazzPath)) {
                Files.createDirectories(clazzPath);
            }
            BufferedWriter staticFile = newStaticFile(clazzPath);
            staticFiles.push(staticFile);

            super.visitClassDef(tree);

            staticFile.flush();
            staticFile.close();
            staticFiles.pop();
        } catch (IOException e) {
            e.printStackTrace();
        }

        clazzPaths.pop();
    }

    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        // rock n roll
        Symbol.MethodSymbol methodSymbol = tree.sym;
        try {
            BufferedWriter methodFile =
                    newMethodFile(clazzPaths.peek(), methodSymbol.toString());
            methodFiles.push(methodFile);

            super.visitMethodDef(tree);

            methodFile.flush();
            methodFile.close();
            methodFiles.pop();
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
            if(methodFiles.empty()) {

                if(!staticFiles.empty()) {
                    BufferedWriter staticFile = staticFiles.peek();
                    if(staticFile != null) {
                        try {
                            staticFile.write(methodSignature(symbol));
                            staticFile.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // TODO-blues
                }

            } else {

                BufferedWriter methodFile = methodFiles.peek();
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

    private BufferedWriter newStaticFile(Path clazzPath) throws IOException {
        return newMethodFile(clazzPath, "STATIC");
    }

    private BufferedWriter newMethodFile(Path clazzPath, String methodName)
            throws IOException {
        Path methodPath = clazzPath.resolve(methodName);
        return Files.newBufferedWriter(
                methodPath, CHARSET, CREATE, WRITE, TRUNCATE_EXISTING);
    }

    private String methodSignature(Symbol symbol) {
        if(symbol.isStatic()) {
            // TODO-blues
        }
        return symbol.owner.toString() + "#"
                + symbol.name.toString() + ":"
                + symbol.type.toString();
    }

}
