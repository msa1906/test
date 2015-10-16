/*
 * HW3
 * @author Mingtong Wu   110033615
 * Mingtong.wu@stonybrook.edu
 * 
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Stack;

public class PythonTracer {
	static String fileName = "";
	static String blockName = "";
	static int currentFileCounter = 1;
	static int preIndents = 0;
	static int indents = 0;
	static final int SPACE_COUNT = 4;
	static Stack tracerStack = new Stack();
	static String str = new String();
	static boolean whileFlag = false;
	static String preName;
	static boolean WhileResultFlag = false;

	public static Complexity traceFile(String fileName)
			throws FileNotFoundException, IOException {
		boolean flag = true;
		// Scanner sc = new Scanner(new File(fileName));
		InputStreamReader inStream = new InputStreamReader(new FileInputStream(
				fileName));// error may happen here
		BufferedReader reader = new BufferedReader(inStream);
		while (flag) {
			do {
				str = reader.readLine();
				// System.out.println(str);
				if (str == null)
					str = "1";// tesingt
				// System.out.println(str);//testing
				// str =sc.nextLine();
			} while (str.trim().isEmpty() || str.trim().charAt(0) == '#');
			indents = ((str.length()) - (str.trim().length())) / SPACE_COUNT;
			// System.out.println(indents + " " + tracerStack.size() + " "
			// + preIndents);// test
			for (int i = 0; i < 6; i++) {
				if (indents < tracerStack.size() && inStream != null) {
					if (indents == 0 && tracerStack.size() == 1) {
						flag = false;
						inStream.close();
						if (i == 5)
							printBlock("", 2);
					} else {
						delateTwo4FileName();
						preIndents--;
						if (WhileResultFlag == true) {
							printBlock("", 4);
							printBlock("", 3);
							WhileResultFlag = false;
						}

						System.out.print("Leaving block "
								+ ((CodeBlock) tracerStack.peek()).getName());
						preName = ((CodeBlock) tracerStack.peek()).getName();
						CodeBlock oldTop = ((CodeBlock) tracerStack.pop());
						blockName = ((CodeBlock) tracerStack.peek()).getName();

						System.out.println(" updating block "
								+ ((CodeBlock) tracerStack.peek()).getName()
								+ ":");
						Complexity oldTopComlexity = oldTop
								.getToltalComplexity();
						if (oldTopComlexity.isGreater(((CodeBlock) tracerStack
								.peek()).getHighestSubComplexity())) {
							((CodeBlock) tracerStack.peek())
									.setHighestSubComplexity(oldTop
											.getToltalComplexity());
						}

						printBlock("", 3);

					}
					// System.out.println(indents + " " + tracerStack.size() +
					// " "
					// + preIndents + "  " + blockName+ "   " + preName);// test
				}
			}
			checker(str);
		}
		return ((CodeBlock) tracerStack.peek()).getBlockComplexity();
	}

	/*
	 * public static void fileNameEqule() { if (indents == preIndents) {
	 * delateTwo4FileName(); fileName += ("." + ++currentFileCounter);
	 * preIndents = indents; } }
	 */

	public static void delateTwo4FileName() {
		int a = blockName.length() - 2;
		if (blockName.trim().length() > 2)// test
			blockName = blockName.substring(0, a);
	}

	public static void checkFileName() {
		String temp = preName;
		if (indents * 2 <= temp.length()) {
			char a = temp.charAt(temp.length() - 1);
			int b = temp.length() - 2;
			temp = temp.substring(0, b);
			temp += "." + (char) (a + 1);
			blockName = temp;
		}
		if (indents * 2 > temp.length()) {
			blockName += ".1";
		}
	}

	public static void checker(String str) {

		if (str.trim().length() > 3
				&& str.trim().substring(0, 4).equals("def ")) {
			blockName = " 1";
			defHooker();
		} else if (str.trim().length() > 3
				&& str.trim().substring(0, 4).equals("for ")) {
			checkFileName();
			forHooker();
		} else if (str.trim().length() > 5
				&& str.trim().substring(0, 6).equals("while ")) {
			checkFileName();
			whileHooker();
		} else if (str.trim().length() > 2
				&& str.trim().substring(0, 3).equals("if ")) {
			checkFileName();
			ifHooker();
		} else if (str.trim().length() > 4
				&& str.trim().substring(0, 5).equals("else:")) {
			checkFileName();
			elseHooker();
		} else if (str.trim().length() > 4
				&& str.trim().substring(0, 5).equals("elif ")) {
			checkFileName();
			elifHooker();
		} else if (whileFlag == true) {
			if (str.contains("-=")) {
				whileFlag = false;
				((CodeBlock) tracerStack.peek()).getBlockComplexity()
						.setN_Power(
								((CodeBlock) tracerStack.peek())
										.getBlockComplexity().getN_Power() + 1);
			}
			System.out.println(str.contains("/="));
			if (str.contains("/=")
			/*
			 * && str.trim() .substring( str.trim().indexOf( ((CodeBlock)
			 * tracerStack.peek()) .getLoopVariable())) .contains("/=")
			 */
			) {
				whileFlag = false;
				((CodeBlock) tracerStack.peek())
						.getBlockComplexity()
						.setLog_Power(
								((CodeBlock) tracerStack.peek())
										.getBlockComplexity().getLog_Power() + 1);
			}
		}
	}

	public static void pushO_one() {
		tracerStack.push(new CodeBlock(blockName, new Complexity(0, 0),
				new Complexity(0, 0)));

	}

	public static void printBlock(String head, int a) {
		if (a == 1) {
			System.out.println("Entering block "
					+ ((CodeBlock) tracerStack.peek()).getName() + " '" + head
					+ "':");
			System.out.println(((CodeBlock) tracerStack.peek()).toString()
					+ "\n");
		}
		if (a == 2) {
			System.out.println("Leaving block 1");
			System.out.println("Overall complexity of test_function: "
					+ ((CodeBlock) tracerStack.peek()).getToltalComplexity()
							.toString() + "\n");
		}
		if (a == 3) {
			System.out.println(((CodeBlock) tracerStack.peek()).toString()
					+ "\n");
		}
		if (a == 4) {
			System.out.println("Found update statement, updating block "
					+ ((CodeBlock) tracerStack.peek()).getName() + ":");
		}
	}

	public static void defHooker() {
		blockName = "1";
		pushO_one();
		printBlock("def", 1);
	}

	public static void forHooker() {
		if (str.contains(" N:"))
			tracerStack.push(new CodeBlock(blockName, new Complexity(1, 0),
					new Complexity(0, 0)));
		else if (str.contains(" log_N:"))
			tracerStack.push(new CodeBlock(blockName, new Complexity(0, 1),
					new Complexity(0, 0)));// throw exception
		printBlock("for", 1);

	}

	public static void whileHooker() {
		tracerStack.push(new CodeBlock(blockName, getLoopVariable(),
				new Complexity(0, 0), new Complexity(0, 0)));
		whileFlag = true;
		// System.out.println((tracerStack.peek()));
		printBlock("while", 1);
	}

	public static String getLoopVariable() {
		int a = 0, b = 0;
		a = str.trim().indexOf(' ');
		b = str.trim().substring(a + 1).indexOf(' ');
		// System.out.println(str.trim().substring(a+1));
		// System.out.println(str.trim().substring(a+1, b + a+1));
		// System.out.println(str.trim().substring(a + 1, b + a + 1));
		return str.trim().substring(a + 1, b + a + 1);

	}

	public static void ifHooker() {
		tracerStack.push(new CodeBlock(blockName, new Complexity(0, 0),
				new Complexity(0, 0)));
		printBlock("if", 1);
	}

	public static void elseHooker() {
		pushO_one();
		printBlock("else", 1);
	}

	public static void elifHooker() {
		pushO_one();
		printBlock("elif", 1);
	}

	public static void main(String[] args) {
		boolean flag = true;
		Scanner input = new Scanner(System.in);
		do {
			fileName = "";
			blockName = "";
			currentFileCounter = 1;
			preIndents = 0;
			indents = 0;
			tracerStack = new Stack();
			str = new String();
			preName = "";
			System.out.println("Please enter the file's name:");
			try {
				// Complexity complexity = traceFile(input.nextLine());
				Complexity complexity = traceFile(input.nextLine());// test
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Quit?(y/any key for no)");
			if (input.nextLine().equals("y"))
				flag = false;
		} while (flag);
		System.out.println("Program terminating successfully..");
	}
}
