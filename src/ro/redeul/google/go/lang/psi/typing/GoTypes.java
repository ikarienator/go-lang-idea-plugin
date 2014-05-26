package ro.redeul.google.go.lang.psi.typing;

import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoMethodDeclaration;
import ro.redeul.google.go.lang.psi.types.*;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitorWithData;
import ro.redeul.google.go.services.GoPsiManager;

import java.util.regex.Pattern;

public class GoTypes {

    public static final Pattern PRIMITIVE_TYPES_PATTERN =
            Pattern.compile("" +
                    "bool|error|byte|rune|uintptr|string|char|" +
                    "(int|uint)(8|16|32|64)?|" +
                    "float(32|64)|" +
                    "complex(64|128)");

    public static <T extends GoType> T resolveTo(GoType type, Class<T> targetType) {
        while (type != null && type != GoType.Unknown && !targetType.isAssignableFrom(type.getClass())) {
            if (type instanceof GoTypeName) {
                type = ((GoTypeName) type).getDefinition();
            } else {
                type = GoType.Unknown;
            }
        }

        return targetType.cast(type);
    }

    public static GoTypeInterface resolveToInterface(GoPsiTypeName typeName) {
        return resolveTo(fromPsiType(typeName), GoTypeInterface.class);
    }

    public static GoType makePointer(GoPsiType argumentType) {
        return new GoTypePointer(fromPsiType(argumentType));
    }

    @NotNull
    public static GoType fromPsiType(final GoPsiType psiType) {
        if (psiType == null)
            return GoType.Unknown;

        return GoPsiManager.getInstance(psiType.getProject()).getType(psiType, new Function<GoPsiElement, GoType[]>() {
            @Override
            public GoType[] fun(GoPsiElement goPsiElement) {
                return new GoType[]{
                        psiType.accept(new GoTypeMakerVisitor())
                };
            }
        })[0];
    }

    @NotNull
    public static GoType[] fromPsiType(GoPsiType[] psiTypes) {
        GoType types[] = new GoType[psiTypes.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = fromPsiType(psiTypes[i]);
        }

        return types;
    }

    private static class GoTypeMakerVisitor extends GoElementVisitorWithData<GoType> {
        private GoTypeMakerVisitor() {
            data = GoType.Unknown;
        }

        @Override
        public void visitChannelType(GoPsiTypeChannel psiType) {
            data = new GoTypeChannel(psiType);
        }

        @Override
        public void visitArrayType(GoPsiTypeArray psiType) {
            data = new GoTypeArray(psiType);
        }

        @Override
        public void visitTypeName(GoPsiTypeName psiType) {
            if (psiType.isPrimitive()) {
                data = GoTypeBuiltin.fromName(psiType.getIdentifier().getCanonicalName());
            } else {
                data = new GoTypeName(psiType);
            }
        }

        @Override
        public void visitMapType(GoPsiTypeMap type) {
            data = new GoTypeMap(type);
        }

        @Override
        public void visitSliceType(GoPsiTypeSlice psiType) {
            data = new GoTypeSlice(GoTypes.fromPsiType(psiType.getElementType()));
        }

        @Override
        public void visitPointerType(GoPsiTypePointer psiType) {
            data = new GoTypePointer(psiType);
        }

        @Override
        public void visitStructType(GoPsiTypeStruct psiType) {
            data = new GoTypeStruct(psiType);
        }

        @Override
        public void visitFunctionType(GoPsiTypeFunction type) {
            data = new GoTypeFunction(type);
        }

        @Override
        public void visitInterfaceType(GoPsiTypeInterface type) {
            data = new GoTypeInterface(type);
        }

        @Override
        public void visitFunctionDeclaration(GoFunctionDeclaration declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitMethodDeclaration(GoMethodDeclaration declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitFunctionLiteral(GoLiteralFunction declaration) {
            visitFunctionType(declaration);
        }

        @Override
        public void visitTypeParenthesized(GoPsiTypeParenthesized parenthesized) {
            parenthesized.getInnerType().accept(this);
        }
    }

    public static GoTypeStruct resolveToStruct(GoPsiType type) {
        return resolveToStruct(fromPsiType(type));
    }

    public static GoTypeStruct resolveToStruct(GoType type) {
        while (type != null && !(type instanceof GoTypeStruct)) {
            if (type instanceof GoTypePointer) {
                type = ((GoTypePointer) type).getTargetType();
            } else if (type instanceof GoTypeName) {
                type = ((GoTypeName) type).getDefinition();
            } else {
                type = null;
            }
        }

        if (type == null)
            return null;

        return (GoTypeStruct) type;
    }
}
