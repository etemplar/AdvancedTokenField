package com.fo0.advancedtokenfield.listener;

import com.fo0.advancedtokenfield.model.ITokenItem;
import com.fo0.advancedtokenfield.model.Token;

public interface OnEnterListener<F extends ITokenItem> {

	void action(Token<F> token);

}
