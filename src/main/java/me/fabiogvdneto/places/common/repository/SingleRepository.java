package me.fabiogvdneto.places.common.repository;

public interface SingleRepository<V> extends Repository {

    void store(V data) throws Exception;

    V fetch() throws Exception;

}
