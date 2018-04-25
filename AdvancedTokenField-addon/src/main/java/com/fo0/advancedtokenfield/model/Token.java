package com.fo0.advancedtokenfield.model;

import java.io.Serializable;

public class Token<T extends ITokenItem> implements Serializable {

	private static final long serialVersionUID = -7438343157114436699L;

	private T value;
	private String style;

	public Token(T value) {
		super();
		this.value = value;
	}

	public Token(T value, String style) {
		super();
		this.value = value;
		this.style = style;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Token))
			return false;
		Token other = (Token) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Token [" + (value != null ? "value=" + value + ", " : "") + (style != null ? "style=" + style : "")
				+ "]";
	}

}
