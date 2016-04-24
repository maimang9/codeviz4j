package me.kisimple.codeviz4j.visitor;

import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;
import com.sun.tools.javac.util.Position;

import java.util.Stack;

/**
 * Created by blues on 12/22/15.
 */
public class MethodInvocationScanner extends TreeScanner {

    private Position.LineMap lineMap;
    private Symbol.ClassSymbol classSymbol;
    private Stack<Symbol.MethodSymbol> methods = new Stack<>();

    public MethodInvocationScanner(Position.LineMap lineMap) {
        this.lineMap = lineMap;
    }

/****************************************************************************
 * Visitor methods
 ****************************************************************************/

    public void visitClassDef(JCTree.JCClassDecl tree) {
        // rock n roll
        classSymbol = tree.sym;

        super.visitClassDef(tree);
    }

    public void visitMethodDef(JCTree.JCMethodDecl tree) {
        // rock n roll
        methods.push(tree.sym);

        super.visitMethodDef(tree);

        // rock n roll
        methods.pop();
    }

    public void visitApply(JCTree.JCMethodInvocation tree) {
        // rock n roll
        String sourceCode = tree.toString();
        JCTree.JCExpression meth = tree.meth;
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
            if(methods.empty()) {
                //
            } else {
                //
            }
        }

        super.visitApply(tree);
    }

}
