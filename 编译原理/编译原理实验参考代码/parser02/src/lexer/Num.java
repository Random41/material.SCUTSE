package lexer;

public class Num extends Token {

	public final int value;
	public int flag; //flag标志是十进制（1）还是十六进制（2）

	public Num(int v,int f) {
		super(Tag.NUM);
		value = v;
		flag=f;
	}

	//如果是十进制直接输出
	//如果是十六进制则转换为十六进制的形式
	public String toString() {
		if(flag==1)return "" + value;
		else return "0X"+Integer.toHexString(value);
	}

}
