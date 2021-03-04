package com.example.mobilestore.product;

import java.util.List;

public class Product {
    public int ProductID, Price, CategoryID, Quantity;
    public String MainImage, ProductName, ProductDescription;
    public List<String> Images;
    private Boolean isSaved;

    public Product(int productID, int categoryID, String mainImage, List<String> images, String productName, String productDescription, int price, Boolean saved) {
        ProductID = productID;
        CategoryID = categoryID;
        MainImage = mainImage;
        Images = images;
        ProductName = productName;
        ProductDescription = productDescription;
        Price = price;
        isSaved = saved;
        Quantity = 1;
    }

    public List<String> getImages() {
        return Images;
    }

    public int getProductID() {
        return ProductID;
    }

    public String getProductName() {
        return ProductName;
    }

    public int getProductPrice() {
        return Price;
    }

    public void setIsSaved() {
        isSaved = true;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public String getMainImage() {
        return MainImage;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int count) {
        this.Quantity = count;
    }
}

