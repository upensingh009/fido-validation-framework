package com.upensingh009.fidovalidation.common.dto;

public record BaseResponse<T>(String status, T payload) {
}
