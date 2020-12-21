package parser;

import inter.Access;
import inter.And;
import inter.Arith;
import inter.Break;
import inter.Constant;
import inter.Do;
import inter.For;
import inter.Else;
import inter.Expr;
import inter.Id;
import inter.If;
import inter.Not;
import inter.Or;
import inter.Rel;
import inter.Seq;
import inter.Set;
import inter.SetElem;
import inter.Stmt;
import inter.Unary;
import inter.While;

import java.io.IOException;

import symbols.Array;
import symbols.Env;
import symbols.Type;
import lexer.Lexer;
import lexer.Tag;
import lexer.Token;
import lexer.Word;
import lexer.Num;

public class Parser {

	   private Lexer lex;    // lexical analyzer for this parser
	   private Token look;   // lookahead tagen
	   Env top = null;       // current or top symbol table
	   int used = 0;         // storage used for declarations

	   public Parser(Lexer l) throws IOException { lex = l; move(); }

	   void move() throws IOException { look = lex.scan(); }

	   void error(String s) { throw new Error("near line "+lex.line+": "+s); }

	   void match(int t) throws IOException {
	      if( look.tag == t ) move();
	      else error("syntax error");
	   }

	   public void program() throws IOException {  // program -> block
		  // build the syntax tree
	      Stmt s = block();
	      // display the syntax tree
	      // only display the stmts, without expr
	      s.display();
	   }

	   Stmt block() throws IOException {  // block -> { decls stmts }
	      match('{');  Env savedEnv = top;  top = new Env(top);
	      decls(); Stmt s = stmts();
	      match('}');  top = savedEnv;
	      return s;
	   }

	   void decls() throws IOException {

	      while( look.tag == Tag.BASIC || look.tag==Tag.INT || look.tag==Tag.FLOAT ) {   // D -> type ID ;
	         Type p = type(); Token tok = look;
	         /*检测声明错误*/
			  if(look.tag!=Tag.ID)
				  throw new Error("decl error "+lex.line+": "+"\""+p.toString()+look.toString()+"\"");
			  else match(Tag.ID);
	         match(';');
	         Id id = new Id((Word)tok, p, used);
	         top.put( tok, id );
	         used = used + p.width;
	      }
	   }

	   Type type() throws IOException {

	   	int flag=0;
	   	Type p= new Type(look.toString(),Tag.BASIC,0);
	   	/*switch来判断当前 标识符 的类型*/
	   	switch (look.tag)// expect look.tag == Tag.BASIC
		{
			case Tag.INT:  p = Type.Int;break;
			case Tag.FLOAT:  p = Type.Float;break;
			case Tag.BOOL: p = Type.Bool;break;
			default:break;
		}

	      //match(Tag.BASIC);
		   move();
	      if( look.tag != '[' ) return p; // T -> basic
	      else return dims(p);            // return array type
	   }

	   Type dims(Type p) throws IOException {
	      match('[');  Token tok = look;  match(Tag.NUM);  match(']');
	      if( look.tag == '[' )
	      p = dims(p);
	      return new Array(((Num)tok).value, p);
	   }

	   Stmt stmts() throws IOException {
	      if ( look.tag == '}' ) return Stmt.Null;
	      else return new Seq(stmt(), stmts());
	   }

	   Stmt stmt() throws IOException {
	      Expr x;  Stmt s, s1, s2;
	      Stmt savedStmt;         // save enclosing loop for breaks

	      switch( look.tag ) {

	      case ';':
	         move();
	         return Stmt.Null;

	      case Tag.IF:
	         match(Tag.IF);
			  /*检测if左括号是否缺失*/
			  if(look.tag!='(')
				  throw new Error("stmt error "+lex.line+": "+"\""+"if is short of Left parenthesis ");
			  else match('(');

			  x = bool();

			  /*检测if右括号是否缺失*/
			  if(look.tag!=')')
				  throw new Error("stmt error "+lex.line+": "+"\""+"if is short of right parenthesis ");
			  else match(')');
	         s1 = stmt();
	         if( look.tag != Tag.ELSE ) return new If(x, s1);
	         match(Tag.ELSE);
	         s2 = stmt();
	         return new Else(x, s1, s2);

	      case Tag.WHILE:
	         While whilenode = new While();
	         savedStmt = Stmt.Enclosing; Stmt.Enclosing = whilenode;
	         match(Tag.WHILE);

			  /*检测while左括号是否缺失*/
			  if(look.tag!='(')
				  throw new Error("stmt error "+lex.line+": "+"\""+"while is short of Left parenthesis ");
			  else match('(');

	         x = bool();

			  /*检测while右括号是否缺失*/
			  if(look.tag!=')')
				  throw new Error("stmt error "+lex.line+": "+"\""+"while is short of right parenthesis ");
			  else match(')');
	         s1 = stmt();
	         whilenode.init(x, s1);
	         Stmt.Enclosing = savedStmt;  // reset Stmt.Enclosing
	         return whilenode;

	      case Tag.DO:
	         Do donode = new Do();
	         savedStmt = Stmt.Enclosing; Stmt.Enclosing = donode;
	         match(Tag.DO);
	         s1 = stmt();
	         match(Tag.WHILE);
			  /*检测while左括号是否缺失*/
			  if(look.tag!='(')
				  throw new Error("stmt error "+lex.line+": "+"\""+"while is short of Left parenthesis ");
			  else match('(');

			  x = bool();

			  /*检测while右括号是否缺失*/
			  if(look.tag!=')')
				  throw new Error("stmt error "+lex.line+": "+"\""+"while is short of right parenthesis ");
			  else match(')');

	         match(';');
	         donode.init(s1, x);
	         Stmt.Enclosing = savedStmt;  // reset Stmt.Enclosing
	         return donode;

	         /*仿照上面写的for语句语法分析程序*/
		 case Tag.FOR:
			For fornode=new For();
			 savedStmt = Stmt.Enclosing; Stmt.Enclosing = fornode;
			 match(Tag.FOR);  //匹配for关键字

			 /*检测for左括号是否缺失*/
			 if(look.tag!='(')
				 throw new Error("stmt error "+lex.line+": "+"\""+"for is short of Left parenthesis ");
			 else match('(');

			 s1=stmt(); //匹配for语句括号内第一个语句
			 x = bool();  //匹配for语句括号内第二个语句（bool类型）
			 match(';'); //匹配bool语句之后的分号
			 s2=stmt(); //匹配for语句括号内第三个语句

			 /*检测for右括号是否缺失*/
			 if(look.tag!=')')
				 throw new Error("stmt error "+lex.line+": "+"\""+"for is short of right parenthesis ");
			 else match(')');
			 s=stmt(); //匹配for里面的语句
			 fornode.init(s1,s2,s,x);
			 Stmt.Enclosing = savedStmt;  // reset Stmt.Enclosing
			 return fornode;


	      case Tag.BREAK:
	         match(Tag.BREAK); match(';');
	         return new Break();

	      case '{':
	         return block();

	      default:
	         return assign();
	      }
	   }

	   Stmt assign() throws IOException {
	      Stmt stmt;  Token t = look;
	      match(Tag.ID);
	      Id id = top.get(t);
	      if( id == null ) error(t.toString() + " undeclared");

	      if( look.tag == '=' ) {       // S -> id = E ;
	         move();  stmt = new Set(id, bool());
	      }
	      else {                        // S -> L = E ;
	         Access x = offset(id);
	         match('=');  stmt = new SetElem(x, bool());
	      }
	      match(';');
	      return stmt;
	   }

	   Expr bool() throws IOException {
	      Expr x = join();
	      while( look.tag == Tag.OR ) {
	         Token tok = look;  move();  x = new Or(tok, x, join());
	      }
	      return x;
	   }

	   Expr join() throws IOException {
	      Expr x = equality();
	      while( look.tag == Tag.AND ) {
	         Token tok = look;  move();  x = new And(tok, x, equality());
	      }
	      return x;
	   }

	   Expr equality() throws IOException {
	      Expr x = rel();
	      while( look.tag == Tag.EQ || look.tag == Tag.NE ) {
	         Token tok = look;  move();  x = new Rel(tok, x, rel());
	      }
	      return x;
	   }

	   Expr rel() throws IOException {
	      Expr x = expr();
	      switch( look.tag ) {
	      case '<': case Tag.LE: case Tag.GE: case '>':
	         Token tok = look;  move();  return new Rel(tok, x, expr());
	      default:
	         return x;
	      }
	   }

	   Expr expr() throws IOException {
	      Expr x = term();
	      while( look.tag == '+' || look.tag == '-' ) {
	         Token tok = look;  move();  x = new Arith(tok, x, term());
	      }
	      return x;
	   }

	   Expr term() throws IOException {
	      Expr x = unary();
	      while(look.tag == '*' || look.tag == '/' ) {
	         Token tok = look;  move();   x = new Arith(tok, x, unary());
	      }
	      return x;
	   }

	   Expr unary() throws IOException {
	      if( look.tag == '-' ) {
	         move();  return new Unary(Word.minus, unary());
	      }
	      else if( look.tag == '!' ) {
	         Token tok = look;  move();  return new Not(tok, unary());
	      }
	      else return factor();
	   }

	   Expr factor() throws IOException {
	      Expr x = null;
	      switch( look.tag ) {
	      case '(':
	         move(); x = bool(); match(')');
	         return x;
	      case Tag.NUM:
	         x = new Constant(look, Type.Int);    move(); return x;
	      case Tag.REAL:
	         x = new Constant(look, Type.Float);  move(); return x;
	      case Tag.TRUE:
	         x = Constant.True;                   move(); return x;
	      case Tag.FALSE:
	         x = Constant.False;                  move(); return x;
	      default:
	         error("syntax error");
	         return x;
	      case Tag.ID:
	         String s = look.toString();
	         Id id = top.get(look);
	         if( id == null ) error(look.toString() + " undeclared");
	         move();
	         if( look.tag != '[' ) return id;
	         else return offset(id);
	      }
	   }

	   Access offset(Id a) throws IOException {   // I -> [E] | [E] I
	      Expr i; Expr w; Expr t1, t2; Expr loc;  // inherit id

	      Type type = a.type;
	      match('['); i = bool(); match(']');     // first index, I -> [ E ]
	      type = ((Array)type).of;
	      w = new Constant(type.width);
	      t1 = new Arith(new Token('*'), i, w);
	      loc = t1;
	      while( look.tag == '[' ) {      // multi-dimensional I -> [ E ] I
	         match('['); i = bool(); match(']');
	         type = ((Array)type).of;
	         w = new Constant(type.width);
	         t1 = new Arith(new Token('*'), i, w);
	         t2 = new Arith(new Token('+'), loc, t1);
	         loc = t2;
	      }

	      return new Access(a, loc, type);
	   }
	}
