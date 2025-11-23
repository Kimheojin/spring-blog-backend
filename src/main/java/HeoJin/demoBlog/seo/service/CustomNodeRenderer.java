package HeoJin.demoBlog.seo.service;

import org.commonmark.node.*;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.text.TextContentNodeRendererContext;
import org.commonmark.renderer.text.TextContentWriter;

import java.util.HashSet;
import java.util.Set;


// https://github.com/commonmark/commonmark-java -> readme html 랜더 부분
public class CustomNodeRenderer implements NodeRenderer {
    private final TextContentNodeRendererContext context;
    private final TextContentWriter writer;

    CustomNodeRenderer(TextContentNodeRendererContext context) {
        this.context = context;
        this.writer = context.getWriter();
    }
    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
        Set<Class<? extends Node>> set = new HashSet<>();
        //  [text](url)
        set.add(Link.class);
        //  ![alt](src)
        set.add(Image.class);
        set.add(FencedCodeBlock.class); //  ```
        set.add(HtmlInline.class);
        set.add(HtmlBlock.class);
        set.add(IndentedCodeBlock.class); // 들여쓰기로 표현된 코드 블록
        return set;
    }

    @Override
    public void render(Node node) {
        // 위에서 설정한 부분 버림
        // 아무것도 설정 x
    }

}
