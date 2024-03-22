// Aziz Önder - 22050141021
package MyStack;

public class StackSLL<T> implements Stack<T>{
    SLL_For_Stack<T> sll = new SLL_For_Stack<T>();
    @Override
    public void push(T data) {
        sll.addFirst(data);
    }
    @Override
    public T pop() {
        return sll.removeFirst();
    }
    @Override
    public T top() {
        return sll.getFirst();
    }
    @Override
    public boolean isEmpty() {
        return sll.isEmpty();
    }
    @Override
    public int getSize() {
        return sll.getSize();
    }
}