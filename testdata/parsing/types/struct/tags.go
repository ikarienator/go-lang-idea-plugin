package main
type T struct {
  a int "tag1"
  b, c int "tag2"
  d struct { e int `tag3` } "tag 4"
}
/**-----
Go file
  PackageDeclaration(main)
    PsiElement(KEYWORD_PACKAGE)('package')
    PsiWhiteSpace(' ')
    PsiElement(IDENTIFIER)('main')
  PsiWhiteSpace('\n')
  TypeDeclarationsImpl
    PsiElement(KEYWORD_TYPE)('type')
    PsiWhiteSpace(' ')
    TypeSpecImpl
      TypeNameDeclaration(T)
        PsiElement(IDENTIFIER)('T')
      PsiWhiteSpace(' ')
      TypeStructImpl
        PsiElement(KEYWORD_STRUCT)('struct')
        PsiWhiteSpace(' ')
        PsiElement({)('{')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('  ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('a')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
          PsiWhiteSpace(' ')
          LiteralStringImpl
            PsiElement(LITERAL_STRING)('"tag1"')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('  ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('b')
          PsiElement(,)(',')
          PsiWhiteSpace(' ')
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('c')
          PsiWhiteSpace(' ')
          TypeNameImpl
            LiteralIdentifierImpl
              PsiElement(IDENTIFIER)('int')
          PsiWhiteSpace(' ')
          LiteralStringImpl
            PsiElement(LITERAL_STRING)('"tag2"')
        PsiWhiteSpace('\n')
        PsiWhiteSpace('  ')
        TypeStructFieldImpl
          LiteralIdentifierImpl
            PsiElement(IDENTIFIER)('d')
          PsiWhiteSpace(' ')
          TypeStructImpl
            PsiElement(KEYWORD_STRUCT)('struct')
            PsiWhiteSpace(' ')
            PsiElement({)('{')
            PsiWhiteSpace(' ')
            TypeStructFieldImpl
              LiteralIdentifierImpl
                PsiElement(IDENTIFIER)('e')
              PsiWhiteSpace(' ')
              TypeNameImpl
                LiteralIdentifierImpl
                  PsiElement(IDENTIFIER)('int')
              PsiWhiteSpace(' ')
              LiteralStringImpl
                PsiElement(LITERAL_STRING)('`tag3`')
            PsiWhiteSpace(' ')
            PsiElement(})('}')
          PsiWhiteSpace(' ')
          LiteralStringImpl
            PsiElement(LITERAL_STRING)('"tag 4"')
        PsiWhiteSpace('\n')
        PsiElement(})('}')