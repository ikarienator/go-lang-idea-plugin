package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;

public class GoTypeFunction extends GoTypeBase<GoPsiTypeFunction> {
    @NotNull
    private final GoType[] parameters;

    @NotNull
    private final GoType[] results;

    private final boolean isVariadic;

    public GoTypeFunction(@Nullable GoPsiTypeFunction psiType, @NotNull GoType[] parameters, @NotNull GoType[] results, boolean isVariadic) {
        super(psiType);
        this.parameters = parameters;
        this.results = results;
        this.isVariadic = isVariadic;
    }

    @Override
    public boolean isIdentical(GoType goType) {
        if (!(goType instanceof GoTypeFunction))
            return false;

        GoTypeFunction functionDeclaration = (GoTypeFunction) goType;

        GoType[] funcTypeArguments = this.getParameters();
        GoType[] funcDeclArguments = functionDeclaration.getParameters();

        if (isVariadic != functionDeclaration.isVariadic) {
            return false;
        }

        int idx = 0;

        if (funcDeclArguments.length != funcTypeArguments.length)
            return false;

        for (GoType parameter : funcDeclArguments) {
            if (!parameter.isIdentical(funcTypeArguments[idx]))
                return false;
            idx++;
        }

        funcTypeArguments = this.getResults();
        funcDeclArguments = functionDeclaration.getResults();

        if (funcDeclArguments.length != funcTypeArguments.length)
            return false;

        idx = 0;
        for (GoType parameter : funcDeclArguments) {
            if (!parameter.isIdentical(funcTypeArguments[idx]))
                return false;
            idx++;
        }
        return true;
    }

    @NotNull
    public GoType[] getParameters() {
        return parameters;
    }

    @NotNull
    public GoType[] getResults() {
        return results;
    }

    public boolean isVariadic() {
        return isVariadic;
    }
}
