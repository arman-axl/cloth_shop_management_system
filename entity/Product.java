package entity;

public class Product {
    private String productId;
    private String name;
    private int price;
    private int stock;

    public Product(){

    }

    public Product(String productId, String name, int price, int stock){
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public void setProductId(String productId){ 
        this.productId = productId; 
    }
    public void setName(String name){ 
        this.name = name; 
    }
    public void setPrice(int price){ 
        this.price = price; 
    }
    public void setStock(int stock){
         this.stock = stock; 
        }

    public String getProductId(){ 
        return productId; 
    }
    public String getName(){ 
        return name; 
    }
    public int    getPrice(){ 
        return price; 
    }
    public int    getStock(){ 
        return stock; 
    }

    @Override
    public String toString() {
        return productId + "|" + name + "|" + price + "|" + stock;
    }

    public static Product fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 4) 
            return null;
        try {
            return new Product(p[0].trim(), p[1].trim(),Integer.parseInt(p[2].trim()),Integer.parseInt(p[3].trim()));
        } catch (NumberFormatException e) {
            System.out.println("Invalid product data: " + line);
            return null;
        }
    }
}
