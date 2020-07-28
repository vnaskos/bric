package org.bric.core.process;

import java.util.function.Supplier;

public interface SaveService<T, P extends SaveService.Parameters> {

    void save(Supplier<T> supplier, P parameters);

    interface Parameters {}
}
