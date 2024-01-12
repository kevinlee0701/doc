package com.kevinlee.elasticsearch.config;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能描述: 复写 org.jsoup.safety.Whitelist 扩展白名单过滤能力
 *
 * @author LiuYang
 * @return
 * @date 2020/4/23 8:24 下午
 */
public class JsoupWhitelistUntil extends Whitelist {

    /**
     * 功能描述:默认加载特殊
     *
     * @author LiuYang
     * @date 2020/4/24 1:46 下午
     */
    public JsoupWhitelistUntil() {
        addTags("strong", "b", "br", "p", "h", "img", "i", "u","h");
        addAttributes("img", "src");
    }

    /**
     * 功能描述:默认加载特殊
     *
     * @author LiuYang
     * @date 2020/4/24 1:46 下午
     */
    public JsoupWhitelistUntil(String... tags) {
        Validate.notNull(tags);
        addTags(tags);
        addTags("strong", "b", "br", "p", "h", "img", "i", "u");
        addAttributes("img", "src");
    }

    /**
     * 功能描述:默认加载特殊
     *
     * @author LiuYang
     * @date 2020/4/24 1:46 下午
     */
    public JsoupWhitelistUntil(String tagName,String attributes) {
        Validate.notNull(tagName);
        addTags(tagName);
        addTags("strong", "b", "br", "p", "h", "img", "i", "u");
        addAttributes("img", "src");
        addAttributes(tagName,attributes);
    }

    /**
     * 功能描述:默认加载特殊
     *
     * @author LiuYang
     * @date 2020/4/24 1:46 下午
     */
    public JsoupWhitelistUntil(String[] tagName,String attributes) {
        Validate.notNull(tagName);
        addTags(tagName);
        addTags("strong", "b", "br", "p", "h", "img", "i", "u");
        addAttributes("img", "src");
        for (String s : tagName) {
            addAttributes(s,attributes);

        }
    }

    @Override
    protected boolean isSafeAttribute(String tagName, Element el, Attribute attr) {
        String attrValue= attr.getValue().replaceAll(" ","");

        if ("p".equals(tagName) && attrValue.contains("text-align:center") && "style".equals(attr.getKey())) {
            attr.setValue("text-align:center;");
            return true;
        } else
        if ("h2".equals(tagName) && attrValue.contains("text-align:center") && "style".equals(attr.getKey())) {
            attr.setValue("text-align:center;");
            return true;
        }
        if ("div".equals(tagName) && attrValue.contains("text-align:center") && "style".equals(attr.getKey())) {
            attr.setValue("text-align:center;");
            return true;
        }
        else {
            return super.isSafeAttribute(tagName, el, attr);
        }
    }
}
