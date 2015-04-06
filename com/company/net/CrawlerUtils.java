package com.company.net;

/**
 * Created by art71_000 on 16.03.2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlerUtils {

    public static Pattern linkPattern = Pattern.compile("<a href=\"([^\"#]+)\"");

    public static String getContent(URL url) {
        StringBuilder page = new StringBuilder ();
        BufferedReader in;
        try {
            URLConnection conn = url.openConnection();
            String contentType = conn.getContentType();
            if (contentType != null && contentType.startsWith("text/html")) {
                if (contentType.indexOf("charset=") == -1) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                } else {
                    String encoding = contentType.substring(contentType.indexOf("charset=") + 8);
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream(), encoding));
                }
                String str;
                while ((str = in.readLine()) != null) {
                    page.append(str);
                }
                in.close();
                return page.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<URI> getLinks(URL url, String content) {
        Set<URI> links = new HashSet<URI>();
        Matcher matcher = linkPattern.matcher(content);
        while (matcher.find()) {
            try {
                URI link = new URI(new URL(url, matcher.group(1)).toString());
                links.add(link);
            } catch (MalformedURLException e) {
            } catch (URISyntaxException e) {
            }
        }
        return links;
    }
}