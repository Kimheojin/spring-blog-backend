package HeoJin.demoBlog.seo.service;

import HeoJin.demoBlog.global.exception.refactor.NotFoundException;

import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.stereotype.Component;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;

@Component
public class SyncManager {
    private static final Parser parser = Parser.builder()
            .extensions(List.of(AutolinkExtension.create())).build();
    private static final TextContentRenderer textContentRenderer =
            TextContentRenderer.builder()
                    .nodeRendererFactory(CustomNodeRenderer::new)
                    .build();

    // plain content 생성 메소드
    public static String toPlainText(String content) {
        // content = 원본 markdown
        if (content == null) {
            return null;
        }
        Node document = parser.parse(content);
        // TextContentRenderer를 사용해 순수한 텍스트만
        return textContentRenderer.render(document);
    }


    // 해시 검증 관련 일치 여부 메소드
    public static String makeHashCodeToContent(String content){
        if (content == null) {
            return null;
        }

        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes());
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new NotFoundException("256 알고리즘 사용 불가");
        }


    }


}
