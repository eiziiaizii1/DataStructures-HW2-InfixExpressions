package MyStack;

// This Singly Linked List contains only required methods for Stack
class SLL_For_Stack<T> {
    private static class Node<T>{
        private final T data;
        private Node<T> next;
        public Node(T data, Node<T> nextNode){
            this.data = data;
            next = nextNode;
        }

        public T getData(){
            return this.data;
        }
        public Node<T> getNext(){
            return this.next;
        }
        public void setNext(Node<T> n){
            this.next = n;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;
    SLL_For_Stack(){
        head = null;
        tail = null;
        size = 0;
    }

    public void addFirst(T data){
        Node<T> n = new Node<>(data,null);
        if(head == null){
            head = tail = n;
        }else{
            n.setNext(head);
            head = n;
        }
        size++;
    }

    public T removeFirst(){
        if(size == 0) return null;
        T temp = head.getData();
        head = head.getNext();
        size--;
        if(size == 0) tail = null;
        return temp;
    }

    public T getFirst(){
        return  head.getData();
    }
    boolean isEmpty(){
        return size == 0;
    }
    public int getSize(){
        return size;
    }
}