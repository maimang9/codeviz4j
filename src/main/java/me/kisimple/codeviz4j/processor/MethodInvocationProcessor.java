package me.kisimple.codeviz4j.processor;

import com.sun.source.tree.Tree;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.comp.*;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;

import me.kisimple.codeviz4j.visitor.MethodInvocationScanner;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Created by blues on 12/22/15.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class MethodInvocationProcessor extends AbstractProcessor {

    private Trees trees;
    private Context context = null;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        trees = Trees.instance(env);

        // rabbit hole
        if(env instanceof JavacProcessingEnvironment) {
            context = ((JavacProcessingEnvironment)env).getContext();
        }

    }

    @Override
    public boolean process(Set<? extends TypeElement> set,
                           RoundEnvironment roundEnvironment) {
        if(!roundEnvironment.processingOver()) {
            if(context != null) {

                ///////// total hack
                Todo todo = Todo.instance(context);
                if(todo.isEmpty()) { // -proc:only
                    Set<? extends Element> rootElements = roundEnvironment.getRootElements();
                    for (Element rootElement : rootElements) {
                        if(rootElement.getKind() == ElementKind.CLASS) {

                            Tree tree = trees.getTree(rootElement);
                            if(tree != null && tree instanceof JCTree.JCClassDecl) {
                                JCTree.JCClassDecl jcTree = (JCTree.JCClassDecl)tree;
                                Symbol.TypeSymbol sym = jcTree.sym;
                                Enter enter = Enter.instance(context);
                                Attr attr = Attr.instance(context);
                                Env<AttrContext> env = enter.getEnv(sym);
                                if(env != null && env.toplevel != null) {
                                    attr.attrib(env);
                                    MethodInvocationScanner scanner =
                                            new MethodInvocationScanner(env.toplevel.lineMap);
                                    scanner.scan(jcTree);
                                }
                            }

                        }
                    }
                }

            }
        }
        return false;
    }

}
