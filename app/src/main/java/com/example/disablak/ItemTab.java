package com.example.disablak.skysofttaskone;

public class ItemTab {

    private int idItem;
    private String urlImage;

    public ItemTab(int idItem, String urlImage){
        this.idItem = idItem;
        this.urlImage = urlImage;
    }

    public int getIdItem() {
        return idItem;
    }

    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
