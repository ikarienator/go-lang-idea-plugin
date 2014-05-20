package ro.redeul.google.go.lang.psi.impl.expressions.literals.composite;

import com.intellij.lang.ASTNode;
import com.intellij.patterns.ElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralComposite;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeValue;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.impl.GoPsiElementBase;

import ro.redeul.google.go.lang.psi.toplevel.GoTypeSpec;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeName;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static ro.redeul.google.go.lang.parser.GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveTypeSpec;

public class GoLiteralCompositeElementImpl extends GoPsiElementBase
        implements GoLiteralCompositeElement {
    private static final
    ElementPattern patternCompositeParent =
            psiElement(GoLiteralCompositeElement.class)
                    .withParent(
                            psiElement(GoLiteralCompositeValue.class)
                                    .withParent(
                                            psiElement(GoLiteralComposite.class))
                    );
    private static final
    ElementPattern patternElementParent =
            psiElement(GoLiteralCompositeElement.class)
                    .withParent(
                            psiElement(GoLiteralCompositeValue.class)
                                    .withParent(
                                            psiElement(GoLiteralCompositeElement.class))
                    );

    public GoLiteralCompositeElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public GoIdentifier getKey() {
        GoExpr keyExpression = getIndex();

        if (keyExpression == null)
            return null;

        if (keyExpression instanceof GoLiteralExpression) {
            GoLiteralExpression expression = (GoLiteralExpression) keyExpression;

            if (expression.getLiteral() instanceof GoIdentifier) {
                return (GoIdentifier) expression.getLiteral();
            }
        }

        return null;
    }

    @Override
    public GoExpr getIndex() {
        PsiElement keyNode = findChildByType(COMPOSITE_LITERAL_ELEMENT_KEY);

        if (keyNode == null) {
            return null;
        }

        return GoPsiUtils.findChildOfClass(keyNode, GoExpr.class);
    }

    @Override
    public GoExpr getExpressionValue() {
        return findChildByClass(GoExpr.class);
    }

    @Override
    public GoLiteralCompositeValue getLiteralValue() {
        return findChildByClass(GoLiteralCompositeValue.class);
    }

    @Override
    public GoType getElementType() {

        GoType parentType = null;
        if (patternCompositeParent.accepts(this)) {
            GoLiteralComposite literalComposite =
                    (GoLiteralComposite) getParent().getParent();

            parentType = GoTypes.fromPsiType(literalComposite.getLiteralType());
        }

        if (patternElementParent.accepts(this)) {
            GoLiteralCompositeElement compositeElement =
                    (GoLiteralCompositeElement) getParent().getParent();

            if (compositeElement.getKey() != null) {
                GoIdentifier identifier =
                        resolveSafely(
                                compositeElement.getKey(),
                                psiElement(GoIdentifier.class)
                                        .withParent(psiElement(GoTypeStructField.class)),
                                GoIdentifier.class
                        );
                if (identifier != null) {
                    parentType = GoTypes.fromPsiType(
                            ((GoTypeStructField) identifier.getParent()).getType());
                }
            } else {
                parentType = compositeElement.getElementType();
            }
        }

        if (parentType == null) {
            return null;
        }

        while (parentType != null) {
            if (!(parentType.getPsiType() instanceof GoPsiTypeName)) {
                break;
            }

            GoTypeSpec typeSpec = resolveTypeSpec((GoPsiTypeName) parentType.getPsiType());

            if (typeSpec != null)
                parentType = GoTypes.fromPsiType(typeSpec.getType());

            if (typeSpec == null)
                parentType = null;

        }

        if (parentType instanceof GoTypeArray) {
            return ((GoTypeArray) parentType).getElementType();
        }

        if (parentType instanceof GoTypeSlice) {
            return ((GoTypeSlice) parentType).getElementType();
        }

        if (parentType instanceof GoTypeMap) {
            return ((GoTypeMap) parentType).getValueType();
        }

        return parentType;
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitLiteralCompositeElement(this);
    }


    @Override
    public PsiReference getReference() {
        return null;
    }
}
