package com.fo0.advancedtokenfield.main;

import com.fo0.advancedtokenfield.interceptor.TokenAddInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.listener.OnEnterListener;
import com.fo0.advancedtokenfield.listener.TokenAddListener;
import com.fo0.advancedtokenfield.listener.TokenRemoveListener;
import com.fo0.advancedtokenfield.model.Token;
import com.fo0.advancedtokenfield.model.TokenLayout;
import com.vaadin.data.HasValue;
import com.vaadin.shared.Registration;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import fi.jasoft.dragdroplayouts.DDCssLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AdvancedTokenField extends DDCssLayout {

	private static final long serialVersionUID = 8139678186130686248L;

	private ComboBox<Token> inputField = null;

	private List<Token> tokensOfField = new ArrayList<>();

	/**
	 * Interceptors
	 */
	private TokenRemoveInterceptor tokenRemoveInterceptor;
	private TokenAddInterceptor tokenAddInterceptor;
	private TokenNewItemInterceptor tokenNewItemInterceptor;

	/**
	 * Listener
	 */
	private TokenRemoveListener tokenRemoveListener;
	private TokenAddListener tokenAddListener;

	private OnEnterListener enterListener;

	private boolean allowNewTokens = false;
	private boolean allowEmptyValues = false;
	private boolean tokenCloseButton = true;

	private static final String BASE_STYLE = "advancedtokenfield-layouttokens";

	public AdvancedTokenField(String caption) {
		this(caption, null);
	}

	public AdvancedTokenField(List<Token> tokens) {
		this(null, tokens);
	}

	public AdvancedTokenField(String caption, List<Token> tokens) {
		if (tokens != null && !tokens.isEmpty())
			this.tokensOfField.addAll(tokens);
		init();
		if(caption != null)
			inputField.setCaption(caption);
	}

	public AdvancedTokenField() {
		init();
	}

	private void init() {
		addStyleName(BASE_STYLE);

		inputField = new ComboBox<>();

		inputField.setItemCaptionGenerator(Token::getValue);
		inputField.setItems(tokensOfField);


		inputField.addValueChangeListener(new HasValue.ValueChangeListener<Token>() {
			@Override
			public void valueChange(HasValue.ValueChangeEvent<Token> event) {
				if(event.getValue() != null) {
					Token token = event.getValue();
					addToken(token);
				}
			}
		});

		addComponent(inputField);

		tokenAddInterceptor = new TokenAddInterceptor() {

			@Override
			public Token action(Token token) {
				return token;
			}
		};

		tokenRemoveInterceptor = new TokenRemoveInterceptor() {

			@Override
			public Token action(Token event) {
				return event;
			}
		};

		tokenNewItemInterceptor = new TokenNewItemInterceptor() {

			@Override
			public Token action(Token token) {
				return token;
			}
		};
	}

	public void setAllowNewItems(boolean allow) {
		this.allowNewTokens = allow;
	}

	public void setAllowEmptyValues(boolean allow) {
		this.allowNewTokens = allow;
	}

	public void setTokenCloseButton(boolean tokenCloseButton) {
		this.tokenCloseButton = tokenCloseButton;
	}

	public boolean getTokenCloseButton() {
		return tokenCloseButton;
	}

	@Override
	public Registration addComponentAttachListener(ComponentAttachListener listener) {
		System.out.println("add detecting class attach");
		return super.addComponentAttachListener(listener);
	}

	@Override
	public void removeComponent(Component c) {
		System.out.println("remove detecting class: " + c.getClass());
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			removeToken(((TokenLayout) c).getToken());
			return;
		}
		super.removeComponent(c);
	}

	@Override
	public void addComponentAsFirst(Component c) {
		System.out.println("add detecting class: " + c.getClass());
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), getComponentCount());
			return;
		}

		super.addComponent(c);
	}

	@Override
	public void addComponent(Component c) {
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), -1);
			return;
		}

		super.addComponent(c);
	}

	@Override
	public void addComponent(Component c, int index) {
		if (c instanceof TokenLayout) {
			// detect the drag and drop from layout
			addToken(((TokenLayout) c).getToken(), index);
			return;
		}

		super.addComponent(c, index);
	}

	public void removeToken(Token token) {
		Token tokenData = tokenRemoveInterceptor.action(token);

		if (tokenData == null) {
			// prevent remove if interceptor not allow
			return;
		}

		// search in layout and remove if found
		TokenLayout tl = null;
		for (Iterator<Component> iterator = iterator(); iterator.hasNext();) {
			Component component = iterator.next();
			if (component instanceof TokenLayout) {
				if (((TokenLayout) component).getToken().equals(token)) {
					tl = (TokenLayout) component;
					break;
				}
			}
		}

		if (tl != null && tl.getToken() != null && tl.getToken().equals(tokenData)) {
			super.removeComponent(tl);
		}

		if (tokenRemoveListener != null && tl != null) {
			tokenRemoveListener.action(tl);
		}

		tokensOfField.add(token);
		Collections.sort(tokensOfField);
		inputField.setItems(tokensOfField);
	}

	public void addToken(Token token, int idx) {
		Token tokenData = tokenAddInterceptor.action(token);
		if (tokenData == null) {
			// filter empty tokens
			return;
		}

		TokenLayout tokenLayout = new TokenLayout(tokenData, tokenCloseButton);

		if (tokenCloseButton)
			tokenLayout.getBtn().addClickListener(e -> {
				removeToken(tokenLayout.getToken());
			});

		addTokenToInputField(tokenData);

		if (idx > -1) {
			super.addComponent(tokenLayout, idx);
		} else {
			super.addComponent(tokenLayout, getComponentCount() - 1);

		}

		if (tokenAddListener != null)
			tokenAddListener.action(tokenData);

		inputField.clear();
		tokensOfField.remove(token);
		inputField.setItems(tokensOfField);
	}

	public void addToken(Token token) {
		addToken(token, getComponentCount());
	}

	public void addTokens(List<Token> token) {
		token.forEach(this::addToken);
	}

	public ComboBox<Token> getInputField() {
		return inputField;
	}

	public List<Token> getTokensOfInputField() {
		return tokensOfField;
	}

	public List<Token> getTokens() {
		List<Token> list = new ArrayList<>();
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof CssLayout) {
				CssLayout c = (CssLayout) getComponent(i);
				Token t = (Token) c.getData();
				list.add(t);
			}
		}
		return list;
	}

	public void addTokensToInputField(List<Token> tokens) {
		if (tokens == null || tokens.isEmpty())
			return;

		addTokensToInputField(tokens.toArray(new Token[0]));
	}

	public void addTokensToInputField(Token... tokens) {
		List<Token> list = Stream.of(tokens).distinct().filter(e -> !tokensOfField.contains(e))
				.collect(Collectors.toList());
		if (list == null || list.isEmpty())
			return;

		tokensOfField.addAll(list);
	}

	public void addTokenToInputField(Token token) {
		addTokensToInputField(token);
	}

	public void clearTokens() {
		List<Component> componentsToRemove = new ArrayList<>();

		IntStream.range(0, getComponentCount()).forEach(e -> {
			if (getComponent(e) instanceof CssLayout) {
				componentsToRemove.add(getComponent(e));
			}
		});

		componentsToRemove.forEach(this::removeComponent);
	}

	public void clearAll() {
		clearTokens();
		tokensOfField.clear();
	}

	/**
	 * Listener
	 */

	public void addTokenRemoveListener(TokenRemoveListener listener) {
		this.tokenRemoveListener = listener;
	}

	public void addTokenAddListener(TokenAddListener listener) {
		this.tokenAddListener = listener;
	}

	public void addOnEnterListener(OnEnterListener listener) {
		enterListener = listener;
	}

	/**
	 * Interceptors
	 */

	public void addTokenRemoveInterceptor(TokenRemoveInterceptor interceptor) {
		this.tokenRemoveInterceptor = interceptor;
	}

	public void addTokenAddInterceptor(TokenAddInterceptor interceptor) {
		this.tokenAddInterceptor = interceptor;
	}

	public void addTokenAddNewItemInterceptor(TokenNewItemInterceptor interceptor) {
		this.tokenNewItemInterceptor = interceptor;
	}

}
