package com.fo0.advancedtokenfield.listener;

import com.fo0.advancedtokenfield.model.ITokenItem;
import com.fo0.advancedtokenfield.model.TokenLayout;

public interface TokenRemoveListener<F extends ITokenItem> {

	void action(TokenLayout<F> event);

}
