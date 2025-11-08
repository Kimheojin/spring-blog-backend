package HeoJin.demoBlog.Post.controller;


import HeoJin.demoBlog.configuration.Integration.ApiDocTestBase;
import HeoJin.demoBlog.configuration.Integration.DataInitComponent;
import HeoJin.demoBlog.member.entity.Member;
import HeoJin.demoBlog.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostReadTestApiDocTest extends ApiDocTestBase {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private DataInitComponent dataInitComponent;


    @BeforeEach
    void init() {
        Member member = dataInitComponent.createTestMember();
        dataInitComponent.saveAllCategories();
        dataInitComponent.saveAllPosts(member);
        dataInitComponent.saveAllTag();
    }
    @Test
    @DisplayName("get /api/posts/single -> 단일 포스트 조회 캐시 테스트")
    void test1() throws Exception {

        // given
        Long testPostId = postRepository.findAll().get(0).getId();
        // when + then
        mockMvc.perform(get("/api/posts/single")
                .contentType(MediaType.APPLICATION_JSON)
                .param("postId", Long.toString(testPostId)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Cache-Control"))
                .andExpect(header().string("Cache-Control", "max-age=3600"))
                .andDo(print()); // 전체 응답 헤더 출력해서 확인

    }
}
