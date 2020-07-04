import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BasicCrawler extends WebCrawler {

    private static final Pattern FILTERS = Pattern.compile(
            ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
    private static final String HEADER = "URL, ";

    private final Map<String, List<Long>> linkHits = new HashMap<>();
    private final Map<String, Long> hintsAmount = new HashMap<>();
    private final List<String> urls = new ArrayList<>();

    private final CrawlerStatistics stats;

    public BasicCrawler(CrawlerStatistics stats) {
        this.stats = stats;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches();
    }

    @Override
    public void visit(Page page) {
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();
            String url = page.getWebURL().getURL();
            urls.add(url);

            List<String> text = Arrays.asList(parseData.getText().toLowerCase().trim().split("[^a-z-A-Z0-9]+"));
            Map<String, Long> frequencyOfWords = getHints(stats.getSearchWords(), text);
            List<Long> amountOfHints = new ArrayList<>(frequencyOfWords.values());

            System.out.printf("Count hits for words : %s %n", getHints(stats.getSearchWords(), text));
            System.out.printf("Sum of hints %d %n", getMaxHint(frequencyOfWords));

            hintsAmount.put(url, getMaxHint(frequencyOfWords));
            linkHits.put(url, amountOfHints);
        }
    }

    @Override
    public void onBeforeExit() {
        stats.setUrls(urls);
        stats.setWordsHint(linkHits);
        stats.setWordsHintAmount(hintsAmount);
    }

    public static void saveToCsv(Map<String, List<Long>> values, List<String> header, String fileName) throws IOException {
        try (FileWriter csvWrite = new FileWriter(fileName)) {
            csvWrite.append(HEADER)
                    .append(header.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(", ")))
                    .append("\n");
            for (Map.Entry<String, List<Long>> entry : values.entrySet()) {
                csvWrite.append(entry.getKey())
                        .append(", ")
                        .append(entry.getValue().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")))
                        .append("\n");
            }
            csvWrite.flush();
        } catch (IOException e) {
            System.out.printf("Exception during writing file to csv!", e);
        }
    }

    public Map<String, Long> getHints(List<String> words, List<String> text) {
        Map<String, Long> result = text.stream()
                .filter(words::contains)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for (String word : words) {
            if (!result.containsKey(word)) result.put(word, 0L);
        }

        return result;
    }

    public Long getMaxHint(Map<String, Long> hintsOfWords) {
        return hintsOfWords.values()
                .stream()
                .mapToLong(Long::longValue)
                .sum();
    }

    public static Map<String, Long> sortHintsMap(Map<String, Long> hintsAmount) {

        return hintsAmount.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .collect(
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new)
                );
    }

    public static Map<String, List<Long>> prettyOutPut(Map<String, Long> sortedMap,
                                                       Map<String, List<Long>> notSortedMap) {
        Map<String, List<Long>> results = new LinkedHashMap<>();

        for (String sortedKeys : sortedMap.keySet())
            for (Map.Entry<String, List<Long>> notSorted : notSortedMap.entrySet()) {
                if (notSorted.getKey().equals(sortedKeys)) {
                    results.put(notSorted.getKey(), notSorted.getValue());
                }
            }

        return results;
    }
}
