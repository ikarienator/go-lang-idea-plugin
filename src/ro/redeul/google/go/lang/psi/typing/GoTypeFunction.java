package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;

import java.util.ArrayList;

public class GoTypeFunction
        extends GoTypePsiBacked<GoPsiTypeFunction>
        implements GoType {

    @NotNull
    private final GoType[] types;

    private final int parametersLength;
    private final boolean variadic;

    GoTypeFunction(GoPsiTypeFunction psiType) {
        super(psiType);
        GoFunctionParameter[] parameters = psiType.getParameters();
        GoFunctionParameter[] results = psiType.getResults();
        int i = 0;
        ArrayList<GoType> types = new ArrayList<GoType>();
        boolean variadic = false;
        for (GoFunctionParameter param : parameters) {
            if (param.isVariadic()) {
                variadic = true;
            }
            GoLiteralIdentifier[] identifiers = param.getIdentifiers();
            if (identifiers.length == 0) {
                types.add(GoTypes.fromPsiType(param.getType()));
                i++;
            } else {
                GoType type = GoTypes.fromPsiType(param.getType());
                for (GoLiteralIdentifier ident : identifiers) {
                    types.add(type);
                    i++;
                }
            }
        }
        parametersLength = i;
        this.variadic = variadic;
        for (GoFunctionParameter param : results) {
            GoLiteralIdentifier[] identifiers = param.getIdentifiers();
            if (identifiers.length == 0) {
                types.add(GoTypes.fromPsiType(param.getType()));
                i++;
            } else {
                GoType type = GoTypes.fromPsiType(param.getType());
                for (GoLiteralIdentifier ident : identifiers) {
                    types.add(type);
                    i++;
                }
            }
        }
        this.types = types.toArray(new GoType[types.size()]);
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        if (type == this) {
            return true;
        }
        if (type instanceof GoTypeFunction) {
            GoTypeFunction funcType = (GoTypeFunction) type;
            if (this.variadic != funcType.variadic) return false;
            if (this.parametersLength != funcType.parametersLength) return false;
            if (this.types.length != funcType.types.length) return false;
            for (int i = 0; i < this.types.length; i++) {
                if (!funcType.types[i].isIdentical(this.types[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeFunction(this);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("func(");

        int counter = 0;

        for (int i = 0; i < parametersLength; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            if (variadic && i == parametersLength - 1) {
                stringBuilder.append("...");
            }
            stringBuilder.append(types[i].getNameLocalOrGlobal(currentFile));
        }
        stringBuilder.append(')');

        if (parametersLength < types.length - 1)
            stringBuilder.append('(');

        for (int i = 0; i < types.length - parametersLength; i++) {
            if (i > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(types[i + parametersLength].getNameLocalOrGlobal(currentFile));
        }


        if (parametersLength < types.length - 1)
            stringBuilder.append(')');

        return stringBuilder.toString();
    }
}
