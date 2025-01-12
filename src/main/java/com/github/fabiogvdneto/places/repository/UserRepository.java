package com.github.fabiogvdneto.places.repository;

import com.github.fabiogvdneto.places.common.repository.KeyedRepository;
import com.github.fabiogvdneto.places.repository.data.UserData;

import java.util.UUID;

public interface UserRepository extends KeyedRepository<UUID, UserData> {

    int purge(int days) throws Exception;

}
