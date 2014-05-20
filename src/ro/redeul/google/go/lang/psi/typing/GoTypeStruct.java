package ro.redeul.google.go.lang.psi.typing;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;

import java.util.ArrayList;

public class GoTypeStruct extends GoTypeBase<GoPsiTypeStruct> {
    @Nullable
    private final String name;

    @NotNull
    private final String[] names;

    @NotNull
    private final GoType[] types;

    @NotNull
    private final String[] tags;

    public GoTypeStruct(@Nullable GoPsiTypeStruct psiType, @Nullable String name, @NotNull ArrayList<String> names, @NotNull ArrayList<GoType> types, @NotNull ArrayList<String> tags) {
        super(psiType);
        this.name = name;
        int n = names.size();
        this.names = names.toArray(new String[n]);
        this.types = types.toArray(new GoType[n]);
        this.tags = tags.toArray(new String[n]);
    }

    @Override
    public boolean isIdentical(GoType other) {
        if (this == other) return true;
        if (!(other instanceof GoTypeStruct)) return false;


        GoTypeStruct struct = ((GoTypeStruct) other);

        if (name == null && struct.name != null) {
            return false;
        }

        if (struct.name == null) {
            return false;
        }

        if (!name.equals(struct.name)) {
            return false;
        }

        if (names.length != struct.names.length) {
            return false;
        }
        for (int i = 0; i < names.length; i++) {
            if (!names[i].equals(struct.names[i])) {
                return false;
            }
        }
        if (types.length != struct.types.length) {
            return false;
        }
        for (int i = 0; i < types.length; i++) {
            if (!types[i].isIdentical(struct.types[i])) {
                return false;
            }
        }
        if (tags.length != struct.tags.length) {
            return false;
        }
        for (int i = 0; i < tags.length; i++) {
            if (!tags[i].equals(struct.tags[i])) {
                return false;
            }
        }
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
