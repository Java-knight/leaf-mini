package com.knight.leaf.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * service 异常
 * @desc
 * @author knight
 * @date 2023/6/10
 */
@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
public class LeafServerException extends  RuntimeException {

    public LeafServerException(String msg) {
        super(msg);
    }
}
