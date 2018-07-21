package com.example.konshensx.firstapp;

import java.math.BigDecimal;

public class Quotes {

    // maybe add the remaining fields later
    private double price;
    private BigDecimal volume;

    private BigDecimal marketCap;
    private double percentChange1H;
    private double percentChange24H;
    private double percentChange7D;

    public double getPercentChange1H() {
        return percentChange1H;
    }

    public void setPercentChange1H(double percentChange1H) {
        this.percentChange1H = percentChange1H;
    }

    public double getPercentChange24H() {
        return percentChange24H;
    }

    public void setPercentChange24H(double percentChange24H) {
        this.percentChange24H = percentChange24H;
    }

    public double getPercentChange7D() {
        return percentChange7D;
    }

    public void setPercentChange7D(double percentChange7D) {
        this.percentChange7D = percentChange7D;
    }

    public Quotes() {}

    public Quotes(double price, BigDecimal volume24, BigDecimal marketCap, double percentChange1H, double percentChange24H, double percentChange7D)
    {
        this.price = price;
        this.volume = volume24;
        this.marketCap = marketCap;
        this.percentChange1H = percentChange1H;
        this.percentChange24H = percentChange24H;
        this.percentChange7D = percentChange7D;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getMarketCap() {
        return marketCap;
    }

    public void setMarketCap(BigDecimal marketCap) {
        this.marketCap = marketCap;
    }
}
