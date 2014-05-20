package ro.redeul.google.go.lang.psi.impl.types.struct;

import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.typing.GoTypes;

import java.util.*;

public class PromotedFieldsDiscover {
    private final Map<String, List<GoIdentifier>> namedFieldsMap = new HashMap<String, List<GoIdentifier>>();
    private final Map<String, List<GoTypeStructAnonymousField>> anonymousFieldsMap = new HashMap<String, List<GoTypeStructAnonymousField>>();

    private final GoPsiTypeStruct struct;
    private final Set<String> ignoreNames;

    public PromotedFieldsDiscover(GoPsiTypeStruct struct) {
        this(struct, Collections.<String>emptySet());
    }

    private PromotedFieldsDiscover(GoPsiTypeStruct struct, Set<String> ignoreNames) {
        this.struct = struct;
        this.ignoreNames = new HashSet<String>(ignoreNames);
        this.ignoreNames.addAll(getDirectFieldNameSet());
    }

    public GoTypeStructPromotedFields getPromotedFields() {
        discover();
        return new GoTypeStructPromotedFields(getNamedFields(), getAnonymousFields());
    }

    private boolean ignore(GoTypeStructAnonymousField field) {
        return ignoreNames.contains(field.getFieldName());
    }

    private boolean ignore(GoIdentifier identifier) {
        return ignoreNames.contains(identifier.getUnqualifiedName());
    }

    private GoIdentifier[] getNamedFields() {
        List<GoIdentifier> namedFields = new ArrayList<GoIdentifier>();
        for (List<GoIdentifier> identifiers : namedFieldsMap.values()) {
            if (identifiers.size() == 1 && !ignore(identifiers.get(0))) {
                namedFields.add(identifiers.get(0));
            }
        }

        return namedFields.toArray(new GoIdentifier[namedFields.size()]);
    }

    private GoTypeStructAnonymousField[] getAnonymousFields() {
        List<GoTypeStructAnonymousField> anonymousFields = new ArrayList<GoTypeStructAnonymousField>();
        for (List<GoTypeStructAnonymousField> fields : anonymousFieldsMap.values()) {
            if (fields.size() == 1 && !ignore(fields.get(0))) {
                anonymousFields.add(fields.get(0));
            }
        }
        return anonymousFields.toArray(new GoTypeStructAnonymousField[anonymousFields.size()]);
    }

    private void discover() {
        namedFieldsMap.clear();
        anonymousFieldsMap.clear();

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            GoTypeStruct struct = GoTypes.resolveToStruct(field.getType().getType());
            if (struct == null) {
                continue;
            }

            GoPsiTypeStruct psiType = struct.getPsiType();
            if (psiType == null) {
                continue;
            }

            for (GoTypeStructField subField : psiType.getFields()) {
                for (GoIdentifier identifier : subField.getIdentifiers()) {
                    if (!ignore(identifier)) {
                        addNamedField(identifier);
                    }
                }
            }

            for (GoTypeStructAnonymousField subField : psiType.getAnonymousFields()) {
                if (ignore(subField)) {
                    continue;
                }

                addAnonymousField(subField);
                GoTypeStruct subStruct = GoTypes.resolveToStruct(subField.getType().getType());
                if (subStruct == null) {
                    continue;
                }

                GoPsiTypeStruct subPsiType = struct.getPsiType();
                if (subPsiType != null) {
                    discoverSubType(subPsiType);
                }
            }
        }
    }

    private void discoverSubType(GoPsiTypeStruct subPsiType) {
        GoTypeStructPromotedFields fields = new PromotedFieldsDiscover(subPsiType, ignoreNames).getPromotedFields();
        for (GoIdentifier identifier : fields.getNamedFields()) {
            addNamedField(identifier);
        }

        for (GoTypeStructAnonymousField field2 : fields.getAnonymousFields()) {
            addAnonymousField(field2);
        }
    }

    private void addAnonymousField(GoTypeStructAnonymousField field) {
        String name = field.getFieldName();
        List<GoTypeStructAnonymousField> fields = anonymousFieldsMap.get(name);
        if (fields == null) {
            fields = new ArrayList<GoTypeStructAnonymousField>();
            anonymousFieldsMap.put(name, fields);
        }
        fields.add(field);
    }

    private void addNamedField(GoIdentifier identifier) {
        if (identifier.isBlank()) {
            return;
        }

        String name = identifier.getUnqualifiedName();
        List<GoIdentifier> fields = namedFieldsMap.get(name);
        if (fields == null) {
            fields = new ArrayList<GoIdentifier>();
            namedFieldsMap.put(name, fields);
        }
        fields.add(identifier);
    }

    private Set<String> getDirectFieldNameSet() {
        Set<String> directFields = new HashSet<String>();
        for (GoTypeStructField field : struct.getFields()) {
            for (GoIdentifier identifier : field.getIdentifiers()) {
                if (!identifier.isBlank()) {
                    directFields.add(identifier.getUnqualifiedName());
                }
            }
        }

        for (GoTypeStructAnonymousField field : struct.getAnonymousFields()) {
            directFields.add(field.getFieldName());
        }
        return directFields;
    }

}
