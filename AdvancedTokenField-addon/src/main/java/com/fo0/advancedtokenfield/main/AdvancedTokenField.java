package com.fo0.advancedtokenfield.main;

import com.fo0.advancedtokenfield.interceptor.TokenAddInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenNewItemInterceptor;
import com.fo0.advancedtokenfield.interceptor.TokenRemoveInterceptor;
import com.fo0.advancedtokenfield.listener.OnEnterListener;
import com.fo0.advancedtokenfield.listener.TokenAddListener;
import com.fo0.advancedtokenfield.listener.TokenRemoveListener;
import com.fo0.advancedtokenfield.model.ITokenItem;
import com.fo0.advancedtokenfield.model.Token;
import com.fo0.advancedtokenfield.model.TokenLayout;
import com.vaadin.data.HasValue;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.shared.Registration;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import fi.jasoft.dragdroplayouts.DDCssLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class AdvancedTokenField<F extends ITokenItem> extends DDCssLayout {

	private static final long serialVersionUID = 8139678186130686248L;

	private ComboBox<F> inputField = null;

	private List<F> initInputFieldDtos = new ArrayList<>();
	private List<F> addedDtos = new ArrayList<>();

	/**
	 * Interceptors
	 */
	private TokenRemoveInterceptor<F> tokenRemoveInterceptor;
	private TokenAddInterceptor<F> tokenAddInterceptor;
	private TokenNewItemInterceptor<F> tokenNewItemInterceptor;

	/**
	 * Listener
	 */
	private TokenRemoveListener<F> tokenRemoveListener;
	private TokenAddListener<F> tokenAddListener;

	private OnEnterListener<F> enterListener;

	private boolean tokenCloseButton = true;

	private static final String BASE_STYLE = "advancedtokenfield-layouttokens";

	public AdvancedTokenField(String caption) {
		this(caption, null);
	}

	public AdvancedTokenField(List<F> tokens) {
		this(null, tokens);
	}

	public AdvancedTokenField(String caption, List<F> tokens) {
		if (tokens != null && !tokens.isEmpty())
			this.initInputFieldDtos.addAll(tokens);
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
		inputField.setSizeFull();

        inputField.setItemCaptionGenerator(ITokenItem::getStrRepresentation);
        inputField.setItems(initInputFieldDtos);

		inputField.addValueChangeListener((HasValue.ValueChangeListener<F>) event -> {
            if(event.getValue() != null){
                Token<F> token = new Token<>(event.getValue());
                addToken(token);
            }
        });

		addComponent(inputField);

		tokenAddInterceptor = token -> token;

		tokenRemoveInterceptor = event -> event;

		tokenNewItemInterceptor = token -> token;
	}

	public void setDataProvider(DataProvider<F, String> dataProvider){
	    inputField.setDataProvider(dataProvider);
    }

	public void setAllowNewItems() {
		inputField.setNewItemProvider((ComboBox.NewItemProvider<F>) s -> {

		    ITokenItem iTokenItem = new ITokenItem() {
                @Override
                public String getStrRepresentation() {
                    return s;
                }

                @Override
                public Object getDbRepresentation() {
                    return s;
                }
            };

            Token token = new Token<>(iTokenItem);
            addToken(token);
            return Optional.empty();
        });
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

	public void removeToken(Token<F> token) {
		Token<F> tokenData = tokenRemoveInterceptor.action(token);

		if (tokenData == null) {
			// prevent remove if interceptor not allow
			return;
		}

		// search in layout and remove if found
		TokenLayout<F> tl = null;
        for (Component component : this) {
            if (component instanceof TokenLayout) {
                if (((TokenLayout<F>) component).getToken().equals(token)) {
                    tl = (TokenLayout<F>) component;
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

        addedDtos.remove(token.getValue());
	}

	public void addToken(Token<F> token, int idx) {
		Token<F> tokenData = tokenAddInterceptor.action(token);
		if (tokenData == null) {
			// filter empty tokens
			return;
		}

		if(addedDtos.contains(token.getValue())){
		    inputField.clear();
		    return;
        }

		TokenLayout<F> tokenLayout = new TokenLayout<F>(tokenData, tokenCloseButton);

		if (tokenCloseButton)
			tokenLayout.getBtn().addClickListener(e -> {
				removeToken(tokenLayout.getToken());
			});

		addDtoToList(tokenData.getValue());

		if (idx > -1) {
			super.addComponent(tokenLayout, idx);
		} else {
			super.addComponent(tokenLayout, getComponentCount() - 1);

		}

		if (tokenAddListener != null)
			tokenAddListener.action(tokenData);

		inputField.clear();
	}

	public void addToken(Token<F> token) {
		addToken(token, getComponentCount());
	}

	public void addTokens(List<Token<F>> token) {
		token.forEach(this::addToken);
	}

	public ComboBox<F> getInputField() {
		return inputField;
	}

	public List<F> getAddedDtos() {
		return addedDtos;
	}

	public List<Token<F>> getTokens() {
		List<Token<F>> list = new ArrayList<>();
		for (int i = 0; i < getComponentCount(); i++) {
			if (getComponent(i) instanceof CssLayout) {
				CssLayout c = (CssLayout) getComponent(i);
				Token<F> t = (Token<F>) c.getData();
				list.add(t);
			}
		}
		return list;
	}

	public void addDtosToList(List<F> dtos) {
		if (dtos == null || dtos.isEmpty())
			return;

        addedDtos.addAll(dtos);
	}

	public void addDtoToList(F dto) {
        addedDtos.add(dto);
	}

    public void addDtosToInputField(List<F> dtos) {
        if (dtos == null || dtos.isEmpty())
            return;

        initInputFieldDtos.addAll(dtos);
        inputField.setItems(initInputFieldDtos);
    }

    public void addDtoToInputField(F dto) {
        initInputFieldDtos.add(dto);
        inputField.setItems(initInputFieldDtos);
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
        addedDtos.clear();
	}

	/**
	 * Listener
	 */

	public void addTokenRemoveListener(TokenRemoveListener<F> listener) {
		this.tokenRemoveListener = listener;
	}

	public void addTokenAddListener(TokenAddListener<F> listener) {
		this.tokenAddListener = listener;
	}

	public void addOnEnterListener(OnEnterListener<F> listener) {
		enterListener = listener;
	}

	/**
	 * Interceptors
	 */

	public void addTokenRemoveInterceptor(TokenRemoveInterceptor<F> interceptor) {
		this.tokenRemoveInterceptor = interceptor;
	}

	public void addTokenAddInterceptor(TokenAddInterceptor<F> interceptor) {
		this.tokenAddInterceptor = interceptor;
	}

	public void addTokenAddNewItemInterceptor(TokenNewItemInterceptor<F> interceptor) {
		this.tokenNewItemInterceptor = interceptor;
	}

}
