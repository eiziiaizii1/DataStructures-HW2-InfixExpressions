import MyStack.StackSLL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OperationHandler {
    OperationHandler(String pathName) {
        File inputFile = new File(pathName);
        try {
            processFile(inputFile);
        } catch (Exception e) {
            System.out.println("File not found");
        }
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
            String line = sc.nextLine().trim();

            operatorStack = new StackSLL<>();
            operandStack = new StackSLL<>();
            operatorStack.push('@');  //'@' dummy operator

            int lineLength = line.length();
            if (line.isBlank()) {
                continue;
            }
            boolean isExpValid = true;
            String errorExplanation = ""; // Holds the relevant error message
            for (int i = 0; i < lineLength && isExpValid; i++) {
                // Skips the spaces and first '-' character at the beginning
                if (line.charAt(i) == ' ' || (i == 0 && line.charAt(i) == '-')) {
                    continue;
                }
                // Checks the characters, if it is numeric value adds it to operandStack
                if (Character.isDigit(line.charAt(i))) {
                    int numIndex = i + 1;
                    while (numIndex < lineLength && Character.isDigit(line.charAt(numIndex)))
                        numIndex++;

                    // Checks whether the first operand of the expression is negative, based on that adds to the stack
                    if (line.charAt(0) == '-' && operandStack.getSize() == 0)
                        operandStack.push(Float.parseFloat(("-" + line.substring(i, numIndex))));
                    else
                        operandStack.push(Float.parseFloat(line.substring(i, numIndex)));
                    i = numIndex - 1;
                }
                // Checks symbol, determines what do next
                else if (isValidOperator(line.charAt(i))) {
                    if (i == 0 || i == lineLength - 1) {
                        errorExplanation = "There can't be an operator at the end or at the beginning of the expression, except for '-' at the beginning!!!";
                        isExpValid = false;
                        break;
                    }
                    // Repeat until currentSymbol becomes less than the popped one (Instead of popping and pushing I used top, then popped if necessary)
                    char currentOperator = line.charAt(i);
                    if (checkPrecedence(currentOperator, operatorStack.top())) {
                        // Push the current operator
                        operatorStack.push(currentOperator);
                    } else {
                        // Pop 2 operands, perform operation, push result to operand stack, repeat until symbol becomes less than the popped one
                        do {
                            try {
                                float operationResult = performOperation(operandStack.pop(), operatorStack.pop(), operandStack.pop());
                                operandStack.push(operationResult);
                            } catch (NullPointerException npe) {
                                errorExplanation = "Consecutive operators are invalid!!!";
                                isExpValid = false;
                                break;
                            }
                        } while (!checkPrecedence(currentOperator, operatorStack.top()));
                        operatorStack.push(currentOperator);
                    }
                } else {
                    errorExplanation = "Undefined character";
                    isExpValid = false;
                    break;
                }
            }

            // (# of operands) - (# of operators) must be equal to 1
            if (operatorStack.getSize() != operandStack.getSize() && isExpValid) {
                errorExplanation = "(# of operands) and (# of operators) does not match ";
                isExpValid = false;
            }

            // Performs the operations remained in the stack
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

            System.out.print(line + " = ");
            System.out.println(isExpValid ? operandStack.pop() : errorExplanation);
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