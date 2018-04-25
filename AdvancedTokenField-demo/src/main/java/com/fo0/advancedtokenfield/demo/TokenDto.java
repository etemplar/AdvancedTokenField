package com.fo0.advancedtokenfield.demo;

import com.fo0.advancedtokenfield.model.ITokenItem;

/**
 * Created by Anton Nikitin on 25.04.2018.
 */
public class TokenDto implements ITokenItem {

    private String value;

    public TokenDto(String value) {
        this.value = value;
    }

    @Override
    public String getStrRepresentation() {
        return value;
    }

    @Override
    public String getDbRepresentation() {
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TokenDto{" +
                "value='" + value + '\'' +
                '}';
    }
}
