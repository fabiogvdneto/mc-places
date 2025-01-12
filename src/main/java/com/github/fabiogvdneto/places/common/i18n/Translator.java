package com.github.fabiogvdneto.places.common.i18n;

import java.util.Collection;

public interface Translator {

    String code();

    Collection<String> keys();

    Collection<String> translations();

    String get(String key);

}
