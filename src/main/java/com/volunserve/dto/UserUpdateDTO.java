package com.volunserve.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

    @Size(max = 20, message = "手机号不能超过20字符")
    private String phone;

    @Size(max = 50, message = "真实姓名不能超过50字符")
    private String realName;

    @Size(max = 200, message = "头像URL不能超过200字符")
    private String avatar;

    @Size(max = 500, message = "个人简介不能超过500字符")
    private String bio;
}
