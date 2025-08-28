package com.project.code.Model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PlaceOrderRequestDTO {
    private Long storeId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String datetime;
    private List<PurchaseProductDTO> purchaseProduct;
    private Double totalPrice;

    public PlaceOrderRequestDTO(Long storeId, String customerName, String customerEmail, String customerPhone,
            String datetime, List<PurchaseProductDTO> purchaseProduct) {
        this.storeId = storeId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.datetime = datetime;
        this.purchaseProduct = purchaseProduct;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public List<PurchaseProductDTO> getPurchaseProduct() {
        return purchaseProduct;
    }

    public void setPurchaseProduct(List<PurchaseProductDTO> purchaseProduct) {
        this.purchaseProduct = purchaseProduct;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
