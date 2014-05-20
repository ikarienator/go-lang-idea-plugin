package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.expressions.GoIdentifier;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralString;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.impl.types.struct.PromotedFieldsDiscover;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeStruct;
import ro.redeul.google.go.lang.psi.visitors.GoElementVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: 5/27/11
 * Time: 12:17 AM
 */
public class GoPsiTypeStructImpl extends GoPsiPackagedElementBase implements
        GoPsiTypeStruct {

    public GoPsiTypeStructImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String getName() {
        return super.getParent().getFirstChild().getText();
    }

    @Override
    public GoTypeStructField[] getFields() {
        return findChildrenByClass(GoTypeStructField.class);
    }

    @NotNull
    @Override
    public GoTypeStructPromotedFields getPromotedFields() {
        return new PromotedFieldsDiscover(this).getPromotedFields();
    }


    @Override
    public GoTypeStructAnonymousField[] getAnonymousFields() {
        return findChildrenByClass(GoTypeStructAnonymousField.class);
    }

    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitStructType(this);
    }

    @Override
    public GoType resolveType() {
        PsiElement[] fields = getAllFields();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<GoType> types = new ArrayList<GoType>();
        ArrayList<String> tags = new ArrayList<String>();
        for (PsiElement field : fields) {
            if (field instanceof GoTypeStructField) {
                GoTypeStructField namedField = (GoTypeStructField) field;
                GoIdentifier[] identifiers = namedField.getIdentifiers();
                GoType type = namedField.getType().getType();
                for (GoIdentifier identifier : identifiers) {
                    names.add(identifier.getCanonicalName());
                    types.add(type);
                    if (namedField.getTag() instanceof GoLiteralString) {
                        tags.add(((GoLiteralString) namedField.getTag()).getValue());
                    } else {
                        tags.add(null);
                    }
                }
            } else if (field instanceof GoTypeStructAnonymousField) {
                GoTypeStructAnonymousField anonymousField = (GoTypeStructAnonymousField) field;
                String name = anonymousField.getFieldName();
                GoType type = anonymousField.getType().getType();
                names.add(name);
                types.add(type);
                if (anonymousField.getTag() instanceof GoLiteralString) {
                    tags.add(((GoLiteralString) anonymousField.getTag()).getValue());
                } else {
                    tags.add(null);
                }
            }
        }
        return new GoTypeStruct(this, getName(), names, types, tags);
    }

    public PsiElement[] getAllFields() {
        List<PsiElement> vector = new ArrayList<PsiElement>();

        for (PsiElement element = getFirstChild(); element != null; element = element.getNextSibling()) {
            if (element instanceof GoTypeStructField || element instanceof GoTypeStructAnonymousField) {
                vector.add(element);
            }
        }
        return vector.toArray(new GoPsiElement[vector.size()]);
    }

}
