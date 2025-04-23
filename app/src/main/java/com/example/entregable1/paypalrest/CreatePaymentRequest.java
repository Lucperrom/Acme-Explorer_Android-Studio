package com.example.entregable1.paypalrest;

import java.util.List;

public class CreatePaymentRequest {
    private String intent;
    private Payer payer;
    private List<Transaction> transactions;
    private RedirectUrls redirect_urls;

    // Getters and Setters
    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public Payer getPayer() {
        return payer;
    }

    public void setPayer(Payer payer) {
        this.payer = payer;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public RedirectUrls getRedirect_urls() {
        return redirect_urls;
    }

    public void setRedirect_urls(RedirectUrls redirect_urls) {
        this.redirect_urls = redirect_urls;
    }

    // Nested classes
    public static class Payer {
        private String payment_method;

        public String getPayment_method() {
            return payment_method;
        }

        public void setPayment_method(String payment_method) {
            this.payment_method = payment_method;
        }
    }

    public static class Transaction {
        private Amount amount;
        private String description;
        private ItemList item_list;

        public Amount getAmount() {
            return amount;
        }

        public void setAmount(Amount amount) {
            this.amount = amount;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public ItemList getItem_list() {
            return item_list;
        }

        public void setItem_list(ItemList item_list) {
            this.item_list = item_list;
        }
    }

    public static class Amount {
        private String currency;
        private String total;
        private Details details;

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public Details getDetails() {
            return details;
        }

        public void setDetails(Details details) {
            this.details = details;
        }
    }

    public static class Details {
        private String subtotal;
        private String shipping;
        private String tax;

        public String getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(String subtotal) {
            this.subtotal = subtotal;
        }

        public String getShipping() {
            return shipping;
        }

        public void setShipping(String shipping) {
            this.shipping = shipping;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }
    }

    public static class ItemList {
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }

    public static class Item {
        private String name;
        private String quantity;
        private String price;
        private String currency;
        private String sku;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }
    }

    public static class RedirectUrls {
        private String return_url;
        private String cancel_url;

        public String getReturn_url() {
            return return_url;
        }

        public void setReturn_url(String return_url) {
            this.return_url = return_url;
        }

        public String getCancel_url() {
            return cancel_url;
        }

        public void setCancel_url(String cancel_url) {
            this.cancel_url = cancel_url;
        }
    }
}