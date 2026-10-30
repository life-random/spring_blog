package com.tenco.spring_blog.refund;

import com.tenco.spring_blog._core.error.Exception400;
import com.tenco.spring_blog._core.util.MyDateUtil;
import lombok.Builder;
import lombok.Data;

public class RefundRequest {

    @Data
    public static class SaveDto{
        private Long paymentPk;
        private String reason;

        public void validate(){
            if (paymentPk == null){
                throw new Exception400("결제 정보가 없습니다");
            }

            if (reason == null || reason.trim().isEmpty()){
                throw new Exception400("환불 사유는 필수입니다");
            }

            if (reason.length() > 500 ){
                throw new Exception400("환불 사유는 500자 이하로 입력해주세요");
            }
        }
    }

    @Data
    public static class ListDto {
        private Long id;            // 환불 요청 pk
        private String paymentId;
        private int amount;
        private String createdAt;   // 결제 요청 시간

        @Builder
        public ListDto(Refund refund) {
            this.id = refund.getId();
            this.paymentId = refund.getPayment().getPaymentId();
            this.amount = refund.getPayment().getAmount();
            this.createdAt = MyDateUtil.timestampFormat(refund.getCreatedAt());
        }
    }

}
