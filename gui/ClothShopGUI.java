package gui;


import entity.Product;
import fileio.ProductFileIO;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ClothShopGUI extends JFrame {

    private final ProductFileIO fileIO = new ProductFileIO();
    private JComboBox<String> sizeDropDown;
    private JTextField qtyField;
    private DefaultListModel<String> billModel;
    private JList<String> billList;
    private JLabel totalBillLabel;
    private JPanel cardPanel;
    private int finalTotalAmount = 0;
    private String selectedProductName = null;
    private JPanel selectedCard = null;

    private static final String[] SIZES = {
        "S", "M", "L", "XL", "XXL"
    };

    private JTextField tfId, tfName, tfPrice, tfStock, tfSearch;
    private DefaultTableModel tableModel;
    private JTable manageTable;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnShowAll;

    private static final String[] COLUMNS = {
        "Product ID", "Name", "Price (TK)", "Stock"
    };

    public ClothShopGUI(){
        setTitle("THREADORA OUTFIT - Professional Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 750);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("\uD83D\uDECD  Shop & Billing", buildShopTab());
        tabs.addTab("\uD83D\uDCE6 Manage Products", buildManageTab());
        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (tabs.getSelectedIndex() == 0) refreshCardPanel();
            }
        });
        setContentPane(tabs);
    }

    private JPanel buildShopTab(){
        JPanel bg = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                ImageIcon b = findImage("background.jpg");
                if (b != null) g.drawImage(b.getImage(), 0, 0, getWidth(), getHeight(), this);
                g.setColor(new Color(255, 255, 255, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JPanel bannerPanel = new JPanel(new BorderLayout());
        bannerPanel.setOpaque(false);
        bannerPanel.setPreferredSize(new Dimension(0, 180));
        JLabel bannerLabel = new JLabel("Welcome to THREADORA OUTFIT", SwingConstants.CENTER);
        ImageIcon bannerIcon = findImage("banner.jpg");
        if (bannerIcon != null){
            bannerLabel.setIcon(new ImageIcon(bannerIcon.getImage().getScaledInstance(1400, 180, Image.SCALE_SMOOTH)));
            bannerLabel.setText("");
        } else{
            bannerLabel.setFont(new Font("Serif", Font.BOLD, 60));
        }

        bannerPanel.add(bannerLabel, BorderLayout.CENTER);
        bg.add(bannerPanel, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        cardPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        cardPanel.setOpaque(false);
        refreshCardPanel();
        JScrollPane cardScroll = new JScrollPane(cardPanel);
        cardScroll.setOpaque(false);
        cardScroll.getViewport().setOpaque(false);
        cardScroll.setBorder(null);
        mainContent.add(cardScroll, BorderLayout.CENTER);
        mainContent.add(buildBillingPanel(), BorderLayout.EAST);
        bg.add(mainContent, BorderLayout.CENTER);
        return bg;
    }

    private JPanel buildBillingPanel(){
        JPanel right = new JPanel();
        right.setPreferredSize(new Dimension(450, 0));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(new Color(255, 255, 255, 200));
        right.setBorder(BorderFactory.createTitledBorder(" Billing & Invoice "));

        JPanel inputArea = new JPanel(new GridLayout(5, 2, 10, 10));
        inputArea.setOpaque(false);
        inputArea.add(new JLabel("Size:"));
        sizeDropDown = new JComboBox<>(SIZES);
        inputArea.add(sizeDropDown);
        inputArea.add(new JLabel("Qty:"));
        qtyField = new JTextField("1");
        inputArea.add(qtyField);

        final JButton addBtn = new JButton("Add to Cart");
        final JButton removeBtn = styledBtn("Remove Selected", new Color(231, 76, 60));
        final JButton confirmBtn = styledBtn("Confirm & Save",  new Color(46, 204, 113));
        inputArea.add(addBtn); 
        inputArea.add(removeBtn); 
        inputArea.add(confirmBtn);
        right.add(inputArea);
        right.add(Box.createRigidArea(new Dimension(0, 20)));

        billModel = new DefaultListModel<>();
        billList  = new JList<>(billModel);
        right.add(new JScrollPane(billList));

        totalBillLabel = new JLabel("Total Bill: 0 TK");
        totalBillLabel.setFont(new Font("Arial", Font.BOLD, 28));
        totalBillLabel.setForeground(Color.RED);
        right.add(totalBillLabel);

        addBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (selectedProductName == null){
                    JOptionPane.showMessageDialog(ClothShopGUI.this, "Please click a product card first to select it.");
                    return;
                }
                Product found = null;
                for (Product p : fileIO.readAll())
                    if (p.getName().equalsIgnoreCase(selectedProductName)){ 
                        found = p; break; 
                    }
                if (found == null){
                     JOptionPane.showMessageDialog(ClothShopGUI.this, "Selected product not found in database."); 
                     return; 
                    }

                String qtyText = qtyField.getText().trim();
                if (qtyText.isEmpty()){
                     JOptionPane.showMessageDialog(ClothShopGUI.this, "Quantity cannot be empty!");
                      return; 
                    }

                int q;

                try{ 
                    q = Integer.parseInt(qtyText); 
                }
                catch (NumberFormatException ex){ 
                    JOptionPane.showMessageDialog(ClothShopGUI.this, "Enter a valid quantity!"); 
                    return; 
                }
                if (q <= 0) {
                     JOptionPane.showMessageDialog(ClothShopGUI.this, "Quantity must be greater than 0!"); 
                     return; 
                    }
                if (q > found.getStock()) {
                    JOptionPane.showMessageDialog(ClothShopGUI.this, "Not enough stock! Only " + found.getStock() + " item(s) available.");
                    return;
                }
                int cost = found.getPrice() * q;
                finalTotalAmount += cost;
                billModel.addElement(String.format("%-15s (%s) x%d = %d TK", found.getName(), (String) sizeDropDown.getSelectedItem(), q, cost));
                totalBillLabel.setText("Total Bill: " + finalTotalAmount + " TK");
            }
        });

        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                int idx = billList.getSelectedIndex();
                if (idx == -1) return;
                if (JOptionPane.showConfirmDialog(ClothShopGUI.this, "Are you sure you want to remove this item?", "Confirm Remove", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                int price = Integer.parseInt(((String) billModel.get(idx)).split("=")[1].replace(" TK", "").trim());
                finalTotalAmount -= price;
                totalBillLabel.setText("Total Bill: " + finalTotalAmount + " TK");
                billModel.remove(idx);
            }
        });



        confirmBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (billModel.isEmpty()) return;
                StringBuilder inv = new StringBuilder();
                inv.append("\nDate: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n");
                for (int i = 0; i < billModel.size(); i++) inv.append(billModel.get(i)).append("\n");
                inv.append("Total: ").append(finalTotalAmount).append(" TK\n------------------\n");

                // Deduct stock for each sold item
                for (int i = 0; i < billModel.size(); i++) {
                    String line = (String) billModel.get(i);
                    // line format: "ProductName    (S) x3 = 4497 TK"
                    String productName = line.split("\\(")[0].trim();
                    int qty = Integer.parseInt(line.split("x")[1].split("=")[0].trim());
                    for (Product p : fileIO.readAll()) {
                        if (p.getName().equalsIgnoreCase(productName)) {
                            p.setStock(p.getStock() - qty);
                            fileIO.updateProduct(p);
                            break;
                        }
                    }
                }

                fileIO.saveInvoice(inv.toString());
                JOptionPane.showMessageDialog(ClothShopGUI.this, "Order Confirmed & Saved!");
                billModel.clear(); finalTotalAmount = 0;
                totalBillLabel.setText("Total Bill: 0 TK");
                refreshCardPanel();
            }
        });

        return right;
    }



    private void refreshCardPanel(){
        cardPanel.removeAll();
        selectedProductName = null; selectedCard = null;
        for (Product p : fileIO.readAll()){
            final Product fp   = p;
            final JPanel  card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setBackground(new Color(255, 255, 255, 180));
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel imgLabel = new JLabel("", SwingConstants.CENTER);
            ImageIcon icon = findImage(p.getName().toLowerCase() + ".jpg");
            if (icon != null) imgLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH)));
            else imgLabel.setText("[No Image]");
            card.add(imgLabel, BorderLayout.CENTER);

            card.add(new JLabel("<html><center>" + p.getName() + " (stock: " + p.getStock() + ")<br><font color='blue'>" + p.getPrice() + " TK</font></center></html>", 
            SwingConstants.CENTER), BorderLayout.SOUTH);
            
            card.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedCard != null) selectedCard.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    selectedCard = card; selectedProductName = fp.getName();
                    card.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 3));
                }
            });
            cardPanel.add(card);
        }
        cardPanel.revalidate(); cardPanel.repaint();
    }




    private JPanel buildManageTab(){
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        root.setBackground(new Color(240, 241, 248));

        // Left form
        JPanel outer = new JPanel(new BorderLayout());
        outer.setPreferredSize(new Dimension(280, 0));
        outer.setBackground(new Color(22, 33, 62));
        outer.setBorder(new MatteBorder(0, 0, 0, 2, new Color(232, 93, 4)));
        JLabel heading = new JLabel("  Product Details", SwingConstants.LEFT);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        heading.setForeground(new Color(232, 93, 4));
        heading.setPreferredSize(new Dimension(280, 38));
        heading.setBorder(new MatteBorder(0, 0, 1, 0, new Color(232, 93, 4)));
        JPanel fields = new JPanel(new GridBagLayout());
        fields.setOpaque(false);
        fields.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0;
        gc.insets = new Insets(5, 3, 5, 3); gc.gridwidth = 2;

        tfId = mField(); 
        tfId.setToolTipText("Exactly 8 digits required");
        tfName = mField(); 
        tfPrice = mField(); 
        tfStock = mField();

        String[] labels = {
            "Product ID *", "Product Name *", "Price (TK) *", "Stock *"
        };

        JTextField[] tfs = {
            tfId, tfName, tfPrice, tfStock
        };

        for (int i = 0; i < labels.length; i++) {
            gc.gridy = i * 2;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setForeground(new Color(200, 200, 220));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            fields.add(lbl, gc);
            gc.gridy = i * 2 + 1; fields.add(tfs[i], gc);
        }


        JPanel btns = new JPanel(new GridLayout(2, 2, 6, 6));
        btns.setOpaque(false);
        btns.setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
        btnAdd = mBtn("\u2795 Add", new Color(232, 93,   4));
        btnUpdate = mBtn("\u270F Update", new Color(41,  128, 185));
        btnDelete = mBtn("\uD83D\uDDD1 Delete", new Color(192, 57,  43));
        btnClear = mBtn("\u2716 Clear",new Color(100, 100, 100));
        btns.add(btnAdd); 
        btns.add(btnUpdate); 
        btns.add(btnDelete); 
        btns.add(btnClear);
        outer.add(heading, BorderLayout.NORTH);
        outer.add(fields,  BorderLayout.CENTER);
        outer.add(btns,    BorderLayout.SOUTH);


        // Right table + search
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        searchBar.setOpaque(false);
        JLabel lbl = new JLabel("\uD83D\uDD0D Search by ID or Name:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tfSearch = new JTextField();
        tfSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(232, 93, 4), 1, true), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        btnSearch = mBtn("Search", new Color(232, 93, 4));
        btnShowAll = mBtn("Show All", new Color(100, 100, 100));
        JPanel sbBtns = new JPanel(new GridLayout(1, 2, 6, 0));

        sbBtns.setOpaque(false); 
        sbBtns.add(btnSearch); 
        sbBtns.add(btnShowAll);
        searchBar.add(lbl, BorderLayout.WEST); 
        searchBar.add(tfSearch, BorderLayout.CENTER); 
        searchBar.add(sbBtns, BorderLayout.EAST);

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };


        manageTable = new JTable(tableModel);
        manageTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        manageTable.setRowHeight(30); manageTable.setGridColor(new Color(220, 220, 230));
        manageTable.setSelectionBackground(new Color(232, 93, 4, 55));
        manageTable.setShowVerticalLines(false);
        manageTable.getTableHeader().setReorderingAllowed(false);
        manageTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 250));
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        });

        JTableHeader header = manageTable.getTableHeader();
        header.setBackground(new Color(232, 93, 4)); header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 34));
        JScrollPane scroll = new JScrollPane(manageTable);
        scroll.setBorder(new LineBorder(new Color(210, 210, 220)));
        panel.add(searchBar, BorderLayout.NORTH); panel.add(scroll, BorderLayout.CENTER);

        root.add(outer, BorderLayout.WEST);
        root.add(panel, BorderLayout.CENTER);
        wireManageListeners();
        loadManageTable(fileIO.readAll());
        return root;
    }

    private void wireManageListeners(){
        manageTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && manageTable.getSelectedRow() != -1){
                    int r = manageTable.getSelectedRow();
                    tfId.setText(tableModel.getValueAt(r, 0).toString()); tfId.setEditable(false);
                    tfName.setText(tableModel.getValueAt(r, 1).toString());
                    tfPrice.setText(tableModel.getValueAt(r, 2).toString());
                    tfStock.setText(tableModel.getValueAt(r, 3).toString());
                }
            }
        });
        
        btnAdd.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (!validateForm()) return;
                String id = tfId.getText().trim();
                if (fileIO.idExists(id)) { 
                    err("Product ID " + id + " already exists.\nDuplicate IDs are not allowed."); 
                    return; 
                }
                if (fileIO.createProduct(formToProduct())){ 
                    loadManageTable(fileIO.readAll()); 
                    clearForm();
                    ok("Product added successfully!"); 
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (manageTable.getSelectedRow() == -1) { 
                    err("Please select a record to update."); 
                    return; 
                }
                if (!validateForm()) return;
                if (fileIO.updateProduct(formToProduct())) { 
                    loadManageTable(fileIO.readAll()); clearForm(); ok("Product updated successfully!"); 
                }
            }
        });

        btnDelete.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (manageTable.getSelectedRow() == -1) { 
                    err("Please select a record to delete."); 
                    return; 
                }
                int r = manageTable.getSelectedRow();
                String id = tableModel.getValueAt(r, 0).toString(), name = tableModel.getValueAt(r, 1).toString();
                if (JOptionPane.showConfirmDialog(ClothShopGUI.this, "Delete product?\n\n  ID: " + id + "\n  Name: " + name, "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
                if (fileIO.deleteProduct(id)) { 
                    loadManageTable(fileIO.readAll()); 
                    clearForm(); 
                    ok("Product deleted.");
                }
            }
        });

        btnClear.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { 
                clearForm(); 
            }
        });

        btnSearch.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String q = tfSearch.getText().trim();
                if (q.isEmpty()) { 
                    err("Enter an ID or name to search."); 
                    return; 
                }
                loadManageTable(fileIO.search(q));
            }
        });

        btnShowAll.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) { 
                tfSearch.setText(""); 
                loadManageTable(fileIO.readAll()); 
            }
        });

        tfSearch.addKeyListener(new KeyAdapter(){
            @Override public void keyPressed(KeyEvent e){
                 if (e.getKeyCode() == KeyEvent.VK_ENTER) btnSearch.doClick(); 
                }
        });
    }

    private boolean validateForm(){
        String id = tfId.getText().trim(), name = tfName.getText().trim(),
               price = tfPrice.getText().trim(), stock = tfStock.getText().trim();
        if (id.isEmpty() || name.isEmpty() || price.isEmpty() || stock.isEmpty()){ 
            err("All fields are required."); 
            return false;
         }
        if (!id.matches("\\d{8}")) { 
            err("Product ID must be exactly 8 digits.\nExample: 10000001"); 
            return false; 
        }
        try { 
            if (Integer.parseInt(price) < 0) 
                throw new NumberFormatException(); 
        }
        catch (NumberFormatException ex){ 
            err("Price must be a positive whole number."); 
            return false;
        }
        try { 
            if (Integer.parseInt(stock) < 0) throw new NumberFormatException(); 
        }
        catch (NumberFormatException ex){ 
            err("Stock must be a positive whole number."); 
            return false; 
        }
        return true;
    }

    private Product formToProduct(){
        return new Product(tfId.getText().trim(), tfName.getText().trim(),Integer.parseInt(tfPrice.getText().trim()),Integer.parseInt(tfStock.getText().trim()));
    }

    private void loadManageTable(Product[] list){
        tableModel.setRowCount(0);
        for (Product p : list)
            tableModel.addRow(new Object[]{
        p.getProductId(), p.getName(), p.getPrice(), p.getStock()
    });
    }

    private void clearForm(){
        tfId.setText(""); 
        tfId.setEditable(true);
        tfName.setText(""); 
        tfPrice.setText(""); 
        tfStock.setText("");
        manageTable.clearSelection();
    }

    private void err(String msg){ 
        JOptionPane.showMessageDialog(this, msg, "Validation Error", JOptionPane.ERROR_MESSAGE); 
    }
    private void ok(String msg){ 
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); 
    }

    private JTextField mField(){
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(60, 80, 120), 1, true), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        tf.setBackground(new Color(245, 246, 255));
        return tf;
    }

    private JButton mBtn(String text, final Color bg){

        JButton b = new JButton(text);

        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBackground(bg); 
        b.setForeground(Color.WHITE);
        b.setOpaque(true); 
        b.setBorderPainted(false); 
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            final Color n = bg, h = bg.brighter();
            @Override public void mouseEntered(MouseEvent e){ 
                b.setBackground(h); 
            }
            @Override public void mouseExited (MouseEvent e){ 
                b.setBackground(n); 
            }
        });
        
        return b;
    }

    private JButton styledBtn(String text, Color bg){
        JButton b = new JButton(text);
        b.setBackground(bg); 
        b.setForeground(Color.WHITE);
        b.setOpaque(true); 
        b.setBorderPainted(false);
        return b;
    }

    private ImageIcon findImage(String name){
        File f = new File(name.toLowerCase().endsWith(".jpg") ? name.toLowerCase() : name.toLowerCase() + ".jpg");
        return f.exists() ? new ImageIcon(f.getAbsolutePath()) : null;
    }
}