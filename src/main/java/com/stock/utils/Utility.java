package com.stock.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.stock.data.UserPosition;
import com.stock.model.WatchSymbol;

public class Utility {

  // Formatter for LocalDate: Date and Time
  protected DateTimeFormatter dateTimeFormatter;
  // Formatter for LocalDate: Date only
  protected DateTimeFormatter dateFormatter;

  public DateTimeFormatter getDateTimeFormatter() {
    if (dateTimeFormatter == null) {
      dateTimeFormatter = DateTimeFormatter.ofPattern(StringUtility.DF_YYYYMMDD_HHMMSS);
    }
    return dateTimeFormatter;
  }

  /**
   * Getting a watch list
   *
   * @return
   */
  public static List<WatchSymbol> readWatchList() {
    List<WatchSymbol> r = new ArrayList<>();
    String fileName =
        "C:\\AV\\WorkProjects\\DividendOptimizer\\Optimizer\\src\\main\\resources\\WatchList.csv";

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {

      String line;
      int i = 0;
      while ((line = br.readLine()) != null) {
        // System.out.println(line);
        i++;
        if (i == 1) {
          continue;
        }

        WatchSymbol ws = new WatchSymbol();
        String[] data = line.split(",");

        ws.setSymbol(data[0]);

        // convert quarterly dividend into BigDecimal
        BigDecimal qd = new BigDecimal(0);
        if (data[1].length() > 0) {
          qd = new BigDecimal(data[1]);
        }
        ws.setQuoterlyDividendAmount(qd);

        // UpperYield
        BigDecimal uy = new BigDecimal(0);
        if (data[2].length() > 0) {
          uy = new BigDecimal(data[2]);
        }
        ws.setUpperYield(uy);

        // LowerYield
        BigDecimal ly = new BigDecimal(0);
        if (data[3].length() > 0) {
          ly = new BigDecimal(data[3]);
        }
        ws.setLowerYield(ly);

        r.add(ws);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return r;
  }

  /**
   * Getting current positions
   *
   * @return
   */
  public static List<UserPosition> getCurrentPositions() {
    List<UserPosition> r = new ArrayList<>();
    // r.add(new UserPosition("CM.TO", 300));
    // r.add(new CurrentPosition("BMO.TO", 200));
    // r.add(new UserPosition("BNS.TO", 730));
    // r.add(new CurrentPosition("TD.TO", 250));
    // r.add(new UserPosition("SU.TO", 300));
    // r.add(new UserPosition("ENB.TO", 600));
    // r.add(new CurrentPosition("SHOP.TO", 172));
    // r.add(new CurrentPosition("IAG.TO", 160));
    // r.add(new CurrentPosition("RY.TO", 250));
    // r.add(new UserPosition("BCE.TO", 600));
    return r;
  }

  public static BigDecimal getAvailableCash() {
    return BigDecimal.valueOf(385);
  }

  public static BigDecimal getInvestedAmount() {
    return BigDecimal.valueOf(140000); // 187044
  }
}
