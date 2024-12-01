
package com.crio.warmup.stock.dto;
import java.util.Objects;

public class TotalReturnsDto implements Comparable<TotalReturnsDto>{ //implemented comparable

  private String symbol;
  private Double closingPrice;

  public TotalReturnsDto(String symbol, Double closingPrice) {
    this.symbol = symbol;
    this.closingPrice = closingPrice;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public Double getClosingPrice() {
    return closingPrice;
  }

  public void setClosingPrice(Double closingPrice) {
    this.closingPrice = closingPrice;
  }
//After implementing comparable we wrote this
  @Override
  public int compareTo(TotalReturnsDto otherReturn) {
    if(this.closingPrice.compareTo(otherReturn.closingPrice)> 0 ) return 1;
    else if(this.closingPrice.compareTo(otherReturn.closingPrice)< 0 ) return -1;
     return 0;
  }
}
