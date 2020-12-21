package main;

import java.io.IOException;
import java.util.Hashtable;

import lexer.Lexer;
import lexer.Token;

public class Main {
	public static void main(String[] args) throws IOException {
		Lexer lexer = new Lexer();
		char c;

		int cnt=0; //计数器，5个token一行
		do {
		    cnt++;
			Token token=lexer.scan();
			switch (token.tag) {

			    //文件末尾
                case 65535:
                    return;
				//标识符
				case 264:
					System.out.print("(ID , "+token.toString()+")"+'\t');
					break;

				//数字常量
				case 270:
				case 272:
					System.out.print("(NUM , "+token.toString()+")"+'\t');
					break;

				//字符串常量
				case 293:
					System.out.print("(STR , "+token.toString()+")"+'\t');
					break;

				//注释
				case 294:
				    System.out.println();
				    cnt--;
					System.out.println("(COMMENT , "+token.toString()+")");
					break;

				//词法错误
				case 295:
                    System.out.println();
                    cnt--;
					System.out.println("！！！invalid！！！"+"in the  "+Lexer.line+"  line："+token.toString()+'\t');
					break;

				/*以下的全是关键字
					因为对于case匹配到一个是关键字就会一直执行下去，所以可以省下相同的语句*/

				case 257:
				case 258:
				case 259:
				case 260:
				case 262:
				case 265:
				case 266:
				case 273:
				case 274:
				case 275:
				case 276:
				case 277:
				case 278:
				case 279:
				case 280:
				case 281:
				case 282:
				case 283:
				case 284:
				case 285:
				case 286:
				case 287:
				case 288:
				case 289:
				case 290:
				case 291:
				case 292:
					System.out.print("(KEY , "+token.toString()+")"+'\t');
					break;

				//以下全为标识符
				case 256:
				case 261:
				case 263:
				case 267:
				case 268:
				case 269:
				case 271:
				default:
					System.out.print("(SYM , "+token.toString()+")"+'\t');
					break;
			}

			if(cnt==5)
            {
                System.out.println();
                cnt=0;
            }
			//消去当前的空行保证可以继续读取
			/*if(lexer.getPeek()=='\n' )
				lexer.getnext();*/

		} while (lexer.getPeek()!=65535); //文件结束标志，因为char类型被赋值为-1时值为65535


	}
}


