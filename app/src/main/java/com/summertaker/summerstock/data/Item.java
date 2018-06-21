package com.summertaker.summerstock.data;

public class Item {
    private int no;
    private String code;
    private String name;
    private int price;
    private int psp; // prediction stock price
    private String indt;
    private int adp;
    private float adr;
    private int volume;
    private float per;
    private float roe;
    private int ndr; // Number of days after Recommendation
    private float ror; // Rate of Return
    private int nor; // Number of Recommendations

    private String favorite;
    private String possession;

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getIndt() {
        return indt;
    }

    public void setIndt(String indt) {
        this.indt = indt;
    }

    public int getPsp() {
        return psp;
    }

    public void setPsp(int psp) {
        this.psp = psp;
    }

    public int getAdp() {
        return adp;
    }

    public void setAdp(int adp) {
        this.adp = adp;
    }

    public float getAdr() {
        return adr;
    }

    public void setAdr(float adr) {
        this.adr = adr;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public float getPer() {
        return per;
    }

    public void setPer(float per) {
        this.per = per;
    }

    public float getRoe() {
        return roe;
    }

    public void setRoe(float roe) {
        this.roe = roe;
    }

    public int getNdr() {
        return ndr;
    }

    public void setNdr(int ndr) {
        this.ndr = ndr;
    }

    public float getRor() {
        return ror;
    }

    public void setRor(float ror) {
        this.ror = ror;
    }

    public int getNor() {
        return nor;
    }

    public void setNor(int nor) {
        this.nor = nor;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getPossession() {
        return possession;
    }

    public void setPossession(String possession) {
        this.possession = possession;
    }
}
