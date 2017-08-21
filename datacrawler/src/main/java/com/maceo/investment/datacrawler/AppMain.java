package com.maceo.investment.datacrawler;

import com.maceo.investment.datacrawler.batch.KRStockCodeCrawler;
import com.maceo.investment.datacrawler.batch.KRStockDailyDataCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = {
        "com.maceo.investment.datacrawler.batch",
})
public class AppMain implements CommandLineRunner {

    @Autowired
    private KRStockCodeCrawler krStockCodeCrawler;

    @Autowired
    private KRStockDailyDataCrawler krStockDailyDataCrawler;

    @Override
    public void run(String... args) throws Exception {
        if(args.length == 0) {
            System.out.println("No arguments were provided. Ex)datacrawler.jar code daily");
        }

        for(int i = 0; i < args.length; i++) {
            String arg = args[i];
            if(arg.equals("code")) {
                krStockCodeCrawler.run();
            } else if(arg.equals("daily")) {
                krStockDailyDataCrawler.run();
            } else {
                throw new IllegalArgumentException("Illegal command line argument " + arg + ". It supports code, daily for now");
            }
        }

    }

    public static void main(String... args) throws Exception {
        SpringApplication.run(AppMain.class, args);
    }
}
