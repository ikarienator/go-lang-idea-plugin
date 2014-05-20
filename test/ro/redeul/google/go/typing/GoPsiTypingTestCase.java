package ro.redeul.google.go.typing;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import ro.redeul.google.go.GoFileBasedPsiTestCase;
import ro.redeul.google.go.lang.psi.expressions.GoExpr;
import ro.redeul.google.go.lang.psi.resolve.GoResolveUtil;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.typing.GoType;
import ro.redeul.google.go.lang.psi.typing.GoTypeUntyped;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import java.util.HashSet;
import java.util.Set;

public class GoPsiTypingTestCase extends GoFileBasedPsiTestCase {
    public String MARKER = "/*type*/";

    @Override
    protected String getTestDataRelativePath() {
        return "psi/typing/";
    }

    Set<GoType[]> expressionsTypes = new HashSet<GoType[]>();

    @Override
    protected void postProcessFilePsi(PsiFile psiFile, String fileContent) {

        int position = 0;
        while ((position = fileContent.indexOf(MARKER, position)) > 0) {
            position += MARKER.length();

            PsiElement element = psiFile.findElementAt(position);
            while (!(element instanceof GoExpr) &&
                    element != null &&
                    element.getStartOffsetInParent() == 0) {
                element = element.getParent();
            }

            if (!(element instanceof GoExpr)) {
                fail("The maker was not positioned on an expression");
            }

            GoExpr expr = (GoExpr) element;
            expressionsTypes.add(expr.getType());
        }
    }

    @Override
    protected void assertTest() {
        int typesCount = 0;
        for (GoType[] types : expressionsTypes) {
            typesCount = Math.max(typesCount, types.length);
        }

        GoType[] baseTypes = GoType.EMPTY_ARRAY;
        for (GoType[] expressionTypes : expressionsTypes) {
            assertEquals("We should have the same number of types",
                    typesCount, expressionTypes.length);
            baseTypes = expressionTypes;
        }

        assertNotSame(GoPsiType.EMPTY_ARRAY, baseTypes);

        for (GoType[] expressionsType : expressionsTypes) {
            for (int i = 0; i < baseTypes.length; i++) {
                GoType baseType = GoTypeUtils.resolveToFinalType(baseTypes[i]);

                assertNotNull(baseType);
                assertNotNull(expressionsType[i]);
                assertTrue("We should have the same underlying type",
                        baseType.isIdentical(GoTypeUtils.resolveToFinalType(expressionsType[i])));
            }
        }
    }

    public void testPredefinedTypes() throws Exception {
        doTest();
    }

    public void testArrayTypes() throws Exception {
        doTest();
    }

    public void testSliceTypes() throws Exception {
        doTest();
    }

    public void testPointerTypes() throws Exception {
        doTest();
    }

    public void testBinaryExpressions() throws Exception {
        doTest();
    }
}
