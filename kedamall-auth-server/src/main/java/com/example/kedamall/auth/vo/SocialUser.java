package com.example.kedamall.auth.vo; /**
 * Copyright 2020 bejson.com
 */

import lombok.Data;

/**
 * Auto-generated: 2020-12-03 19:33:29
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class SocialUser {

    private String access_token;
    private String remind_in;
    private long expires_in;
    private String uid;
    private String isRealName;

}
