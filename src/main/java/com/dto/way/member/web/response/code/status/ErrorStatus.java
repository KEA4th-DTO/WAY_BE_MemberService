package com.dto.way.member.web.response.code.status;

import com.dto.way.member.web.response.code.BaseErrorCode;
import com.dto.way.member.web.response.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 회원가입 응답
    MEMBER_NICKNAME_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBER4001", "유효한 닉네임이 아닙니다."),
    MEMBER_NICKNAME_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER4002", "중복된 닉네임 입니다."),
    MEMBER_PASSWORD_NOT_VALID(HttpStatus.BAD_REQUEST, "MEMBER4003", "유효한 비밀번호가 아닙니다."),
    MEMBER_PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "MEMBER4004", "비밀번호가 일치하지 않습니다."),
    MEMBER_EMAIL_DUPLICATED(HttpStatus.BAD_REQUEST, "MEMBER4005", "해당 이메일로 가입한 계정이 존재합니다."),

    // 로그인 응답
    MEMBER_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "MEMBER40010", "아이디 또는 비밀번호가 일치하지 않습니다."),
    ;



    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
