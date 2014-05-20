package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;
import ro.redeul.google.go.lang.psi.patterns.GoElementPatterns;
import ro.redeul.google.go.lang.psi.resolve.references.CompositeElementOfStructFieldReference;
import ro.redeul.google.go.lang.psi.resolve.references.LabelReference;
import ro.redeul.google.go.lang.psi.resolve.references.ShortVarDeclarationReference;
import ro.redeul.google.go.lang.psi.resolve.references.VarOrConstReference;
import ro.redeul.google.go.lang.psi.toplevel.*;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.or;
import static com.intellij.patterns.StandardPatterns.string;
import static ro.redeul.google.go.lang.lexer.GoTokenTypes.mIDENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.FOR_WITH_CLAUSES_STATEMENT;
import static ro.redeul.google.go.lang.parser.GoElementTypes.FOR_WITH_RANGE_STATEMENT;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.*;
import static ro.redeul.google.go.lang.psi.utils.GoTypeUtils.resolveToFinalType;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Jul 24, 2010
 * Time: 10:43:49 PM
 */
public class GoIdentifierImpl extends GoPsiElementBase implements GoIdentifier {

    private final boolean isIota;
    private Integer iotaValue;

    public GoIdentifierImpl(@NotNull ASTNode node) {
        this(node, false);
    }

    public GoIdentifierImpl(@NotNull ASTNode node, boolean isIota) {
        super(node);

        this.isIota = isIota;
    }

    @Override
    public boolean isBlank() {
        return getText().equals("_");
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitIdentifier(this);
    }

    @Override
    @NotNull
    public String getName() {
        if (isQualified()) {
            return getLocalPackageName() + "." + getUnqualifiedName();
        }

        return getUnqualifiedName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String name)
            throws IncorrectOperationException {
        return null;
    }

    @SuppressWarnings("unchecked")
    private static final ElementPattern<PsiElement> NO_REFERENCE =
            or(
                    psiElement(GoIdentifier.class)
                            .withText(string().matches("nil")),
                    psiElement()
                            .withParent(
                                    or(
                                            psiElement(GoFunctionDeclaration.class),
                                            psiElement(GoFunctionParameter.class),
                                            psiElement(GoMethodReceiver.class),
                                            psiElement(GoTypeStructField.class),
                                            psiElement(GoPsiTypeName.class),
                                            psiElement(GoLiteralExpression.class)
                                                    .withParent(
                                                            or(
                                                                    psiElement(FOR_WITH_CLAUSES_STATEMENT),
                                                                    psiElement(FOR_WITH_RANGE_STATEMENT)
//                                    psiElement(BUILTIN_CALL_EXPRESSION)
                                                            )
                                                    )
                                                    .atStartOf(
                                                            or(
                                                                    psiElement(FOR_WITH_CLAUSES_STATEMENT),
                                                                    psiElement(FOR_WITH_RANGE_STATEMENT)
//                                    psiElement(BUILTIN_CALL_EXPRESSION)
                                                            )
                                                    )
                                    )
                            )
            );



    @NotNull
    @Override
    public PsiReference[] getReferences() {
//        if (references != null)
//            return references;

        if (NO_REFERENCE.accepts(this))
            return refs(PsiReference.EMPTY_ARRAY);

        if (LabelReference.MATCHER.accepts(this))
            return refs(new LabelReference(this));

        if (ShortVarDeclarationReference.MATCHER.accepts(this))
            return refs(new ShortVarDeclarationReference(this));


        return refs(PsiReference.EMPTY_ARRAY);
    }

//    @Override
//    public ItemPresentation getPresentation() {
//        return new ItemPresentation() {
//            public String getPresentableText() {
//                return getName();
//            }
//
//            public TextAttributesKey getTextAttributesKey() {
//                return null;
//            }
//
//            public String getLocationString() {
//                return String.format(" %s (%s)",
//                                     ((GoFile) getContainingFile()).getPackage()
//                                                                   .getPackageName(),
//                                     getContainingFile().getVirtualFile()
//                                         .getPath());
//            }
//
//            public Icon getIcon(boolean open) {
//                return GoIcons.GO_ICON_16x16;
//            }
//        };
//    }

    @Override
    public PsiElement getNameIdentifier() {
        return this;
    }

    @Override
    public boolean isQualified() {
        return findChildByType(GoTokenTypes.oDOT) != null;
    }

    @Override
    @NotNull
    public String getUnqualifiedName() {

        List<PsiElement> tokens = findChildrenByType(mIDENT);
        if (tokens.size() == 2)
            return tokens.get(1).getText();

        return tokens.size() > 0 ? tokens.get(0).getText() : "";
    }

    @Override
    public String getLocalPackageName() {
        if (isQualified()) {
            return findChildrenByType(GoTokenTypes.mIDENT).get(0).getText();
        }

        return null;
    }

    @NotNull
    @Override
    public String getCanonicalName() {
        if (!isQualified())
            return getUnqualifiedName();

        String packageName = getLocalPackageName();
        if (packageName == null) {
            packageName = "";
        }

        PsiFile file = getContainingFile();
        if (file == null || !(file instanceof GoFile)) {
            return getUnqualifiedName();
        }
        GoFile goFile = (GoFile) file;

        GoImportDeclarations[] goImportDeclarations = goFile.getImportDeclarations();
        for (GoImportDeclarations importDeclarations : goImportDeclarations) {
            for (GoImportDeclaration importDeclaration : importDeclarations.getDeclarations()) {
                if (importDeclaration.getVisiblePackageName().toLowerCase()
                        .equals(packageName.toLowerCase())) {
                    GoLiteralString importPath = importDeclaration.getImportPath();
                    if (importPath != null)
                        return String.format("%s:%s",
                                importPath.getValue(),
                                getUnqualifiedName());
                }
            }
        }

        return getName();
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        if (GoElementPatterns.GLOBAL_CONST_DECL.accepts(this) ||
                GoElementPatterns.GLOBAL_VAR_DECL.accepts(this) ||
                GoElementPatterns.FUNCTION_DECLARATION.accepts(this) ||
                GoElementPatterns.METHOD_DECLARATION.accepts(this)) {
            return getGlobalElementSearchScope(this, getName());
        }

        if (isNodeOfType(getParent(), GoElementTypes.LABELED_STATEMENT) ||
                LabelReference.MATCHER.accepts(this)) {
            return new LocalSearchScope(
                    findParentOfType(this, GoFunctionDeclaration.class));
        }

        return getLocalElementSearchScope(this);
    }
}
