package com.zxw.tenholescrawler;

import org.jsoup.nodes.Element;

public interface ElementMatcher {

    /**
     * 目标元素是否符合爬取条件
     *
     * @param element 目标元素
     * @return 是否符合爬取条件
     */
    boolean match(Element element);
}
