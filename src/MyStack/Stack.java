// Aziz Önder - 22050141021
package MyStack;

public interface Stack<T> {
    public void push(T data);
    public T pop();
    public T top();
    public boolean isEmpty();
    public int getSize();
}