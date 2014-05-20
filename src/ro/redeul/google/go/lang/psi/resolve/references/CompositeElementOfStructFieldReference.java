package ro.redeul.google.go.lang.psi.resolve.references;

import com.intellij.patterns.ElementPattern;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.parser.GoElementTypes;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.composite.GoLiteralCompositeElement;
import ro.redeul.google.go.lang.psi.expressions.primary.GoIdentifierExpression;
import ro.redeul.google.go.lang.psi.resolve.GoResolveResult;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class CompositeElementOfStructFieldReference
        extends AbstractStructFieldsReference<GoIdentifierExpression, CompositeElementOfStructFieldReference> {

    public static final ElementPattern<GoIdentifierExpression> MATCHER_KEY =
            psiElement(GoIdentifierExpression.class)
                    .withParent(
                            psiElement(GoElementTypes.COMPOSITE_LITERAL_ELEMENT_KEY)
                                    .withParent(
                                            psiElement(GoLiteralCompositeElement.class))
                    );

    public static final ElementPattern<GoIdentifierExpression> MATCHER_ELEMENT =
            psiElement(GoIdentifierExpression.class)
                    .withParent(
                            psiElement(
                                    GoLiteralCompositeElement.class)
                    );
    private static final ResolveCache.AbstractResolver<CompositeElementOfStructFieldReference, GoResolveResult> RESOLVER =
            new ResolveCache.AbstractResolver<CompositeElementOfStructFieldReference, GoResolveResult>() {
                @Override
                public GoResolveResult resolve(@NotNull CompositeElementOfStructFieldReference psiReference, boolean incompleteCode) {

                    GoTypeStruct typeStruct = psiReference.resolveTypeDefinition();

                    if (typeStruct == null)
                        return GoResolveResult.NULL;

                    GoPsiTypeStruct psiTypeStruct = (GoPsiTypeStruct) typeStruct.getPsiType();
                    if (psiTypeStruct == null)
                        return GoResolveResult.NULL;

                    GoIdentifier element = psiReference.getReferenceElement();


                    for (GoTypeStructField field : psiTypeStruct.getFields()) {
                        for (GoIdentifier identifier : field.getIdentifiers()) {
                            if (identifier.getUnqualifiedName().equals(element.getUnqualifiedName()))
                                return GoResolveResult.fromElement(identifier);
                        }
                    }

                    for (GoTypeStructAnonymousField field : psiTypeStruct.getAnonymousFields()) {
                        if (field.getFieldName().equals(element.getUnqualifiedName()))
                            return GoResolveResult.fromElement(field);
                    }

                    return GoResolveResult.NULL;
                }
            };

    public CompositeElementOfStructFieldReference(GoIdentifierExpression element) {
        super(element, element.getIdentifier(), RESOLVER);
    }

    @Override
    protected CompositeElementOfStructFieldReference self() {
        return this;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return getReferenceElement().getCanonicalName();
    }

    @Override
    protected GoTypeStruct resolveTypeDefinition() {
        GoPsiElement parent = getElement();
        while (parent != null && !(parent instanceof GoLiteralCompositeElement)) {
            parent = (GoPsiElement) parent.getParent();
        }

        if (parent == null)
            return null;

        GoType type = ((GoLiteralCompositeElement) parent).getElementType();

        if (type == null)
            return null;

        if (type instanceof GoTypeStruct)
            return (GoTypeStruct) type;

        return null;
    }
}
