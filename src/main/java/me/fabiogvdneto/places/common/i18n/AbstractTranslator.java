package me.fabiogvdneto.places.common.i18n;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTranslator implements Translator {

    protected final Map<String, String> translations = new HashMap<>();

    @Override
    public Collection<String> keys() {
        return translations.keySet();
    }

    @Override
    public Collection<String> translations() {
        return translations.values();
    }

    @Override
    public String get(String key) {
        return translations.get(key);
    }
}
