package com.example.entregable1.paypalrest;

import java.util.List;

public class CreatePaymentRequest {
    private String intent;
    private Payer payer;
    private List<Transaction> transactions;
    private RedirectUrls redirect_urls;

    public CreatePaymentRequest(String intent, Payer payer, List<Transaction> transactions, RedirectUrls redirect_urls) {
        this.intent = intent;
        this.payer = payer;
        this.transactions = transactions;
        this.redirect_urls = redirect_urls;
    }
    public CreatePaymentRequest() {
    }

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


    public static class Payer {
        private String payment_method;

        public Payer(String payment_method) {
            this.payment_method = payment_method;
        }
        public Payer() {
        }

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

        public Transaction(Amount amount, String description) {
            this.amount = amount;
            this.description = description;
        }
        public Transaction() {

        }

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
    }

    public static class Amount {
        private String currency;
        private String total;

        public Amount(String currency, String total) {
            this.currency = currency;
            this.total = total;
        }
        public Amount() {

        }

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
    }

    public static class RedirectUrls {
        private String return_url;
        private String cancel_url;

        public RedirectUrls(String return_url, String cancel_url) {
            this.return_url = return_url;
            this.cancel_url = cancel_url;
        }
        public RedirectUrls() {

        }

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
