package com.zxw.tenholescrawler;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import static com.zxw.tenholescrawler.PageDownloadTask.INDEX_PAGE;

public class ListPageIterator implements Iterator<Element> {

    /**
     * 当前页面列表迭代器
     */
    private Iterator<Element> itemIterator;
    /**
     * 下一个符合条件的链接元素
     */
    private Element nextItem;
    /**
     * 下一个待加载的列表页面链接，为 null 代表无下一页
     */
    private String listPageUrl;
    /**
     * 目标元素 matcher
     */
    private final ElementMatcher matcher;
    /**
     * 页面 previewer，用于加载完整的页面
     */
    private PagePreviewer pagePreviewer;

    public ListPageIterator(String listPageUrl,
                            ElementMatcher matcher, PagePreviewer pagePreviewer) {
        this.matcher = matcher;
        this.listPageUrl = listPageUrl;
        this.pagePreviewer = pagePreviewer;
        this.itemIterator = Collections.emptyIterator();
    }

    @Override
    public boolean hasNext() {
        // 加载下一个页面
        while (!itemIterator.hasNext()) {
            if (StringUtils.isBlank(listPageUrl)) {
                return false;
            }

            itemIterator = getListPageItems();
        }

        // 找到匹配的页面
        do {
            if (matcher.match((nextItem = itemIterator.next()))) {
                return true;
            }
        } while (itemIterator.hasNext());

        return hasNext();
    }

    private Iterator<Element> getListPageItems() {
        System.out.println("> 开始查找新页面：" + listPageUrl);

        // 获取页面内容
        String html = pagePreviewer.getPagePreview(listPageUrl, webDriver -> true/*无须等待页面加载动态效果*/);
        Document doc = Jsoup.parse(html);

        // 找到对应的列表元素
        Elements list = doc.select("div.ls-item.clearfix");
        // 下一页
        Element nextPageAnchor = doc.selectFirst("div.ls-list.fl > ul > li.next > a");

        if (!Objects.isNull(nextPageAnchor)) {
            listPageUrl = nextPageAnchor.attr("href");

            if (!listPageUrl.startsWith("http")) {
                listPageUrl = INDEX_PAGE + listPageUrl;
            }
        } else {
            listPageUrl = null;
        }

        return list.iterator();
    }

    @Override
    public Element next() {
        return nextItem;
    }
}
