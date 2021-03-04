package com.example.mobilestore.product;

public class ProductCategory {

    public int CategoryID;
    public int CategoryImage;
    public String CategoryName;

    public ProductCategory(int _CategoryID, int _CategoryImage, String _CategoryName) {
        CategoryID = _CategoryID;
        CategoryImage = _CategoryImage;
        CategoryName = _CategoryName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public int getCategoryImage() {
        return CategoryImage;
    }
}
