package com.fo0.advancedtokenfield.model;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.themes.ValoTheme;

public class TokenLayout<F extends ITokenItem> extends CssLayout {

	private static final long serialVersionUID = 1818425531699295539L;

	private Token<F> token = null;
	private Label lbl = new Label();
	private NativeButton btn = null;

	public TokenLayout(Token<F> token, boolean tokenCloseButton) {
		super();
		this.token = token;
		lbl.setValue(token.getValue().getStrRepresentation());

		setData(token);
		addStyleName("flat");
		addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		if (token.getStyle() != null && !token.getStyle().isEmpty())
			addStyleName(token.getStyle());

		if (tokenCloseButton) {
			btn = new NativeButton();
			btn.setIcon(VaadinIcons.CLOSE);
			addComponents(lbl, btn);
		} else {
			addComponents(lbl);
		}

	}

	public Label getLbl() {
		return lbl;
	}

	public void setLbl(Label lbl) {
		this.lbl = lbl;
	}

	public NativeButton getBtn() {
		return btn;
	}

	public void setBtn(NativeButton btn) {
		this.btn = btn;
	}

	public Token<F> getToken() {
		return token;
	}

	public void setToken(Token<F> token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "TokenLayout [" + (lbl != null ? "lbl=" + lbl + ", " : "") + (btn != null ? "btn=" + btn : "") + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((btn == null) ? 0 : btn.hashCode());
		result = prime * result + ((lbl == null) ? 0 : lbl.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof TokenLayout))
			return false;
		TokenLayout other = (TokenLayout) obj;
		if (btn == null) {
			if (other.btn != null)
				return false;
		} else if (!btn.equals(other.btn))
			return false;
		if (lbl == null) {
			if (other.lbl != null)
				return false;
		} else if (!lbl.equals(other.lbl))
			return false;
		if (token == null) {
			return other.token == null;
		} else return token.equals(other.token);
	}

}
