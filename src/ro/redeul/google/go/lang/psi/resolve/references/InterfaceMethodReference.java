package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoSelectorExpression;
import ro.redeul.google.go.lang.psi.impl.types.interfaces.MethodSetDiscover;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.types.interfaces.GoTypeInterfaceMethodSet;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeInterface;
import ro.redeul.google.go.lang.psi.typing.GoTypePointer;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import java.util.ArrayList;
import java.util.List;

public class InterfaceMethodReference extends
        GoPsiReference<GoSelectorExpression, GoIdentifier, InterfaceMethodReference> {

    private final GoTypeInterface type;
    private final GoSelectorExpression selector;

    private static final ResolveCache.AbstractResolver<InterfaceMethodReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<InterfaceMethodReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull InterfaceMethodReference intfMethodRef,
                                               boolean incompleteCode) {
                    GoSelectorExpression selector = intfMethodRef.selector;

                    GoIdentifier identifier = selector.getIdentifier();
                    if (identifier == null)
                        return GoResolveResult.NULL;

                    String name = identifier.getName();
                    if (name == null)
                        return GoResolveResult.NULL;

                    GoTypeInterface type = intfMethodRef.type;

                    GoTypeInterfaceMethodSet methodSet =
                            new MethodSetDiscover(type).getMethodSet();

                    for (GoFunctionDeclaration declaration : methodSet.getMethods()) {
                        if (name.equals(declaration.getFunctionName())) {
                            return GoResolveResult.fromElement(declaration.getNameIdentifier());
                        }
                    }

                    return GoResolveResult.NULL;
                }
            };


    public InterfaceMethodReference(GoSelectorExpression element) {
        super(element, element.getIdentifier(), RESOLVER);

        selector = element;
        type = findTypeInterfaceDeclaration();
    }

    @Override
    public TextRange getRangeInElement() {
        GoIdentifier identifier = getElement().getIdentifier();
        if (identifier == null)
            return null;

        return new TextRange(identifier.getStartOffsetInParent(),
                identifier.getStartOffsetInParent() + identifier.getTextLength());
    }

    @Override
    protected InterfaceMethodReference self() {
        return this;
    }

    private GoTypeInterface findTypeInterfaceDeclaration() {
        GoType type = GoTypeUtils.resolveToFinalType(selector.getBaseExpression().getType()[0]);

        while (type != null && !(type instanceof GoTypeInterface)) {
            if (type instanceof GoTypePointer) {
                type = GoTypeUtils.resolveToFinalType(((GoTypePointer) type).getTargetType());
            } else {
                type = null;
            }
        }

        if (type == null)
            return null;

        return (GoTypeInterface) type;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return false;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        GoIdentifier identifier = getElement().getIdentifier();
        return identifier != null ? identifier.getCanonicalName() : "";
    }

    @NotNull
    @Override
    public Object[] getVariants() {

        GoTypeInterfaceMethodSet methodSet =
                new MethodSetDiscover(type).getMethodSet();

        List<LookupElementBuilder> variants = new ArrayList<LookupElementBuilder>();
        for (GoFunctionDeclaration functionDeclaration : methodSet.getMethods()) {
            variants.add(functionDeclaration.getCompletionPresentation());
        }

        return variants.toArray();
    }

    @Override
    public boolean isSoft() {
        return false;
    }
}
