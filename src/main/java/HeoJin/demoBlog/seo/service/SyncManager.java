package HeoJin.demoBlog.seo.service;

import HeoJin.demoBlog.global.exception.CustomNotFound;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class SyncManager {
    private static final Parser parser = Parser.builder().build();
    private static final TextContentRenderer textContentRenderer = TextContentRenderer.builder().build();

    // plain content 생성 메소드
    public static String toPlainText(String content) {
        if (content == null) {
            return null;
        }
        Node document = parser.parse(content);
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
            throw new CustomNotFound("256 알고리즘 사용 불가");
        }


    }


}
