package ro.redeul.google.go.lang.psi.impl.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.impl.GoPsiPackagedElementBase;
import ro.redeul.google.go.lang.psi.impl.types.struct.PromotedFieldsDiscover;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeStruct;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructAnonymousField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructField;
import ro.redeul.google.go.lang.psi.types.struct.GoTypeStructPromotedFields;
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

//    @Override
//    public GoPsiElement[] getMembers() {
//        List<GoPsiElement> members = new ArrayList<GoPsiElement>();
//
//        GoTypeStructField fields[] = getFields();
//        for (GoTypeStructField field : fields) {
//            Collections.addAll(members, field.getIdentifiers());
//        }
//
//        GoTypeStructAnonymousField fieldAnonymous[] = getAnonymousFields();
//        for (GoTypeStructAnonymousField field : fieldAnonymous) {
//            GoType type = field.getType();
//            Collections.addAll(members, type.getMembers());
//        }
//
//        return members.toArray(new GoPsiElement[members.size()]);
//    }

    //    @Override
//    public GoType getMemberType(String name) {
//
//        GoTypeStructField fields[] = getFields();
//        for (GoTypeStructField field : fields) {
//            for (GoLiteralIdentifier identifier : field.getIdentifiers()) {
//                String identifierName = identifier.getName();
//
//                if ( identifierName != null && identifierName.equals(name) ) {
//                    return field.getType();
//                }
//            }
//        }
//
//        GoTypeStructAnonymousField fieldAnonymous[] = getAnonymousFields();
//        for (GoTypeStructAnonymousField field : fieldAnonymous) {
//            GoType type = field.getType();
//            GoType memberType = type.getMemberType(name);
//            if ( memberType != null ) {
//                return memberType;
//            }
//        }
//
//        return null;
//    }
//
    @Override
    public void accept(GoElementVisitor visitor) {
        visitor.visitStructType(this);
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
