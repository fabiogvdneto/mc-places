package me.fabiogvdneto.places.repository.java;

import me.fabiogvdneto.places.common.repository.java.JavaSingleRepository;
import me.fabiogvdneto.places.repository.WarpRepository;
import me.fabiogvdneto.places.repository.data.WarpData;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

public class JavaWarpSingleRepository extends JavaSingleRepository<Collection<WarpData>> implements WarpRepository {
    public JavaWarpSingleRepository(Path path) {
        super(path);
    }

    @Override
    public Collection<WarpData> fetch() throws IOException {
        Collection<WarpData> data = super.fetch();
        return (data == null) ? Collections.emptyList() : data;
    }
}
