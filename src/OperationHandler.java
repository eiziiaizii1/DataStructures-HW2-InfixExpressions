// Aziz Önder - 22050141021

import MyStack.Stack;
import MyStack.StackSLL;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class OperationHandler {
    OperationHandler(String pathName) {
        File inputFile = new File(pathName);
        try {
            processFile(inputFile);
        } catch (FileNotFoundException fnfe) {
            System.out.println("File is not found --> " + fnfe);
        }catch (RuntimeException re){
            System.out.println("An error occurred in Runtime --> " + re);
        }
    }

    private void processFile(File inputFile) throws FileNotFoundException{
        Scanner sc;
        try {
            sc = new Scanner(inputFile);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        }
        Stack<Character> operatorStack;
        Stack<Float> operandStack;

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
                    i = handleNumericCharacters(i,lineLength,line,operandStack);
                }
                // Checks symbol, determines what do next
                else if (isValidOperator(line.charAt(i))) {
                    if (i == 0 || i == lineLength - 1) {
                        errorExplanation = "ERROR: There can't be an operator at the end or at the beginning of the expression, except for '-' at the beginning!!!";
                        isExpValid = false;
                        break;
                    }
                    // Repeats until currentSymbol becomes less than the popped one (Instead of popping and pushing I used top, then popped if necessary)
                    char currentOperator = line.charAt(i);
                    if (checkPrecedence(currentOperator, operatorStack.top())) {
                        // Pushes the current operator
                        operatorStack.push(currentOperator);
                    } else {
                        // Pops 2 operands, perform operation, push result to operand stack, repeat until symbol becomes less than the popped one
                        do {
                            try {
                                float operationResult = performOperation(operandStack.pop(), operatorStack.pop(), operandStack.pop());
                                operandStack.push(operationResult);
                            } catch (NullPointerException npe) {
                                errorExplanation = "ERROR: Consecutive operators are invalid!!!";
                                isExpValid = false;
                                break;
                            }
                        } while (!checkPrecedence(currentOperator, operatorStack.top()));
                        operatorStack.push(currentOperator);
                    }
                } else {
                    errorExplanation = "ERROR: Undefined character";
                    isExpValid = false;
                    break;
                }
            }

            // (# of operands) - (# of operators) must be equal to 1
            if (operatorStack.getSize() != operandStack.getSize() && isExpValid) {
                errorExplanation = "ERROR: (# of operands) and (# of operators) does not match ";
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

    private int handleNumericCharacters (int i, int lineLength, String line, Stack<Float> operandStack){
        int numIndex = i + 1;
        while (numIndex < lineLength && Character.isDigit(line.charAt(numIndex)))
            numIndex++;

        // Checks whether the first encountered number in the expression is negative, based on that adds negative value  to the stack
        if (line.charAt(0) == '-' && operandStack.getSize() == 0)
            operandStack.push(-Float.parseFloat((line.substring(i, numIndex))));
        else
            operandStack.push(Float.parseFloat(line.substring(i, numIndex)));

        return numIndex - 1;
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