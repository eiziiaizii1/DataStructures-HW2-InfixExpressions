import MyStack.StackSLL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OperationHandler {
    private OperationHandler() {
    }

    OperationHandler(String pathName) {
        File inputFile = new File(pathName);
        processFile(inputFile);
    }

    private void processFile(File inputFile) {
        Scanner sc;
        try {
            sc = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        StackSLL<Character> operatorStack;
        StackSLL<Float> operandStack;

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();//.replace(" ", "")

            operatorStack = new StackSLL<>();
            operandStack = new StackSLL<>();
            operatorStack.push('@');  //'@' is my dummy operator

            boolean isExpValid = true;
            int lineLength = line.length();
            if(line.isBlank()){
                continue;
            }
            System.out.print(line + " = ");

            for (int i = 0; i < lineLength && isExpValid; i++) {
                //-------
                if(line.charAt(i) == ' '){
                    continue;
                }
                //---------
                else if (Character.isDigit(line.charAt(i))) {
                    int numIndex = i + 1;
                    while (numIndex < lineLength && Character.isDigit(line.charAt(numIndex)))
                        numIndex++;

                    if (operandStack.getSize() == 0 && line.charAt(0) == '-')
                        operandStack.push(Float.parseFloat(("-"+line.substring(i, numIndex))));
                    else
                        operandStack.push(Float.parseFloat(line.substring(i, numIndex)));

                    i = numIndex - 1;
                } else if (i != 0 && (isExpValid = isValidOperator(line.charAt(i)))) {
                    // repeat until currentSymbol becomes less than the popped one (Instead of popping and pushing I used top, then popped if necessary)
                    char currentOperator = line.charAt(i);
                    if (checkPrecedence(currentOperator, operatorStack.top())) {
                        // push the current operator
                        operatorStack.push(currentOperator);
                    } else {
                        // pop 2 operands, perform operation, push result to operand stack, repeat until symbol becomes less than the popped one
                        do {
                            try {
                                float operationResult = performOperation(operandStack.pop(), operatorStack.pop(), operandStack.pop());
                                operandStack.push(operationResult);
                            } catch (NullPointerException npe) {
                                isExpValid = false;
                                break;
                            }
                        } while (!checkPrecedence(currentOperator, operatorStack.top()));
                        operatorStack.push(currentOperator);
                    }
                }
                else{
                    isExpValid = false;
                }
            }


            while (isExpValid) {
                if (operatorStack.top() == '@')
                    break;
                try {
                    float operationResult = performOperation(operandStack.pop(), operatorStack.pop(), operandStack.pop());
                    operandStack.push(operationResult);
                } catch (NullPointerException npe) {
                    isExpValid = false;
                }
            }

            System.out.println(isExpValid ? operandStack.pop() : "ERROR");
        }
    }

    private boolean isValidOperator(char symbol) {
        return symbol == '+' || symbol == '-' || symbol == '*' || symbol == '/';
    }


    // if current operator's precedence is higher than the popped one, returns true
    private boolean checkPrecedence(char currentOperator, char poppedOperator) {
        return getPrecedenceLevel(currentOperator) > getPrecedenceLevel(poppedOperator);
    }

    private int getPrecedenceLevel(char operator) {
        return switch (operator) {
            case '*', '/' -> 2;
            case '+', '-' -> 1;
            case '@' -> 0;
            default -> throw new IllegalArgumentException("Operator " + operator + " is undefined");
        };
    }

    private float performOperation(float operand1, char operator, float operand2) {
        return switch (operator) {
            case '*' -> operand2 * operand1;
            case '/' -> operand2 / operand1;
            case '+' -> operand2 + operand1;
            case '-' -> operand2 - operand1;
            default -> throw new IllegalArgumentException("Operator " + operator + " is undefined");
        };
    }

}
