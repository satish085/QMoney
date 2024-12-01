
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.utils.AnnualizedReturnComparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

  private static String token = "3d6a61a662b484b62b2dc1c5f68b8a9c8c46a7dd";

   public static String getToken(){
       return token;
   }



private RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  private Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
 }


 private Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        String url=buildUri(symbol, from, to);
    TiingoCandle[] candles = restTemplate.getForObject(url, TiingoCandle[].class);
    return List.of(candles);
    
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String url= "https://api.tiingo.com/tiingo/daily/"+ symbol+"/prices?startDate=" +startDate + "&endDate=" + endDate+ "&token=" + token;
    System.out.println("Url :" + url);
    return url;
  }

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        Double buyValue= buyPrice * trade.getQuantity();
        Double sellValue= sellPrice * trade.getQuantity();
        Double totalReturn= (sellValue-buyValue) / buyValue ;

       long totalNoDays= ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
       double totalNoYears=(double) totalNoDays / 365 ;

       Double annualReturn = Math.pow( 1 + totalReturn, (double) 1 / totalNoYears) - 1;
       
      return new AnnualizedReturn(trade.getSymbol(), annualReturn, totalReturn);
  }


  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) {
        List<AnnualizedReturn> annualizedReturns= new ArrayList<>();
        for(PortfolioTrade trade : portfolioTrades){
          try {
            List<Candle> candles = getStockQuote(trade.getSymbol(),trade.getPurchaseDate(), endDate);
            AnnualizedReturn annualizedReturn= calculateAnnualizedReturns(endDate, trade, getOpeningPriceOnStartDate(candles), getClosingPriceOnEndDate(candles));
            annualizedReturns.add(annualizedReturn);
          } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
         
         Collections.sort(annualizedReturns,new AnnualizedReturnComparator());
        }
     return annualizedReturns;
  }
}
