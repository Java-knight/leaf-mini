package com.knight.leaf.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * db/cache 中没有key/biz_tag异常
 * @desc
 * @author knight
 * @date 2023/6/10
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Key is none")
public class NoKeyException extends RuntimeException {
}
