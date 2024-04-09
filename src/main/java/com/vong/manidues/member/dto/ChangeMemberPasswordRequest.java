package com.vong.manidues.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeMemberPasswordRequest {
    private String email;
    private String currentPassword;
    private String changedPassword;
    private String changedPasswordCheck;
}
