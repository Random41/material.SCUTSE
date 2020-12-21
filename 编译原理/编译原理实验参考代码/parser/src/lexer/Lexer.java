package lexer;

import java.io.IOException;
import java.util.Hashtable;
import java.io.FileInputStream;
import java.util.Scanner;

public class Lexer{

	public static int line = 1; //当前代码分析到第几行
	char peek = ' '; //文件读写指针
	Hashtable words = new Hashtable(); //存关键字和标识符的哈希表


	void reserve(Word w) { //添加关键字函数
		words.put(w.lexeme, w);
	}

	public Lexer() {

		//关键字的添加
		reserve(Word.Break);
		reserve(Word.Do);
		reserve(Word.If);
		reserve(Word.Else);
		reserve(Word.True);
		reserve(Word.False);
		reserve(Word.While);
		reserve(Word.Void);
		reserve(Word.Int);
		reserve(Word.Double);
		reserve(Word.Bool);
		reserve(Word.Str);
		reserve(Word.Class);
		reserve(Word.Null);
		reserve(Word.This);
		reserve(Word.Extends);
		reserve(Word.For);
		reserve(Word.Ret);
		reserve(Word.New);
		reserve(Word.Newarr);
		reserve(Word.Print);
		reserve(Word.Ri);
		reserve(Word.Rl);
		reserve(Word.Static);
		reserve(Word.Float);
		reserve(Word.temp);
		reserve(Word.Basic);


		//流重定向，将标准流重定向到文件流
		try {
			FileInputStream fis=new FileInputStream("C:\\Users\\25852\\Desktop\\test.txt");
			{
				System.setIn(fis);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public void readch() throws IOException { //读取一个字符
		peek = (char) System.in.read();
	}

	boolean readch(char c) throws IOException { //读取字符并判断是否为目标字符
		readch();
		if (peek != c) {
			return false;
		}
		peek = ' ';
		return true;
	}

	public Token scan() throws IOException {
		for (; ; readch()) {
			if (peek == ' ' || peek == '\t' || peek=='\r')
				continue;
			else if((int)peek==65535)
				return new Token(65535);
			else if (peek == '\n') {
				line += 1;
			} else {
				break;
			}
		}

		//特殊符号（运算符）的识别
		switch (peek) {
			case '&':
				if (readch('&'))
					return Word.and;
				else
					return new Token('&');
			case '|':
				if (readch('|'))
					return Word.or;
				else
					return new Token('|');
			case '=':
				if (readch('='))
					return Word.eq;
				else
					return new Token('=');
			case '!':
				if (readch('='))
					return Word.ne;
				else
					return new Token('!');
			case '<':
				if (readch('='))
					return Word.le;
				else
					return new Token('<');
			case '>':
				if (readch('='))
					return Word.ge;
				else
					return new Token('>');
			case '+':
				readch();
				return new Token('+');
			case '-':
				readch();
				return new Token('-');
			case '*' :
				readch();
				return new Token('*');
			case '%':
				readch();
				return new Token('%');
			case '\\':
				readch();
				return new Token('\\');
			case ';':
				readch();
				return new Token(';');
			case ',':
				readch();
				return new Token(',');
			case '.':
				readch();
				return new Token('.');
			case '(':
				readch();
				return new Token('(');
			case ')':
				readch();
				return new Token(')');
			case '[':
				readch();
				return new Token('[');
			case ']':
				readch();
				return new Token(']');
			case '{':
				readch();
				return new Token('{');
			case '}':
				readch();
				return new Token('}');

		}

		//数字常量识别
		if (Character.isDigit(peek)) {
			//整数识别
			int v = 0;
			//十六进制识别
			if (peek == '0') {
				readch();
				if (peek == 'x' || peek == 'X') {

					readch();
					do {
						v = 16 * v + Character.digit(peek, 16);
						readch();
					} while (Character.isLetterOrDigit(peek));
					if (peek != '.')
						return new Num(v, 2);
				}

				//十进制识别
			} else {
				//readch();
				do {
					v = 10 * v + Character.digit(peek, 10);
					readch();
				} while (Character.isDigit(peek));
				if (peek != '.')
					return new Num(v, 1);
			}


			//浮点数识别

			float x = v;
			float d = 10;
			for (; ; ) {
				readch();
				//科学记数法判断
				if (!Character.isDigit(peek)) //判断是否有指数
				{
					if (peek == 'e' || peek == 'E') {
						readch();
						int flag = 1;//判断指数正负
						if (peek == '+')
							flag = 1;
						else
							flag = -1;

						readch();
						int e = 0; //指数
						do {
							e = 10 * e + Character.digit(peek, 10);
							readch();
						} while (Character.isDigit(peek));
						return new Real((float) (x * Math.pow(10, e * flag))); //尾数乘上指数即为浮点数

					} else break;
				}
				x = x + Character.digit(peek, 10) / d;
				d = d * 10;
			}
			return new Real(x);
		}

		//字符串常量识别
		if(peek=='\"')
		{
			readch();
			StringBuffer b = new StringBuffer();
			b.append('\"');
			while (peek!='\"'){
				b.append(peek);
				readch();
			}
			readch();
			b.append('\"');
			String s = b.toString();
			Word w = new Word(s, Tag.STR);
			return w;

		}

		//注释识别
		if(peek=='/')
		{
			StringBuffer b = new StringBuffer();
			readch();

			//  “//”型注释
			if(peek=='/')
			{
				readch();
				b.append('/');
				b.append('/');
				for(;;readch())
				{
					if(peek=='\n' || peek=='\r')
						break;
					else b.append(peek);
				}
			}
			//  单纯的“/”号
			else if(peek!='*')
				return new Token('/');
				//  “/**/” 型注释
			else
			{
				readch();
				b.append('/');
				b.append('*');
				int lsum=1,rsum=0;  //lsum：左注释号个数  rsum：右注释号个数
				for(;;readch())
				{
					//右注释
					if(peek=='*')
					{
						readch();
						if(peek=='/')
						{
							rsum+=1;
							if(lsum==rsum)
							{
								readch();
								b.append('*');
								b.append('/');
								break; //如果左注释号个数等于右注释号个数则匹配完成
							}
							b.append('*');
							b.append('/');
						}
						else
						{
							b.append('*');
							b.append(peek);
						}

					}

					//左注释
					else if(peek=='/')
					{
						readch();
						if(peek=='*')
						{
							lsum+=1;
							b.append('/');
							b.append('*');
						}
						else
						{
							b.append('/');
							b.append(peek);
						}

					}

					//换行
					else if(peek=='\n')
					{
						line+=1;  //多行注释可能会换行
						b.append('\n');
					}

					else b.append(peek);
				}
				//return new Token(Tag.COMMENT);
			}

			String s = b.toString();
			Word w = new Word(s, Tag.COMMENT);
			return w;
		}

		//标识符以及关键字识别
		if (Character.isLetter(peek)) {
			StringBuffer b = new StringBuffer();
			do {
				b.append(peek);
				readch();
			} while (Character.isLetterOrDigit(peek) || peek=='_');
			//readch();
			String s = b.toString();
			Word w = (Word) words.get(s);
			if (w != null)
				return w;
			w = new Word(s, Tag.ID);
			words.put(s, w);
			return w;
		}

		//什么都不是，即是非法字符
		Word w=new Word(""+peek,Tag.ILLEGAL);
		//peek = ' ';
		readch();
		return w;


	}


	//输出哈希表大小
	public void out() {
		System.out.println(words.size());

	}

	//获取当前文件读写指针
	public char getPeek() {
		return peek;
	}

	//设置当前文件读写指针
	public void setPeek(char peek) {
		this.peek = peek;
	}

	//如果当前peek为换行符那就读下一个字符
	public void getnext()throws IOException
	{
		while(peek=='\n' )
		{
			readch();
			line+=1;
		}

	}

}
