package me.fabiogvdneto.places.repository.java;

import me.fabiogvdneto.places.common.repository.JavaRepository;
import me.fabiogvdneto.places.repository.WarpRepository;
import me.fabiogvdneto.places.repository.data.WarpData;

import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class JavaWarpRepository extends JavaRepository<Collection<WarpData>> implements WarpRepository {

    public JavaWarpRepository(File file) {
        super(file);
    }

    @Override
    public Collection<WarpData> createDefault() {
        return Collections.emptyList();
    }
}
