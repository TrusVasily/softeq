import java.util.List;
import java.util.Map;

public class CrawlerStatistics {

    private List<String> urls;
    private List<String> searchWords;
    private int depth;
    private int maxPage;
    private String crawlerLink;
    private Map<String, List<Long>> wordsHint;
    private Map<String, Long> wordsHintAmount;

    public Map<String, Long> getWordsHintAmount() {
        return wordsHintAmount;
    }

    public void setWordsHintAmount(Map<String, Long> wordsHintAmount) {
        this.wordsHintAmount = wordsHintAmount;
    }

    public Map<String, List<Long>> getWordsHint() {
        return wordsHint;
    }

    public void setWordsHint(Map<String, List<Long>> wordsHint) {
        this.wordsHint = wordsHint;
    }

    public List<String> getSearchWords() {
        return searchWords;
    }

    public void setSearchWords(List<String> searchWords) {
        this.searchWords = searchWords;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public String getCrawlerLink() {
        return crawlerLink;
    }

    public void setCrawlerLink(String crawlerLink) {
        this.crawlerLink = crawlerLink;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
