package lexer;

public class Tag {
	public final static int
			AND = 256, OR = 271, GE = 263, LE = 267, EQ = 261,NE = 269, MINUS = 268,
	//特殊符号

	NUM = 270,REAL = 272,ID = 264,STR=293,COMMENT=294,
	//类型符号(即tokentype)                 注释

	ILLEGAL=295,
	//错误类型 （非法字符 )


	INDEX = 266,TEMP = 273,BASIC = 257,
	//未知符号，先不理会,暂时都用不到，但还是定义了对应的word

	BREAK = 258, DO = 259,ELSE = 260,IF = 265,
	TRUE = 274,FALSE = 262,WHILE = 275,VOID=276,
	INT=277,DOUBLE=278,BOOL=279,STRING=280,CLASS=281,
	NULL=282,THIS=283,EXTENDS=284,FOR=285,
	RETURN=286,NEW=287,NEWARRAY=288,PRINT=289,
	READINTEGER=290,READERLINE=291,STATIC=292, FLOAT=296;
	//关键字


}
