package com.tenco.spring_blog.sms;

import com.tenco.spring_blog._core.error.Exception400;
import lombok.Data;

public class SmsRequest {

    @Data
    public static class SmsDto{
        private String phone;
        private String code;

        public  void validate() {
            if (phone == null || phone.trim().isEmpty()) {
                throw new Exception400("이메일을 입력해주세요");
            }
        }

        public void validateCode() {
            validate();
            if (code == null || code.trim().isEmpty()){
                throw new Exception400("인증번호를 입력해주세요");
            }
        }
    }
}
