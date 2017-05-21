package com.michael.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Quote.
 */
@Entity
@Table(name = "quote")
public class Quote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quote_seq_no")
    private Long quoteSeqNo;

    @Column(name = "product_id")
    private String productId;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "line")
    private String line;

    @Column(name = "premium")
    private Double premium;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuoteSeqNo() {
        return quoteSeqNo;
    }

    public Quote quoteSeqNo(Long quoteSeqNo) {
        this.quoteSeqNo = quoteSeqNo;
        return this;
    }

    public void setQuoteSeqNo(Long quoteSeqNo) {
        this.quoteSeqNo = quoteSeqNo;
    }

    public String getProductId() {
        return productId;
    }

    public Quote productId(String productId) {
        this.productId = productId;
        return this;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public Quote productName(String productName) {
        this.productName = productName;
        return this;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLine() {
        return line;
    }

    public Quote line(String line) {
        this.line = line;
        return this;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public Double getPremium() {
        return premium;
    }

    public Quote premium(Double premium) {
        this.premium = premium;
        return this;
    }

    public void setPremium(Double premium) {
        this.premium = premium;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Quote quote = (Quote) o;
        if (quote.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, quote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Quote{" +
            "id=" + id +
            ", quoteSeqNo='" + quoteSeqNo + "'" +
            ", productId='" + productId + "'" +
            ", productName='" + productName + "'" +
            ", line='" + line + "'" +
            ", premium='" + premium + "'" +
            '}';
    }
}
