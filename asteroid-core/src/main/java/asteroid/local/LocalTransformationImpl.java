package asteroid.local;

import static org.codehaus.groovy.ast.ClassHelper.make;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.first;
import static org.codehaus.groovy.runtime.DefaultGroovyMethods.last;

import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.AbstractASTTransformation;

/**
 * This class is an abstraction to process certain nodes annotated with a specific annotation node type
 * <br><br>
 * <b class="note">Types indicate wich nodes are affected:</b>
 * <br><br>
 * Lets say we wanted to build a transformation to transform methods annotated by {@literal @}MyAnnotation
 * <br><br>
 * <pre class="inner"><code>
 * public class MyCustomTransformation extends LocalTransformationImpl&lt;MyAnnotation, MethodNode&gt; {
 *     public abstract void doVisit(AnnotationNode annotation, final MethodNode annotated, final SourceUnit source){
 *         // implementation
 *     }
 * }
 * </code></pre>
 * In this example transformation will be applied only to those {@link ASTNode} instances of type
 * {@link org.codehaus.groovy.ast.MethodNode} annotated by {@literal @}MyAnnotation
 * <br><br>
 * <b class="note">Checks (since 0.1.5):</b>
 * <br><br>
 * If you would like to check something before applying the
 * transformation you can use a contract-like programming
 * structure. If you have worked with <a
 * href="https://github.com/spockframework">Spock</a> or <a
 * href="https://github.com/andresteingress/gcontracts">GContracts</a>
 * you are already used to it. The idea is to have two blocks within
 * {@link LocalTransformationImpl#doVisit} method, one for assertions,
 * the other to call the transformation.
 *
 * <pre class="inner"><code>
 *     public abstract void doVisit(AnnotationNode annotation, final ClassNode annotated, final SourceUnit source){
 *         check: 'class has correct name'
 *         annotated.name == 'MyBusinessService'
 *
 *         then: 'we will add a new method'
 *         // transformation code
 *     }
 * </code></pre>
 * Any expression within the <b>check</b> block will be treated as an
 * assertion statement. If any of the assertion fails the compilation
 * will fail.
 * @param <ANNOTATION> The annotation type used to mark the transformation
 * @param <ANNOTATED> The annotated node type. It has to be a subtype
 * of {@link AnnotatedNode}. As a rule of thumb think of any type that
 * can be annotated (a method, a type...)
 * @since 0.1.0
 *
 */
public abstract class LocalTransformationImpl<ANNOTATION,ANNOTATED extends AnnotatedNode>
    extends AbstractASTTransformation {

    /**
     * Default constructor
     *
     * @since 0.1.0
     */
    public LocalTransformationImpl() throws Exception {
        throw new RuntimeException("This method should never be used. It will be re-created by a local AST transformation");
    }

    private final Class<ANNOTATION> annotation;
    private final Class<ANNOTATED> annotated;

    /**
     * Default constructor
     *
     * @param annotation The type of the annotatino used to trigger the transformation
     * @param annotated The type of node marked to be transformed
     * @since 0.1.0
     */
    public LocalTransformationImpl(final Class<ANNOTATION> annotation, final Class<ANNOTATED> annotated) {
        this.annotation = annotation;
        this.annotated = annotated;
    }

    /**
     * This method processes all annotated nodes annotated with a specific annotation node.
     *
     * @param annotation the annotation information
     * @param annotated the ast node annotated with the specific annotation
     * @param source the current source unit available. It could be needed to for instance add a compilation error.
     * @since 0.1.0
     */
    public abstract void doVisit(AnnotationNode annotation, final ANNOTATED annotated, final SourceUnit source);

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final ASTNode[] nodes, final SourceUnit source) {
        if (nodes == null) return;
        if (nodes.length != 2) return;
        if (!(first(nodes) instanceof AnnotationNode)) return;
        if (!(last(nodes) instanceof AnnotatedNode)) return;

        this.sourceUnit = source;

        AnnotationNode annotationNode = (AnnotationNode) first(nodes);
        ANNOTATED annotatedNode = (ANNOTATED) last(nodes);

        if (!annotationNode.getClassNode().isDerivedFrom(make(annotation))) return;

        doVisit(annotationNode, annotatedNode, source);
    }

}