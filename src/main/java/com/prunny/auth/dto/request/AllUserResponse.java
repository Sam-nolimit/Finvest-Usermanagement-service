package com.prunny.auth.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class AllUserResponse {
    private  String responseCode;
    private  String responseMessage;
    private List<AccountInfo> accountInfo;
}
