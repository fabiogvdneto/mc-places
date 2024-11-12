package me.fabiogvdneto.places.repository;

import me.fabiogvdneto.places.common.repository.KeyedRepository;
import me.fabiogvdneto.places.repository.data.UserData;

import java.util.UUID;

public interface UserRepository extends KeyedRepository<UUID, UserData> {

}
