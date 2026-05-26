package model;

// Generic CartItem class — Week 7: Generics
public class CartItem<T> {
    private T item;
    private int quantity;

    public CartItem(T item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public T getItem() { return item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
