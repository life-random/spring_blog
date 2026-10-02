package com.tenco.spring_blog.board;

import lombok.Data;

public class BoardRequest {

    @Data
    public static class SaveDto {
        private String username;
        private String title;
        private String content;

        public void validate(){
            if (username == null || username.trim().isEmpty()){
                throw new IllegalArgumentException("유저 이름은 필수입니다");
            }
            if (title == null || title.trim().isEmpty()){
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if (content == null || content.trim().isEmpty()){
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }
    }

    @Data
    public static class UpdateDto{
        private String title;
        private String content;

        public void validate(){
            if (title == null || title.trim().isEmpty()){
                throw new IllegalArgumentException("제목은 필수입니다");
            }
            if (content == null || content.trim().isEmpty()){
                throw new IllegalArgumentException("내용은 필수입니다");
            }
        }
    }
}
