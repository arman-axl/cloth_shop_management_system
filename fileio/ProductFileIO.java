package fileio;

import entity.Product;
import java.io.*;

public class ProductFileIO {
    private static final String DB_PATH = "database.txt";
    private static final String BILLING_PATH = "billing_history.txt";

    public ProductFileIO() {

        ensureFile(DB_PATH);
        ensureFile(BILLING_PATH);

        if (readAll().length == 0) {
            createProduct(new Product("10000001", "Shirt", 1499,50));
            createProduct(new Product("10000002","Pant", 1699, 50));
            createProduct(new Product("10000003","T-Shirt", 1199,50));
            createProduct(new Product("10000004", "Jersey",2499, 50));
            createProduct(new Product("10000005","Hoodie",2199, 50));
            createProduct(new Product("10000006", "Jacket", 2999, 50));
            createProduct(new Product("10000007","Socks", 599, 50));
            createProduct(new Product("10000008", "Trouser",999, 50));
        }
    }

    public boolean createProduct(Product product) {
        
        try (BufferedWriter w = new BufferedWriter(new FileWriter(DB_PATH, true))) {
            w.write(product.toString()); 
            w.newLine(); 
            return true;

        } catch (IOException e) { 
            System.err.println("Create error: " + e.getMessage()); 
            return false; 
        }
    }

    public Product[] readAll() {
        Product[] temp = new Product[1000];
        int count = 0;

        try (BufferedReader r = new BufferedReader(new FileReader(DB_PATH))) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Product p = Product.fromString(line);
                    if (p != null) temp[count++] = p;
                }
            }

        } catch (IOException e) {}
        Product[] result = new Product[count];
        for (int i = 0; i < count; i++) result[i] = temp[i];
        return result;
    }

    public boolean updateProduct(Product updated) {
        Product[] all = readAll();
        boolean found = false;
        for (int i = 0; i < all.length; i++) {
            if (all[i].getProductId().equals(updated.getProductId())) {
                all[i] = updated; 
                found = true; 
                break;
            }
        }
        return found && writeAll(all);
    }

    public boolean deleteProduct(String productId){
        Product[] all = readAll();
        Product[] temp = new Product[all.length];
        int count = 0;
        boolean removed = false;
        for (int i = 0; i < all.length; i++) {
            if (all[i].getProductId().equals(productId)) 
                removed = true;
            else 
                temp[count++] = all[i];
        }
        Product[] filtered = new Product[count];
        for (int i = 0; i < count; i++) 
            filtered[i] = temp[i];
        return removed && writeAll(filtered);
    }

    public Product[] search(String query){
        Product[] all = readAll();
        Product[] temp = new Product[all.length];
        int count = 0;
        String lower = query.toLowerCase().trim();
        for (int i = 0; i < all.length; i++){
            if (all[i].getProductId().toLowerCase().contains(lower) || all[i].getName().toLowerCase().contains(lower)) temp[count++] = all[i];
        }
        Product[] results = new Product[count];
        for (int i = 0; i < count; i++) results[i] = temp[i];
        return results;
    }

    public boolean idExists(String productId){
        for (Product p : readAll())
            if (p.getProductId().equals(productId)) return true;
        return false;
    }

    public void saveInvoice(String text) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(BILLING_PATH, true))){
            w.write(text);
        } catch (IOException e) { 
            System.err.println("Billing save error: " + e.getMessage()); 
        }
    }

    private boolean writeAll(Product[] products) {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(DB_PATH, false))) {
            for (int i = 0; i < products.length; i++) { w.write(products[i].toString()); w.newLine(); 
            }
            return true;
        } catch (IOException e) { 
            System.err.println("Write error: " + e.getMessage()); 
            return false; 
        }
    }

    private void ensureFile(String path) {
        File f = new File(path);
        if (!f.exists()) try { 
            f.createNewFile(); 
        }
        catch (IOException e) { 
            System.err.println("Cannot create " + path); 

        }
    }
}
