package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.lexer.GoTokenTypes;
import ro.redeul.google.go.lang.psi.GoPackageReference;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.resolve.references.BuiltinTypeNameReference;
import ro.redeul.google.go.lang.psi.resolve.references.TypeNameReference;
import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeBuiltin;
import ro.redeul.google.go.lang.psi.typing.GoTypeNamed;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.regex.Pattern;

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

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
            Pattern.compile("" +
                    "bool|error|byte|rune|uintptr|string|char|" +
                    "(int|uint)(8|16|32|64)?|" +
                    "float(32|64)|" +
                    "complex(64|128)");
    private static final ElementPattern<PsiElement> PRIMITIVE_TYPES =
            psiElement()
                    .withText(string().matches(PRIMITIVE_TYPES_PATTERN.pattern()));
    private static final ElementPattern<PsiElement> NIL_TYPE =
            psiElement()
                    .withText(string().matches("nil"));

    public GoPsiTypeNameImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @NotNull
    public String getName() {
        GoIdentifier identifier =
                findChildByClass(GoIdentifier.class);

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

    @Nullable
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

    @Override
    public GoType resolveType() {

        if (PRIMITIVE_TYPES.accepts(this)) {
            return GoTypeBuiltin.getForName(getText());
        }

        return new GoTypeNamed(this);
    }

    @NotNull
    @Override
    public GoIdentifier getIdentifier() {
        return findChildByClass(GoIdentifier.class);
    }

    @Override
    public boolean isReference() {
        return findChildByType(GoTokenTypes.oMUL) != null;
    }

    @Override
    public boolean isPrimitive() {
        return PRIMITIVE_TYPES_PATTERN.matcher(getText()).matches();
    }

    @Nullable
    @Override
    public GoPsiType getDefinition() {
        GoType primitive = GoTypeBuiltin.getForName(getText());
        if (primitive != null) {
            return primitive.getPsiType();
        }

        PsiReference reference = getReference();
        if (reference == null) {
            return null;
        }

        PsiElement resolved = reference.resolve();
        if (resolved == null) {
            return null;
        }

        if (resolved instanceof GoTypeSpec) {
            GoTypeSpec spec = (GoTypeSpec) resolved;
            return spec.getType();
        }

        return null;
    }

    @Override
    public String getPresentationTailText() {
        return getText();
    }
}
