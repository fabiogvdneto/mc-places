package me.fabiogvdneto.places.common.repository;

import java.io.IOException;
import java.util.Collection;

public interface KeyedRepository<K, D> extends Repository {

    Collection<K> keys();

    SingleRepository<D> select(K key);

    void purge(int days) throws IOException;

}
