import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CrawlerController {

    private static final String ROOT_FOLDER = "src\\main\\resources\\crawlerStat\\";
    private static final String GENERAL_FILENAME = "general.csv";
    private static final String TOP_VALUES_FILENAME = "top.csv";
    private static final int NUMBER_OF_CRAWLERS = 2;

    public static void main(String[] args) throws Exception {

        CrawlConfig crawlConfig = new CrawlConfig();
        CrawlerStatistics stats = new CrawlerStatistics();
        List<String> values = new ArrayList<>();

        if (args.length < 3) {
            System.out.println("Check ur input!");
            System.out.println("\t First parameter is the provided to crawl link!");
            System.out.println("\t The second parameter is the max visited pages!");
            System.out.println("\t The third parameter is the link depth!");
            System.exit(0);
        } else {
            try {
                stats.setCrawlerLink(args[0]);
                stats.setMaxPage(Integer.parseInt(args[1]));
                stats.setDepth(Integer.parseInt(args[2]));
                for (int i = 3; i < args.length; i++) {
                    if (args[i] != null) {
                        values.add(args[i]);
                    }
                }
                stats.setSearchWords(values);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[1] + args[2] + " must be an int!");
                System.exit(0);
            }
        }

        crawlConfig.setMaxPagesToFetch(stats.getMaxPage());
        crawlConfig.setMaxDepthOfCrawling(stats.getDepth());
        crawlConfig.setCrawlStorageFolder(ROOT_FOLDER);

        PageFetcher pageFetcher = new PageFetcher(crawlConfig);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(crawlConfig, pageFetcher, robotstxtServer);
        controller.addSeed(stats.getCrawlerLink());

        CrawlController.WebCrawlerFactory<BasicCrawler> factory = () -> new BasicCrawler(stats);
        controller.start(factory, NUMBER_OF_CRAWLERS);

        BasicCrawler.sortHintsMap(stats.getWordsHintAmount());
        Map<String, List<Long>> expectedMap =
                BasicCrawler.prettyOutPut(BasicCrawler.sortHintsMap(stats.getWordsHintAmount()), stats.getWordsHint());
        System.out.println("TOP 10 PAGES BY TOTAL HITS");
        expectedMap.forEach((k, v) -> System.out.println((k + " - " + v)));
        BasicCrawler.saveToCsv(stats.getWordsHint(), stats.getSearchWords(), GENERAL_FILENAME);
        BasicCrawler.saveToCsv(expectedMap, stats.getSearchWords(), TOP_VALUES_FILENAME);
    }
}
