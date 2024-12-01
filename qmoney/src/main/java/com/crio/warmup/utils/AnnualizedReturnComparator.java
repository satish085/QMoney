package com.crio.warmup.utils;

import java.util.Comparator;
import com.crio.warmup.stock.dto.AnnualizedReturn;

public class AnnualizedReturnComparator implements Comparator <AnnualizedReturn>{

    @Override
    public int compare(AnnualizedReturn stock1, AnnualizedReturn stock2) {

       if(stock1.getAnnualizedReturn().compareTo(stock2.getAnnualizedReturn())<0) return 1;
       else if(stock1.getAnnualizedReturn().compareTo(stock2.getAnnualizedReturn())>0) return -1;
        return 0;

    }
}