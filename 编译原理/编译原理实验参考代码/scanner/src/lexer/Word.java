package lexer;

public class Word extends Token {
	public String lexeme = "";

	public Word(String s, int tag) {
		super(tag);
		lexeme = s;
	}

	public String toString() {
		return lexeme;
	}

	public static final Word
			//特殊符号的word
			and = new Word("&&", Tag.AND),
			or = new Word("||", Tag.OR),
			eq = new Word("==", Tag.EQ),
			ne = new Word("!=", Tag.NE),
			le = new Word("<=", Tag.LE),
			ge = new Word(">=", Tag.GE),
			minus = new Word("minus", Tag.MINUS),


			//关键字的word
			Break=new Word("break",Tag.BREAK),
			Do=new Word("do",Tag.DO),
			If=new Word("if",Tag.IF),
			Else=new Word("else",Tag.ELSE),
			False = new Word("false", Tag.FALSE),
			True = new Word("true", Tag.TRUE),
			While=new Word("while",Tag.WHILE),
			Void=new Word("void",Tag.VOID),
			Int=new Word("int",Tag.INT),
			Double=new Word("double",Tag.DOUBLE),
			Bool=new Word("bool",Tag.BOOL),
			Str=new Word("string",Tag.STRING),
			Class=new Word("class",Tag.CLASS),
			Null=new Word("null",Tag.NULL),
			This=new Word("this",Tag.THIS),
			Extends=new Word("extends",Tag.EXTENDS),
			For=new Word("for",Tag.FOR),
			Ret=new Word("return",Tag.RETURN),
			New=new Word("new",Tag.NEW),
			Newarr=new Word("NewArray",Tag.NEWARRAY),
			Print=new Word("Print",Tag.PRINT),
			Ri=new Word("ReadInteger",Tag.READINTEGER),
			Rl=new Word("ReaderLine",Tag.READERLINE),
			Static=new Word("static",Tag.STATIC);


			//Temp = new Word("t", Tag.TEMP),


}
