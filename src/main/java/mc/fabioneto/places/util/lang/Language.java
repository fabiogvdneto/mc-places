package mc.fabioneto.places.util.lang;

public interface Language {

    String getCode();

    Message translate(String key);

}
