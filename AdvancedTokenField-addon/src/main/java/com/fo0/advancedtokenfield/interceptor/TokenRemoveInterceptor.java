package com.fo0.advancedtokenfield.interceptor;

import com.fo0.advancedtokenfield.model.ITokenItem;
import com.fo0.advancedtokenfield.model.Token;

public interface TokenRemoveInterceptor<F extends ITokenItem> {

	Token<F> action(Token<F> event);

}
