package br.com.sevenheads.userService.utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.net.MalformedURLException;
import java.net.URL;

public final class FormatUtil {

    public static boolean isValidHtml(String html) {
        try {
            Document doc = Jsoup.parse(html, "", Parser.htmlParser());
            return doc != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidUrl(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
