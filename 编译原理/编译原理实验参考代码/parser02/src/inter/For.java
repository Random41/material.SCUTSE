package inter;

import symbols.Type;

public class For extends Stmt{
    /*expr是for语句第二句，即判断循环条件的语句
     stmt是3个语句的数组，分别是1.for语句第一句
                                2.for语句第三句
                                3.循环体类语句
     */
    Expr expr; Stmt []stmt;

    public For() {
        stmt=new Stmt[3];
        for(int i=0;i<3;i++)stmt[i] = null;
        expr = null;
    }

    public void init(Stmt s0,Stmt s1,Stmt s2, Expr x) {
        stmt[0] = s0;
        stmt[1] = s1;
        stmt[2] = s2;
        expr = x;
        if (expr.type != Type.Bool) expr.error("boolean required in for");
    }
    public void gen(int b, int a) {}

    public void display() {
        emit("stmt : for begin");
        stmt[2].display();
        emit("stmt : for end");
    }
}
