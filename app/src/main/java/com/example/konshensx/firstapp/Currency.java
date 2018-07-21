package com.example.konshensx.firstapp;

/**
 * Represents a crypto currency
 */
public class Currency {

    int id;
    int rank;

    double circulating_supply;
    double total_supply;
    double max_supply;

    String name;
    String symbol;
    String website_slug;

    Quotes quotes;

    public Currency() {}

    public Currency(int id, String name, String symbol, String website_slug, int rank, double circulating_supply, double total_supply, double max_supply, Quotes quotes)
    {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.website_slug = website_slug;
        this.rank = rank;
        this.circulating_supply = circulating_supply;
        this.total_supply = total_supply;
        this.max_supply = max_supply;
        this.quotes = quotes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public double getCirculating_supply() {
        return circulating_supply;
    }

    public void setCirculating_supply(double circulating_supply) {
        this.circulating_supply = circulating_supply;
    }

    public double getTotal_supply() {
        return total_supply;
    }

    public void setTotal_supply(double total_supply) {
        this.total_supply = total_supply;
    }

    public double getMax_supply() {
        return max_supply;
    }

    public void setMax_supply(double max_supply) {
        this.max_supply = max_supply;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getWebsite_slug() {
        return website_slug;
    }

    public void setWebsite_slug(String website_slug) {
        this.website_slug = website_slug;
    }

    public Quotes getQuotes() {
        return quotes;
    }

    public void setQuotes(Quotes quotes) {
        this.quotes = quotes;
    }


}
