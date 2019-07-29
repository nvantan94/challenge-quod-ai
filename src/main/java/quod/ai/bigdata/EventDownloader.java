package quod.ai.bigdata;

import com.google.gson.JsonObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class EventDownloader {
    private static final String URL_PREFIX = "https://data.gharchive.org/";
    private static final String URL_SUFFIX = ".json.gz";

    public static BufferedReader openStreamToEvents(LocalDateTime atHour) throws Exception {
        String hourParam = String.format("%4d-%02d-%02d-%d", atHour.getYear(),
                atHour.getMonthValue(), atHour.getDayOfMonth(), atHour.getHour());
        String url = URL_PREFIX + hourParam + URL_SUFFIX;
        URLConnection eventConnection = new URL(url).openConnection();
        eventConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        GZIPInputStream gzip = new GZIPInputStream(eventConnection.getInputStream());
        return new BufferedReader(new InputStreamReader(gzip));
    }
}
