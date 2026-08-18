package com.skala.ch03.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skala.ch03.service.OrderSummaryService;
import com.skala.ch03.service.SummaryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Day 1 실습 — 컨트롤러는 AI를 모른다.
 *
 * <p>여기엔 {@code ChatClient} 가 없다. 나중에 모델을 바꿔도 웹 계층은 그대로다.
 */
@RestController
@RequestMapping("/lab1/orders")
@Tag(name = "Day1 실습 · 주문 요약")
public class OrderSummaryController {

    private final OrderSummaryService service; //Chatclient 는 여기 없다

    public OrderSummaryController(OrderSummaryService service) {
        this.service = service;
    }

    @GetMapping("/{orderId}/summary")
    @Operation(summary = "주문 한 문장 요약",
            description = "본인 주문만 요약된다. 모델을 호출하므로 비용이 발생한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요약 성공"),
            @ApiResponse(responseCode = "404", description = "없는 주문이거나 남의 주문")})
    public SummaryResponse summary(
            @Parameter(description = "주문번호", example = "12345") @PathVariable String orderId,
            @Parameter(description = "조회 주체", example = "user-1") @RequestParam String userId) {
        return service.summarize(orderId, userId);
    }
}
