package HeoJin.demoBlog.seo.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class SyncManagerTest {

    @Test
    @DisplayName("순수 컨텍스트 대충 테스트")
    void test1(){
        // given
        String markdown = "## 안녕하세요";
        // when
        String plainText = SyncManager.toPlainText(markdown);
        // then
        Assertions.assertThat(plainText).isEqualTo("안녕하세요");
    }

    @Test
    @DisplayName("해시 알고리즘 관련 테스트")
    void test2(){
        // given
        String s1 = "가나다asb";
        String s2 = "가나다asb";
        String s3 = "가나다asbb";
        // when
        String hash1 = SyncManager.makeHashCodeToContent(s1);
        String hash2 = SyncManager.makeHashCodeToContent(s2);
        String hash3 = SyncManager.makeHashCodeToContent(s3);

        // then
        Assertions.assertThat(hash1).isEqualTo(hash2);
        Assertions.assertThat(hash1).isNotEqualTo(hash3);
    }

}