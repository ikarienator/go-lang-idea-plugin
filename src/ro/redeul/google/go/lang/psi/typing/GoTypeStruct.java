package ro.redeul.google.go.lang.psi.typing;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.utils.GoPsiUtils;

import java.util.ArrayList;

/**
 * // TODO: mtoader ! Finish this implementation.
 */
public class GoTypeStruct extends GoTypePsiBacked<GoPsiTypeStruct> implements GoType {

    private final String names[];
    private final GoType types[];
    private final String tags[];

    public GoTypeStruct(GoPsiTypeStruct type) {
        super(type);
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<GoType> types = new ArrayList<GoType>();
        ArrayList<String> tags = new ArrayList<String>();
        for (PsiElement structField : type.getAllFields()) {
            if (structField instanceof GoTypeStructField) {
                GoLiteralIdentifier[] identifiers = ((GoTypeStructField) structField).getIdentifiers();
                GoType fieldType = GoTypes.fromPsiType(((GoTypeStructField) structField).getType());
                GoLiteralString tag = ((GoTypeStructField) structField).getTag();
                for (GoLiteralIdentifier identifier : identifiers) {
                    names.add(identifier.getUnqualifiedName());
                    types.add(fieldType);
                    tags.add(tag == null ? null : tag.getValue());
                }
            } else if (structField instanceof GoTypeStructAnonymousField) {
                names.add(null);
                types.add(GoTypes.fromPsiType(((GoTypeStructAnonymousField) structField).getType()));
                GoLiteralString tag = ((GoTypeStructAnonymousField) structField).getTag();
                tags.add(tag == null ? null : tag.getValue());
            }
        }
        int n = names.size();
        this.names = names.toArray(new String[n]);
        this.types = types.toArray(new GoType[n]);
        this.tags = tags.toArray(new String[n]);
    }

    @Override
    public boolean isIdentical(@NotNull GoType type) {
        if (type == this) return true;
        if (!(type instanceof GoTypeStruct))
            return false;

        GoTypeStruct structType = (GoTypeStruct) type;
        if (structType.names.length != this.names.length) return false;
        for (int i = 0; i < this.names.length; i++) {
            if (this.names[i] == null) {
                if (structType.names[i] != null) {
                    return false;
                }
            } else {
                if (structType.names[i] == null) {
                    return false;
                }
                if (!structType.names[i].equals(this.names[i])) {
                    return false;
                }
            }
            if (!this.types[i].isIdentical(structType.types[i])) {
                return false;
            }

            if (this.tags[i] == null) {
                if (structType.tags[i] != null) {
                    return false;
                }
            } else {
                if (structType.tags[i] == null) {
                    return false;
                }
                if (!structType.tags[i].equals(this.tags[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitTypeStruct(this);
    }

    @NotNull
    @Override
    public String getNameLocalOrGlobal(@Nullable GoFile currentFile) {
        StringBuilder stringBuilder = new StringBuilder("struct{");
        for (int i = 0; i < this.names.length; i++) {
            if (i != 0)
                stringBuilder.append("; ");

            if (names[i] != null) {
                stringBuilder.append(names[i]);
                stringBuilder.append(' ');
            }

            stringBuilder.append(types[i].getNameLocalOrGlobal(currentFile));
            if (tags[i] != null) {
                stringBuilder.append(' ');
                stringBuilder.append(GoPsiUtils.escapeStringLiteral(tags[i]));
            }
        }

        return stringBuilder.toString() + "}";
    }
}
