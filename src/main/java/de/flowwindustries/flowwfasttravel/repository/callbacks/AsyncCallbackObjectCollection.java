package de.flowwindustries.flowwfasttravel.repository.callbacks;

import java.util.Collection;

public interface AsyncCallbackObjectCollection<T> {
    void done(Collection<T> result);
}
