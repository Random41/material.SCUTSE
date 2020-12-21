package inter;

import symbols.Type;

public class For extends Stmt {
	Expr expr; Stmt stmt1; Stmt stmt2; Stmt stmt3; 
	
	public For() { expr = null; stmt1 = null; stmt2 = null; stmt3 = null; }
	
	public void init(Stmt s1, Expr x, Stmt s2, Stmt s3) {
		expr = x; stmt1 = s1; stmt2 = s2; stmt3 = s3;
		if( expr.type != Type.Bool ) expr.error("boolean required in for"); //判断语句一定是bool类型
	}


	public void gen(int b, int a) {
		int label=newlabel(); stmt1.gen(label,b); //生成新label，输出语句1（初始化语句）

		int over=label; //保存循环第一条语句（判断语句）
		emitlabel(label); expr.jumping(0,a); //输出iffalse语句（判断语句）

		label=newlabel();
		emitlabel(label); stmt3.gen(label,b); //输出循环体内语句

		label=newlabel();
		emitlabel(label); stmt2.gen(label,b); //输出第二条语句（一般为自增语句）
		emit("goto L" + over); //跳回判断语句
	}

}
