package com.example.entregable1.paypalrest;

import java.util.List;

public class PaymentResponse {
    private String id;
    private String state;
    private List<Link> links;

    public PaymentResponse(String id, String state, List<Link> links) {
        this.id = id;
        this.state = state;
        this.links = links;
    }
    public PaymentResponse() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public static class Link {
        private String href;
        private String rel;
        private String method;

        public Link(String href, String rel, String method) {
            this.href = href;
            this.rel = rel;
            this.method = method;
        }

        public Link() {
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getRel() {
            return rel;
        }

        public void setRel(String rel) {
            this.rel = rel;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}