package HeoJin.demoBlog.seo.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SyncManagerTest {

    @Test
    @DisplayName("Markdown을 Plain Text로 변환 테스트")
    void test1() {
        // given
        String markdown = "# Header\n" +
                "**Bold** text.\n" +
                "[Link](http://example.com)\n" +
                "![Image](http://example.com/img.png)\n" +
                "`code`\n" +
                "```java\nSystem.out.println();\n```";

        // when
        String plainText = SyncManager.toPlainText(markdown);

        // then
        
        assertThat(plainText).contains("Header");
        assertThat(plainText).contains("Bold");
        assertThat(plainText).doesNotContain("http://example.com"); // Link url
        assertThat(plainText).doesNotContain("System.out.println"); // Code block content
    }

    @Test
    @DisplayName("Content 해시 생성 테스트")
    void test2() {
        // given
        String content = "Test Content";

        // when
        String hash1 = SyncManager.makeHashCodeToContent(content);
        String hash2 = SyncManager.makeHashCodeToContent(content);
        String hash3 = SyncManager.makeHashCodeToContent("Different Content");

        // then
        assertThat(hash1).isNotNull();
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(hash3);
    }
    
    @Test
    @DisplayName("Null 입력 처리")
    void test3() {
        // when + then
        assertThat(SyncManager.toPlainText(null)).isNull();
        assertThat(SyncManager.makeHashCodeToContent(null)).isNull();
    }
}
