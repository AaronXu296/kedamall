package com.example.kedamall.cart.vo;

import lombok.Data;

@Data
public class UserInfoTo {
    private Long userId;
    private String UserKey;

    private boolean tempUser=false;
}
