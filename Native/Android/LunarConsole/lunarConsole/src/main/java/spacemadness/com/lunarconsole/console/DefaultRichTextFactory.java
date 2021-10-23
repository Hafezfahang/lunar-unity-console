//
//  DefaultRichTextFactory.java
//
//  Lunar Unity Mobile Console
//  https://github.com/SpaceMadness/lunar-unity-console
//
//  Copyright 2015-2021 Alex Lementuev, SpaceMadness.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//


package spacemadness.com.lunarconsole.console;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import spacemadness.com.lunarconsole.utils.IntReference;
import spacemadness.com.lunarconsole.utils.StringUtils;

/* This class is not thread-safe */
public class DefaultRichTextFactory implements RichTextFactory {
    private final ColorFactory colorFactory;
    private final StyleFactory styleFactory;

    public DefaultRichTextFactory(ColorFactory colorFactory) {
        this(colorFactory, new DefaultStyleFactory());
    }

    public DefaultRichTextFactory(ColorFactory colorFactory, StyleFactory styleFactory) {
        if (colorFactory == null) {
            throw new IllegalArgumentException("Color factory is null");
        }
        if (styleFactory == null) {
            throw new IllegalArgumentException("Style factory is null");
        }
        this.colorFactory = colorFactory;
        this.styleFactory = styleFactory;
    }

    private static boolean isValidTagName(String name) {
        return name.equals("b") || name.equals("i") || name.equals("color");
    }

    private static Tag tryCaptureTag(String str, int position, IntReference iterPtr) {
        int end = iterPtr.value;
        boolean isOpen = true;
        if (end < str.length() && str.charAt(end) == '/') {
            isOpen = false;
            ++end;
        }

        int start = end;
        boolean found = false;
        while (end < str.length()) {
            char chr = str.charAt(end++);
            if (chr == '>') {
                found = true;
                break;
            }
        }

        if (!found) {
            return null;
        }

        String capture = str.substring(start, end - 1);
        int index = capture.lastIndexOf('=');
        String name = index != -1 ? capture.substring(0, index) : capture;
        if (!isValidTagName(name)) {
            return null;
        }

        String attribute = index != -1 ? capture.substring(index + 1) : null;
        iterPtr.value = end;
        return new Tag(name, attribute, isOpen, position);
    }

    //region Rich Text

    @Override
    public CharSequence createRichText(String text) {
        if (StringUtils.IsNullOrEmpty(text)) {
            return text;
        }

        List<Span> tags = null;
        Stack<Tag> stack = null;
        IntReference i = new IntReference(0);

        StringBuilder buffer = new StringBuilder(text.length());

        int boldCount = 0;
        int italicCount = 0;

        while (i.value < text.length()) {
            char chr = text.charAt(i.value++);
            if (chr == '<') {
                Tag tag = tryCaptureTag(text, buffer.length(), i);
                if (tag != null) {
                    if (tag.open) {
                        if ("b".equals(tag.name)) {
                            boldCount++;
                        } else if ("i".equals(tag.name)) {
                            italicCount++;
                        }

                        if (stack == null) stack = new Stack<>();
                        stack.add(tag);
                    } else if (stack != null && stack.size() > 0) {
                        Tag opposingTag = stack.pop();

                        // if tags don't match - just use raw string
                        if (!tag.name.equals(opposingTag.name)) {
                            continue;
                        }

                        if ("b".equals(tag.name)) {
                            boldCount--;
                            if (boldCount > 0) {
                                continue;
                            }
                        } else if ("i".equals(tag.name)) {
                            italicCount--;
                            if (italicCount > 0) {
                                continue;
                            }
                        }

                        // create rich text tag
                        int len = buffer.length() - opposingTag.position;
                        if (len > 0) {
                            if (tags == null) tags = new ArrayList<>();
                            switch (tag.name) {
                                case "b": {
                                    StyleSpan style = italicCount > 0 ? styleFactory.createBoldItalic() : styleFactory.createBold();
                                    tags.add(new Span(style, opposingTag.position, len));
                                    break;
                                }
                                case "i": {
                                    StyleSpan style = boldCount > 0 ? styleFactory.createBoldItalic() : styleFactory.createItalic();
                                    tags.add(new Span(style, opposingTag.position, len));
                                    break;
                                }
                                case "color":
                                    String colorValue = opposingTag.attribute;
                                    if (colorValue != null) {
                                        CharacterStyle style = styleFromColorValue(colorValue);
                                        tags.add(new Span(style, opposingTag.position, len));
                                    }
                                    break;
                            }
                        }
                    }
                } else {
                    buffer.append(chr);
                }
            } else {
                buffer.append(chr);
            }
        }

        // handle un-matched tags
        if (stack != null) {
            while (stack.size() > 0) {
                Tag tag = stack.pop();

                if ("b".equals(tag.name)) {
                    boldCount--;
                    if (boldCount > 0) {
                        continue;
                    }
                } else if ("i".equals(tag.name)) {
                    italicCount--;
                    if (italicCount > 0) {
                        continue;
                    }
                }

                // create rich text tag
                int len = buffer.length() - tag.position;
                if (len > 0) {
                    if (tags == null) tags = new ArrayList<>();
                    switch (tag.name) {
                        case "b": {
                            StyleSpan style = italicCount > 0 ? styleFactory.createBoldItalic() : styleFactory.createBold();
                            tags.add(new Span(style, tag.position, len));
                            break;
                        }
                        case "i": {
                            StyleSpan style = boldCount > 0 ? styleFactory.createBoldItalic() : styleFactory.createItalic();
                            tags.add(new Span(style, tag.position, len));
                            break;
                        }
                        case "color":
                            String colorValue = tag.attribute;
                            if (colorValue != null) {
                                CharacterStyle style = styleFromColorValue(colorValue);
                                tags.add(new Span(style, tag.position, len));
                            }
                            break;
                    }
                }
            }
        }

        if (tags != null && buffer.length() > 0) {
            Collections.reverse(tags); // we need to reverse the list to support nested tags
            return createSpannedString(buffer.toString(), tags);
        }

        if (buffer.length() < text.length()) {
            return buffer.toString();
        }

        return text;
    }

    private SpannableString createSpannedString(String text, List<Span> tags) {
        SpannableString string = new SpannableString(text);
        for (int i = 0; i < tags.size(); i++) {
            Span tag = tags.get(i);
            string.setSpan(tag.style, tag.startIndex, tag.startIndex + tag.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return string;
    }

    CharacterStyle styleFromColorValue(String value) {
        int color = colorFactory.fromValue(value);
        return styleFactory.createCharacterStyle(color);
    }

    public static class Span {
        public final Object style;
        public final int startIndex;
        public final int length;

        public Span(CharacterStyle style, int startIndex, int length) {
            this((Object) style, startIndex, length);
        }

        public Span(StyleSpan style, int startIndex, int length) {
            this((Object) style, startIndex, length);
        }

        private Span(Object style, int startIndex, int length) {
            this.style = style;
            this.startIndex = startIndex;
            this.length = length;
        }
    }

    private static final class Tag {
        public final String name;
        public final String attribute;
        public final boolean open;
        public final int position;

        private Tag(String name, String attribute, boolean open, int position) {
            this.name = name;
            this.attribute = attribute;
            this.open = open;
            this.position = position;
        }
    }

    //endregion
}
