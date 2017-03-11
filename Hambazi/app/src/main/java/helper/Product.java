package helper;

public class Product {
    public String uid;
    public String name;
    public String image;
    public String price;
    public String price_off;

    public Product(String uid, String name, String image, String price, String price_off) {
        this.uid = uid;
        this.name = name;
        this.image = image;
        this.price = price;
        this.price_off = price_off;
    }
}