package com.timcook.capstone.user.dto;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.timcook.capstone.user.dto.QUserResponse is a Querydsl Projection type for UserResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QUserResponse extends ConstructorExpression<UserResponse> {

    private static final long serialVersionUID = -2120861250L;

    public QUserResponse(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> username, com.querydsl.core.types.Expression<String> email, com.querydsl.core.types.Expression<com.timcook.capstone.user.domain.Role> role, com.querydsl.core.types.Expression<String> phoneNumber, com.querydsl.core.types.Expression<? extends com.timcook.capstone.village.domain.Address> address) {
        super(UserResponse.class, new Class<?>[]{long.class, String.class, String.class, com.timcook.capstone.user.domain.Role.class, String.class, com.timcook.capstone.village.domain.Address.class}, id, username, email, role, phoneNumber, address);
    }

}

