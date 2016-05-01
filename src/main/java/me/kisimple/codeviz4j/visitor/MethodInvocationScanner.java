package me.kisimple.codeviz4j.visitor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Position;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Stack;

import static com.sun.tools.javac.code.Symbol.*;
import static java.nio.file.StandardOpenOption.*;
import static me.kisimple.codeviz4j.visitor.ToString.*;

/**
 * Created by blues on 12/22/15.
 */
public class MethodInvocationScanner extends TreeScanner {

    private static String FILE_SEPARATOR = System.getProperty("file.separator");
    private static Path CV4J_HOME = Paths.get(System.getProperty("user.home"), "codeviz4j");

    private static Charset CHARSET = Charset.forName("US-ASCII");

    static {
        init();
    }

    private Stack<Path> clazzPaths = new Stack<>();
    private Stack<BufferedWriter> staticFiles = new Stack<>();
    private Stack<BufferedWriter> methodFiles = new Stack<>();

    private Position.LineMap lineMap;

    public MethodInvocationScanner(Position.LineMap lineMap) {
        this.lineMap = lineMap;
    }

/****************************************************************************
 * Visitor methods
 ****************************************************************************/

    public void visitClassDef(JCTree.JCClassDecl tree) {
        try {
            // rock n roll
            ClassSymbol classSymbol = tree.sym;

            if(classSymbol.isInterface()) {
                return;
            }

            String className = className(classSymbol);
            Path clazzPath = CV4J_HOME.resolve(className.replace(".", FILE_SEPARATOR));
            clazzPaths.push(clazzPath);

            if(Files.notExists(clazzPath)) {
                Files.createDirectories(clazzPath);
            } else {
                cleanDirectories(clazzPath.toFile());
            }

            BufferedWriter staticFile = newStaticFile(clazzPath);
            staticFiles.push(staticFile);

            super.visitClassDef(tree);

            staticFile.flush();
            staticFile.close();
            staticFiles.pop();

            clazzPaths.pop();
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println(tree);
        }
    }

    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        try {
            // rock n roll
            MethodSymbol methodSymbol = tree.sym;

            BufferedWriter methodFile =
                    newMethodFile(clazzPaths.peek(), methodName(methodSymbol));
            methodFiles.push(methodFile);

            super.visitMethodDef(tree);

            methodFile.flush();
            methodFile.close();
            methodFiles.pop();
        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println(tree);
        }
    }

    public void visitApply(JCTree.JCMethodInvocation tree) {
        try {
            // rock n roll
            JCTree.JCExpression meth = tree.meth;
            int lineNumber;
            Symbol symbol;

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
                throw new Throwable(meth.getClass().getName() + "#" + meth.toString());
            }

            if(symbol != null) {
                if(methodFiles.empty()) {

                    if(!staticFiles.empty()) {
                        BufferedWriter staticFile = staticFiles.peek();
                        staticFile.write(methodHtml(symbol, lineNumber));
                        staticFile.newLine();
                    } else {
                        throw new Throwable("staticFiles.empty()");
                    }

                } else {

                    BufferedWriter methodFile = methodFiles.peek();
                    methodFile.write(methodHtml(symbol, lineNumber));
                    methodFile.newLine();

                }
            }

            super.visitApply(tree);

        } catch (Throwable t) {
            t.printStackTrace();
            System.err.println(tree);
        }
    }



    private static void init() {
        try {
            if(Files.notExists(CV4J_HOME)) {
                Files.createDirectories(CV4J_HOME);
            }
            Path errorLog = CV4J_HOME.resolve("error.log");
            OutputStream outputStream = Files.newOutputStream(errorLog);
            System.setErr(new PrintStream(outputStream));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void cleanDirectories(File dir) {
        for (File file : dir.listFiles()) {
            if(file.isDirectory()) {
                cleanDirectories(file);
            }
            file.delete();
        }
    }

    private BufferedWriter newStaticFile(Path clazzPath) throws IOException {
        return newMethodFile(clazzPath, "__static");
    }

    private BufferedWriter newMethodFile(Path clazzPath, String methodName)
            throws IOException {
        Path methodPath = clazzPath.resolve(methodName+".html");
        return Files.newBufferedWriter(
                methodPath, CHARSET, CREATE, WRITE, TRUNCATE_EXISTING);
    }

    private String methodHtml(Symbol symbol, int lineNumber) throws Throwable {
        return String.format("%s: <a href=\"%s\" target=_blank>%s</a><br/>",
                lineNumber, methodLink(symbol), simpleName(symbol));
    }

    private String methodLink(Symbol symbol) throws Throwable {
        if(symbol instanceof MethodSymbol) {
            return String.format("file:///%s/%s.html",
                    CV4J_HOME.resolve(className(symbol.owner).replace(".", FILE_SEPARATOR)),
                    methodName(symbol));
        }
        throw new Throwable(symbol.getClass().getName() + "#" + symbol.toString());
    }

    private String simpleName(Symbol symbol) {
        return className(symbol.owner) + "#" + symbol.name;
    }

}
