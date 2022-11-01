package com.move.Bunjang.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequestDto {

  @NotBlank
  private String email;

  @NotBlank
  private String pw;

  @NotBlank
  private String pwConfirm;
}
