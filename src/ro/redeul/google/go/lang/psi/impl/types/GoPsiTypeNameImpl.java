package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinTypeNameReference;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameReference;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoTypes;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.string;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 30, 2010
 * Time: 7:12:16 PM
 */
public class GoPsiTypeNameImpl extends GoPsiPackagedElementBase
        implements GoPsiTypeName {

    public GoPsiTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getName() {
        GoLiteralIdentifier identifier =
                findChildByClass(GoLiteralIdentifier.class);

        return identifier != null ? identifier.getUnqualifiedName() : getText();
    }

    @Override
    public PsiElement setName(@NotNull @NonNls String name)
            throws IncorrectOperationException {
        return null;
    }

    public GoPackageReference getPackageReference() {
        return findChildByClass(GoPackageReference.class);
    }

    private static final ElementPattern<PsiElement> PRIMITIVE_TYPES =
            psiElement()
                    .withText(
                            string().matches(GoTypes.PRIMITIVE_TYPES_PATTERN.pattern()));

    private static final ElementPattern<PsiElement> NIL_TYPE =
            psiElement()
                    .withText(string().matches("nil"));

    @Override
    public PsiReference getReference() {

        if (PRIMITIVE_TYPES.accepts(this))
            return new BuiltinTypeNameReference(this);

        if (NIL_TYPE.accepts(this))
            return null;

        return new TypeNameReference(this);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitTypeName(this);
    }

    @NotNull
    @Override
    public GoLiteralIdentifier getIdentifier() {
        return findChildByClass(GoLiteralIdentifier.class);
    }

    @Override
    public boolean isReference() {
        return findChildByType(GoTokenTypes.oMUL) != null;
    }

    @Override
    public boolean isPrimitive() {
        return GoTypes.PRIMITIVE_TYPES_PATTERN.matcher(getText()).matches();
    }

    @Override
    public String getPresentationTailText() {
        return getText();
    }
}
