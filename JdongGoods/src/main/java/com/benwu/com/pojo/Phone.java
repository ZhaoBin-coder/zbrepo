package com.benwu.com.pojo;

public class Phone {
   private Long id;
   private String name;
   private Double price;
   private String shop;

    public Long getId() {
        return id;
    }

    public void setId(String id) {
        this.id =  Long.parseLong(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = Double.parseDouble(price);
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", shop='" + shop + '\'' +
                '}';
    }
}
