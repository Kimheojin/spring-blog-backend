package HeoJin.demoBlog.seo.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.stereotype.Component;

@Component
public class SyncManager {
    private static final Parser parser = Parser.builder().build();
    private static final TextContentRenderer textContentRenderer = TextContentRenderer.builder().build();

    public static String toPlainText(String content) {
        if (content == null) {
            return null;
        }
        Node document = parser.parse(content);
        return textContentRenderer.render(document);
    }


    // plain content 생성하느 메소드


}
