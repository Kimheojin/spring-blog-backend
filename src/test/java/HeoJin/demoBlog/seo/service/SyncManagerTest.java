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

}