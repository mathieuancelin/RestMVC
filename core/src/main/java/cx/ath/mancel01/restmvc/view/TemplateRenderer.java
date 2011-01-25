/*
 *  Copyright 2010 Mathieu ANCELIN.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package cx.ath.mancel01.restmvc.view;

import cx.ath.mancel01.restmvc.FrameworkFilter;
import cx.ath.mancel01.restmvc.http.Session;
import cx.ath.mancel01.restmvc.utils.FileUtils;
import groovy.text.SimpleTemplateEngine;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mathieu ANCELIN
 */
public class TemplateRenderer {

    private static final Pattern listPattern = Pattern.compile("\\#\\{list items:[^/]+, as:'[^']+'\\}");
    private static final Pattern extendsPatter = Pattern.compile("\\#\\{extends '[^']+' /\\}");
    private static final Pattern setPattern = Pattern.compile("\\#\\{set [^/]+:'[^']+' /\\}");
    private static final Pattern getPattern = Pattern.compile("\\#\\{get [^/]+ /\\}");
    private static final Pattern linkPattern = Pattern.compile("\\@\\{'[^']+'\\}");
    private final SimpleTemplateEngine engine;
    private final ConcurrentHashMap<String, groovy.text.Template> templates =
            new ConcurrentHashMap<String, groovy.text.Template>();

    public TemplateRenderer() {
        this.engine = new SimpleTemplateEngine();
    }

    public String render(String source, Map<String, Object> context) throws Exception {
        context.put("session", Session.current.get());
        context.put("request", FrameworkFilter.currentRequest.get());
        context.put("logger", FrameworkFilter.logger);
        context.put("dev", FrameworkFilter.dev);
        context.put("root", FrameworkFilter.getContextRoot());
        return renderWithGroovy(source, context);
    }

    private String renderWithGroovy(String fileName, Map<String, Object> context) throws Exception {
        // TODO : if file not exists, return 404
        StringWriter osw = new StringWriter();
        context.put("out", osw);
        File file = FrameworkFilter.getFile("views/" + fileName);
        if (FrameworkFilter.dev) {
            String code = FileUtils.readFileAsString(file);
            return engine.createTemplate(enhanceCode(code)).make(context).writeTo(osw).toString();
            //return engine.createTemplate(file).make(context).writeTo(osw);
        } else {
            if (!templates.containsKey(file.getAbsolutePath())) {
                String code = FileUtils.readFileAsString(file);
                templates.putIfAbsent(file.getAbsolutePath()
                    , engine.createTemplate(enhanceCode(code)));
                //templates.putIfAbsent(file.getAbsolutePath()
                //    , engine.createTemplate(file));
            }
            return templates.get(file.getAbsolutePath()).make(context).writeTo(osw).toString();
        }
    }

    public static String enhanceCode(String code) {
        // TODO : custom tags, links, optimize :)
        List<String> before = new ArrayList<String>();
        String custom = code
            .replace("%{", "<%")
            .replace("}%", "%>")
            .replace("$.", "\\$.")
            .replace("$(", "\\$(")
            .replace("#{/list}", "<% } %>")
            .replace("#{/list }", "<% } %>");
        Matcher matcher = listPattern.matcher(custom);
        while(matcher.find()) {
            String group = matcher.group();
            String list = group;
            list = list.replace("#{list items:", "<% ")
                .replace(", as:'", ".each { ")
                .replace(",as:'", ".each { ")
                .replace("'}", " -> %>")
                .replace("' }", " -> %>");
            custom = custom.replace(group, list);
        }
        Matcher setMatcher = setPattern.matcher(custom);
        while(setMatcher.find()) {
            String group = setMatcher.group();
            String name = group;
            name = name.replace("#{set ", "").replaceAll(":'[^']+' /\\}", "");
            String value = group;
            value = group.replaceAll("\\#\\{set [^/]+:'", "").replace("' /}", "");
            //custom = custom.replace(group, "<% " + name + " = '" + value + "' %>");
            custom = custom.replace(group, "");
            before.add("<% " + name + " = '" + value + "' %>\n");
        }
        Matcher getMatcher = getPattern.matcher(custom);
        while(getMatcher.find()) {
            String group = getMatcher.group();
            String name = group;
            name = name.replace("#{get ", "").replace(" /}", "");
            custom = custom.replace(group, "${" + name + "}");
        }
        Matcher linkMatcher = linkPattern.matcher(custom);
        while(linkMatcher.find()) {
            String group = linkMatcher.group();
            String link = group;
            link = link.replace("@{'", "").replace("'}", "");
            if (!"/".equals(FrameworkFilter.getContextRoot())) {
                link = FrameworkFilter.getContextRoot() + link;
            }
            custom  = custom.replace(group, link);
        }
        Matcher extendsMatcher = extendsPatter.matcher(custom);
        custom = custom.replaceAll("\\#\\{extends '[^']+' /\\}", "");
        while(extendsMatcher.find()) {
            String group = extendsMatcher.group();
            String fileName = group;
            fileName = fileName.replace("#{extends '", "").replace("' /}", "");
            File file = FrameworkFilter.getFile("views/" + fileName);
            String parentCode = FileUtils.readFileAsString(file);
            String parentCustomCode = enhanceCode(parentCode);
            String[] parts = parentCustomCode.split("\\#\\{doLayout /\\}");
            if (parts.length > 2) {
                throw new RuntimeException("Can't have #{doLayout /} more than one time in a template.");
            }
            String finalCode = parts[0] + custom + parts[1];
            for (String bef : before) {
                finalCode = bef + finalCode;
            }
            return finalCode;
        }
        for (String bef : before) {
            custom = bef + custom;
        }
        return custom;
    }

}