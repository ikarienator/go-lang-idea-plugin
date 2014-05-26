package ro.redeul.google.go.util;

import com.intellij.ide.Bootstrap;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ro.redeul.google.go.lang.psi.GoFile;
import ro.redeul.google.go.lang.psi.GoPsiElement;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclaration;
import ro.redeul.google.go.lang.psi.declarations.GoConstDeclarations;
import ro.redeul.google.go.lang.psi.declarations.GoVarDeclaration;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteral;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralFunction;
import ro.redeul.google.go.lang.psi.expressions.literals.GoLiteralIdentifier;
import ro.redeul.google.go.lang.psi.expressions.primary.GoCallOrConvExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoLiteralExpression;
import ro.redeul.google.go.lang.psi.expressions.primary.GoParenthesisedExpression;
import ro.redeul.google.go.lang.psi.processors.GoResolveStates;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionDeclaration;
import ro.redeul.google.go.lang.psi.toplevel.GoFunctionParameter;
import ro.redeul.google.go.lang.psi.types.GoPsiType;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeFunction;
import ro.redeul.google.go.lang.psi.types.GoPsiTypeParenthesized;
import ro.redeul.google.go.lang.psi.types.GoPsiTypePointer;
import ro.redeul.google.go.lang.psi.typing.*;
import ro.redeul.google.go.lang.psi.utils.GoPsiScopesUtil;
import ro.redeul.google.go.lang.psi.utils.GoTypeUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ro.redeul.google.go.lang.psi.utils.GoIdentifierUtils.getFunctionDeclaration;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.findChildOfClass;
import static ro.redeul.google.go.lang.psi.utils.GoPsiUtils.psiIsA;

/**
 * Author: Toader Mihai Claudiu <mtoader@gmail.com>
 * <p/>
 * Date: Aug 19, 2010
 * Time: 4:18:54 AM
 */
public class GoUtil {

    /**
     * Gets the idea home directory.
     * Note: Copied over from IDEA Main class but the home directory doesn't seem to be properly made available to
     * plugins.
     *
     * @return The idea home directory
     */
    private static File getIdeaHomeDir() {
        URL url = Bootstrap.class.getResource("");
        if (url == null || !"jar".equals(url.getProtocol())) return null;

        String path = url.getPath();

        int start = path.indexOf("file:/");
        int end = path.indexOf("!/");
        if (start == -1 || end == -1) return null;

        String jarFileUrl = path.substring(start, end);

        try {
            File bootstrapJar = new File(new URI(jarFileUrl));
            return bootstrapJar.getParentFile().getParentFile();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static boolean testGoHomeFolder(String goRoot) {
        return goRoot != null
                && goRoot.trim().length() > 0 && new File(goRoot).isDirectory();
    }

    private final static Pattern RE_PACKAGE_TARGET =
            Pattern.compile("^TARG=([^\\s]+)\\s*$", Pattern.MULTILINE);

    /**
     * Returns a string if there is a TARG=xxx specified in the provided makefile and null if there is no such file.
     *
     * @param makefile the file we want to test (can be null)
     * @return the specified target or null
     */
    public static String getTargetFromMakefile(VirtualFile makefile) {
        if (makefile == null) {
            return null;
        }

        try {
            String content = new String(makefile.contentsToByteArray(), "UTF-8");

            Matcher matcher = RE_PACKAGE_TARGET.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        } catch (IOException e) {
            //
        }

        return null;
    }


    public static String getNameLocalOrGlobalAsParameter(GoPsiType type, GoFile currentFile) {
        // FIXME(ikarienator):this can't be right.
        // if (type.getParent().getNode().getElementType().equals(GoElementTypes.FUNCTION_PARAMETER_VARIADIC))
        // return String.format("...%s", recursiveNameOrGlobalTypeImp(type, currentFile));
        return GoTypes.fromPsiType(type).getNameLocalOrGlobal(currentFile);
    }

    public static boolean isFunctionNameIdentifier(PsiElement e) {
        if (!psiIsA(e, GoLiteralExpression.class))
            return false;

        GoLiteral literal = ((GoLiteralExpression) e).getLiteral();
        if (!(literal instanceof GoLiteralIdentifier))
            return false;

        if (((GoLiteralIdentifier) literal).isQualified())
            return false;

        if (!psiIsA(e.getParent(), GoCallOrConvExpression.class))
            return false;

        // function name is the first element of its parent.
        return e.getStartOffsetInParent() == 0;
    }

    public static String getNameLocalOrGlobal(GoType type, GoFile currentFile) {
        return type.getNameLocalOrGlobal(currentFile);
    }


    public static GoPsiElement ResolveReferece(GoPsiElement element) {
        for (PsiReference reference : element.getReferences()) {
            PsiElement resolve = reference.resolve();
            if (resolve != null && resolve != element)
                return ResolveReferece((GoPsiElement) resolve);
        }
        return element;
    }

    // FIXME
    private static boolean isAssignableFrom(GoPsiType element, Object element2, GoPsiElement goExpr) {
/*
        if (element2 instanceof GoTypePsiBacked) {
            GoPsiType psiType = ((GoTypePsiBacked) element2).getPsiType();
            if (psiType != null)
                return psiType.isIdentical(element);
        }

        if (element instanceof GoPsiTypeFunction && !(element2 instanceof GoPsiTypeFunction)) {


            if (goExpr instanceof GoCallOrConvExpression) {
                element2 = findChildOfClass(goExpr, GoPsiTypeParenthesized.class);
                if (element2 != null) {
                    element2 = ((GoPsiTypeParenthesized) element2).getInnerType();
                    return isAssignableFrom(element, element2, null);
                }
            }
            if (goExpr instanceof GoParenthesisedExpression) {
                goExpr = ((GoParenthesisedExpression) goExpr).getInnerExpression();
            }
            if (goExpr instanceof GoLiteralExpression) {
                goExpr = ((GoLiteralExpression) goExpr).getLiteral();
            }
            if (goExpr instanceof GoLiteralIdentifier) {
                goExpr = ResolveReferece(goExpr);
                element2 = getFunctionDeclaration(goExpr);
                if (element2 == null) {
                    element2 = goExpr.getParent().getLastChild();
                    if (!(element2 instanceof GoFunctionDeclaration)) {
                        if (element2 instanceof GoLiteralExpression) {
                            GoType[] type = ((GoLiteralExpression) element2).getType();
                            if (type.length != 0 && type[0] instanceof GoTypePsiBacked)
                                return isAssignableFrom(element, type[0], (GoPsiElement) element2);
                            element2 = ((GoLiteralExpression) element2).getLiteral();
                            if (!(element2 instanceof GoFunctionDeclaration)) {
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }
            }

            if (goExpr instanceof GoFunctionDeclaration)
                return CompareFnTypeToDecl((GoPsiTypeFunction) element, (GoFunctionDeclaration) goExpr);

            return element2 instanceof GoFunctionDeclaration && CompareFnTypeToDecl((GoPsiTypeFunction) element, (GoFunctionDeclaration) element2);
        }


        if (element2 instanceof GoPsiType) {
            GoTypeUtils.resolveToFinalType((GoPsiType) element2);
            return element.isIdentical((GoPsiType) element2);
        }
        //Resolve Issue In PR #330
        if (element instanceof GoPsiTypePointer && element2 instanceof GoTypePointer) {
            GoType targetType1 = ((GoTypePointer) element2).getTargetType();
            return isAssignableFrom(((GoPsiTypePointer) element).getTargetType(), targetType1);
//            if (targetType1 instanceof GoTypePsiBacked) {
//                GoTypePsiBacked targetType = (GoTypePsiBacked) targetType1;
//                return ((GoPsiTypePointer) element).getTargetType().isIdentical(targetType.getPsiType());
//            }
        }
        if (element2 instanceof GoTypeArray) {
            GoPsiType psiType = ((GoTypeArray) element2).getPsiType();
            if (psiType != null)
                return psiType.isIdentical(element);
        }
        if (element2 instanceof GoType) {
            if (goExpr instanceof GoParenthesisedExpression)
                goExpr = ((GoParenthesisedExpression) goExpr).getInnerExpression();
            if (goExpr instanceof GoLiteralExpression) {
                GoLiteral literal = ((GoLiteralExpression) goExpr).getLiteral();
                if (literal instanceof GoLiteralFunction) {
                    return ((GoLiteralFunction) literal).isIdentical(element);
                }
            }

        }

        return element.getUnderlyingType().isIdentical(((GoType) element2).getUnderlyingType());
*/
        return false;
    }


    public static boolean CompareFnTypeToDecl(GoPsiTypeFunction psiType, GoFunctionDeclaration functionDeclaration) {
        if (!CompareParameterList(psiType.getParameters(), functionDeclaration.getParameters()))
            return false;
        return CompareParameterList(psiType.getResults(), functionDeclaration.getResults());
    }

    private static boolean CompareParameterList(GoFunctionParameter[] funcTypeArguments, GoFunctionParameter[] funcDeclArguments) {
        /* FIXME: List<GoPsiType> list = new ArrayList<GoPsiType>();
        for (GoFunctionParameter argument : funcDeclArguments) {
            GoLiteralIdentifier[] identifiers = argument.getIdentifiers();
            if (identifiers.length == 0) {
                list.add(argument.getType());
            } else {
                for (GoLiteralIdentifier identifier : identifiers) {
                    list.add(argument.getType());
                }
            }
        }
        int i = 0;
        for (GoFunctionParameter argument : funcTypeArguments) {
            if (argument.getIdentifiers().length == 0) {
                if (i >= list.size())
                    return false;
                if (!argument.getType().isIdentical(list.get(i)))
                    return false;
                i++;
            } else {
                for (GoLiteralIdentifier identifier : argument.getIdentifiers()) {
                    if (i >= list.size())
                        return false;
                    if (!argument.getType().isIdentical(list.get(i)))
                        return false;
                    i++;
                }
            }
        }*/
        return true;
    }

    public static GoPsiElement ResolveTypeOfVarDecl(GoPsiElement element) {
        PsiElement parent = element.getParent();

        if (parent instanceof GoConstDeclaration) {
            GoPsiType identifiersType = ((GoConstDeclaration) parent).getIdentifiersType();
            if (identifiersType != null)
                return identifiersType;
            GoConstDeclarations parentDec = (GoConstDeclarations) parent.getParent();
            for (GoConstDeclaration declaration : parentDec.getDeclarations()) {
                identifiersType = declaration.getIdentifiersType();
                if (identifiersType != null)
                    return identifiersType;
            }
            return (GoPsiElement) parent.getLastChild();
        }
        if (parent instanceof GoVarDeclaration) {
            GoPsiType identifiersType = ((GoVarDeclaration) parent).getIdentifiersType();
            if (identifiersType != null)
                return identifiersType;
            return (GoPsiElement) parent.getLastChild();
        }
        if (element instanceof GoLiteralIdentifier) {
            for (PsiReference reference : element.getReferences()) {
                if (reference != null) {
                    GoPsiElement resolve = (GoPsiElement) reference.resolve();
                    if (resolve != null && !resolve.equals(element))
                        return ResolveTypeOfVarDecl(resolve);
                }
            }
        }
        return element;
    }

    public static boolean TestDeclVar(PsiElement expr, String k) {
        return !GoPsiScopesUtil.treeWalkUp(new GoVariableScopeCheck(k), expr, expr.getContainingFile(), GoResolveStates.variables());
    }

    public static GoType[] getFuncCallTypes(GoPsiTypeFunction psiTypeFunction) {
        GoFunctionParameter[] results = psiTypeFunction.getResults();
        List<GoPsiType> types = new ArrayList<GoPsiType>();
        for (GoFunctionParameter result : results) {

            GoLiteralIdentifier[] identifiers = result.getIdentifiers();
            if (identifiers == null || identifiers.length == 0) {
                types.add(result.getType());
            } else {
                for (GoLiteralIdentifier identifier : identifiers) {
                    types.add(result.getType());
                }
            }

        }

        return GoTypes.fromPsiType(types.toArray(new GoPsiType[types.size()]));
    }

    private static class GoVariableScopeCheck implements PsiScopeProcessor {
        private String var;

        public GoVariableScopeCheck(String k) {
            var = k;
        }

        @Override
        public boolean execute(@NotNull PsiElement element, ResolveState state) {
            return !element.getText().equals(var);
        }

        @Nullable
        @Override
        public <T> T getHint(@NotNull Key<T> hintKey) {
            return null;
        }

        @Override
        public void handleEvent(Event event, @Nullable Object associated) {

        }
    }
}

