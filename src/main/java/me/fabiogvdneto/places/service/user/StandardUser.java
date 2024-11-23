package me.fabiogvdneto.places.service.user;

import me.fabiogvdneto.places.model.Home;
import me.fabiogvdneto.places.model.TeleportationRequest;
import me.fabiogvdneto.places.model.User;
import me.fabiogvdneto.places.model.exception.HomeAlreadyExistsException;
import me.fabiogvdneto.places.model.exception.HomeNotFoundException;
import me.fabiogvdneto.places.model.exception.TeleportationRequestAlreadyExistsException;
import me.fabiogvdneto.places.model.exception.TeleportationRequestNotFoundException;
import me.fabiogvdneto.places.repository.data.HomeData;
import me.fabiogvdneto.places.repository.data.UserData;
import org.bukkit.Location;

import java.time.Duration;
import java.util.*;

class StandardUser implements User {

    private final UUID uid;
    private final Map<String, Home> homes;
    private final Map<UUID, TeleportationRequest> tprequests;

    StandardUser(UserData userData) {
        this(userData.uid());

        for (HomeData homeData : userData.homes()) {
            homes.put(homeData.name().toLowerCase(), new StandardHome(homeData));
        }
    }

    StandardUser(UUID uid) {
        this.uid = uid;
        this.homes = new HashMap<>();
        this.tprequests = new HashMap<>();
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public Collection<Home> getHomes() {
        return homes.values();
    }

    @Override
    public Home getHome(String name) throws HomeNotFoundException {
        Home home = homes.get(name.toLowerCase());

        if (home == null)
            throw new HomeNotFoundException();

        return home;
    }

    @Override
    public Home createHome(String name, Location location) throws HomeAlreadyExistsException {
        Home home = new StandardHome(name, location);

        if (homes.putIfAbsent(name.toLowerCase(), home) != null)
            throw new HomeAlreadyExistsException();

        return home;
    }

    @Override
    public void deleteHome(String name) throws HomeNotFoundException {
        if (homes.remove(name.toLowerCase()) == null)
            throw new HomeNotFoundException();
    }

    public Collection<TeleportationRequest> getTeleportationRequests() {
        return tprequests.values();
    }

    public TeleportationRequest getTeleportationRequest(UUID sender) throws TeleportationRequestNotFoundException {
        TeleportationRequest request = tprequests.get(sender);

        if (request == null)
            throw new TeleportationRequestNotFoundException();

        return request;
    }

    public TeleportationRequest createTeleportationRequest(UUID sender, Duration duration)
            throws TeleportationRequestAlreadyExistsException {
        TeleportationRequest request = tprequests.get(sender);

        if (request != null && !request.hasExpired())
            throw new TeleportationRequestAlreadyExistsException(request);

        request = new StandardTeleportationRequest(sender, uid, duration);
        tprequests.put(sender, request);
        return request;
    }

    UserData memento() {
        List<HomeData> homeData = homes.values().stream().map(home -> ((StandardHome) home).memento()).toList();
        return new UserData(uid, homeData);
    }
}
