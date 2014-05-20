package ro.redeul.google.go.lang.psi.types.struct;

import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;

public class GoTypeStructPromotedFields {
    private final GoIdentifier[] namedFields;
    private final GoTypeStructAnonymousField[] anonymousFields;

    public GoTypeStructPromotedFields(GoIdentifier[] namedFields, GoTypeStructAnonymousField[] anonymousFields) {
        this.namedFields = namedFields;
        this.anonymousFields = anonymousFields;
    }

    public GoIdentifier[] getNamedFields() {
        return namedFields;
    }

    public GoTypeStructAnonymousField[] getAnonymousFields() {
        return anonymousFields;
    }
}
