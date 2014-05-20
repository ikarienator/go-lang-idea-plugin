package ro.redeul.google.go.lang.psi.impl.expressions.primary;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.GoPrimaryExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.expressions.GoExpressionBase;
import ro.redeul.google.go.lang.psi.resolve.references.InterfaceMethodReference;
import ro.redeul.google.go.lang.psi.resolve.references.MethodReference;
import ro.redeul.google.go.lang.psi.resolve.references.SelectorOfStructFieldReference;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoIdentifierUtils;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;
import ro.redeul.google.go.services.GoPsiManager;

import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.resolveSafely;

public class GoSelectorExpressionImpl extends GoExpressionBase implements GoSelectorExpression {

    public GoSelectorExpressionImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(GoElementVisitor visitor) {
        visitor.visitSelectorExpression(this);
    }

    @Override
    protected GoType[] resolveTypes() {
        return GoPsiManager.getInstance(getProject()).getType(
                this,
                new Function<GoSelectorExpression, GoType[]>() {
                    @Override
                    public GoType[] fun(GoSelectorExpression expression) {
                        PsiElement target =
                                resolveSafely(GoSelectorExpressionImpl.this,
                                        PsiElement.class);

                        if (target != null &&
                                target.getParent() instanceof GoTypeStructField) {

                            GoTypeStructField structField =
                                    (GoTypeStructField) target.getParent();

                            return new GoType[]{
                                    GoTypes.fromPsiType(structField.getType())
                            };
                        }

                        if (target instanceof GoTypeStructAnonymousField) {
                            GoTypeStructAnonymousField structField =
                                    (GoTypeStructAnonymousField) target;

                            return new GoType[]{
                                    GoTypes.fromPsiType(structField.getType())
                            };
                        }

                        if (target instanceof GoIdentifier) {
                            GoFunctionDeclaration functionDeclaration = GoIdentifierUtils.getFunctionDeclaration(target);
                            if (functionDeclaration != null) {
                                return new GoType[]{
                                        GoTypes.fromPsiType(functionDeclaration)
                                };
                            }
                        }

                        return GoType.EMPTY_ARRAY;
                    }
                }
        );
    }

    @Override
    public GoPrimaryExpression getBaseExpression() {
        return findChildByClass(GoPrimaryExpression.class);
    }

    @Override
    @Nullable
    public GoIdentifier getIdentifier() {
        return findChildByClass(GoIdentifier.class);
    }

    private Object[] convertToPresentation(GoPsiType type, GoPsiElement[] members) {

        Object[] presentations = new Object[members.length];

        for (int i = 0, numMembers = members.length; i < numMembers; i++) {
            GoPsiElement member = members[i];

            if (member instanceof GoIdentifier) {
                LookupElementBuilder presentation =
                        getFieldPresentation(type, (GoIdentifier) member);

                if (presentation != null)
                    presentations[i] = presentation;

            } else {
                presentations[i] = member;
            }
        }

        return presentations;
    }

    @Nullable
    private LookupElementBuilder getFieldPresentation(GoPsiType type, GoIdentifier id) {

        String name = id.getName();
        if (name == null)
            return null;

        LookupElementBuilder builder = LookupElementBuilder.create(id, name);

        GoPsiType ownerType = null;
        if (id.getParent() != null && id.getParent() instanceof GoTypeStructField) {
            GoTypeStructField structField = (GoTypeStructField) id.getParent();
            ownerType = (GoPsiType) structField.getParent();
        }

        if (ownerType == null) {
            return builder;
        }

        return builder
                .bold()
                .withTailText(String.format(" (defined by: %s)",
                        ownerType.getQualifiedName()))
                .withTypeText("<field>", ownerType != type);
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        GoPrimaryExpression baseExpression = getBaseExpression();

        if (baseExpression == null) {
            return PsiReference.EMPTY_ARRAY;
        }

        GoType[] baseTypes = baseExpression.getType();
        if (baseTypes.length == 0) {
            return PsiReference.EMPTY_ARRAY;
        }

        GoType type = baseTypes[0];

        if (type instanceof GoTypePointer) {
            type = ((GoTypePointer) type).getTargetType();
        }

        type = GoTypeUtils.resolveToFinalType(type);

        if (type instanceof GoTypeInterface)
            return new PsiReference[]{new InterfaceMethodReference(this)};

        if (type instanceof GoTypeStruct && getIdentifier() != null)
            return new PsiReference[]{
                    new SelectorOfStructFieldReference(this),
                    new MethodReference(this)
            };

        if (type instanceof GoTypePointer) {
            return new PsiReference[]{
                    new SelectorOfStructFieldReference(this),
                    new MethodReference(this)
            };
        }

        if (type instanceof GoTypeNamed) {
            return new PsiReference[]{
                    new MethodReference(this)
            };
        }

        return super.getReferences();
    }

    @Override
    public boolean isConstantExpression() {
        return false;
    }
}

