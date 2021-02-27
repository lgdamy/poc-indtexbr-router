package com.tcc.listener;

import java.math.BigDecimal;

/**
 * @author lgdamy@raiadrogasil.com on 25/02/2021
 */
public class Orcamento {
    private Long id;

    private Long listing;

    private String company;

    private String companyDocument;

    private BigDecimal price;

    private Integer productionTime;

    private Integer shippingTime;

    private String proposalLink;

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Long getListing() {
        return listing;
    }

    public void setListing(Long listing) {
        this.listing = listing;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyDocument() {
        return companyDocument;
    }

    public void setCompanyDocument(String companyDocument) {
        this.companyDocument = companyDocument;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getProductionTime() {
        return productionTime;
    }

    public void setProductionTime(Integer productionTime) {
        this.productionTime = productionTime;
    }

    public Integer getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(Integer shippingTime) {
        this.shippingTime = shippingTime;
    }

    public String getProposalLink() {
        return proposalLink;
    }

    public void setProposalLink(String proposalLink) {
        this.proposalLink = proposalLink;
    }
}
