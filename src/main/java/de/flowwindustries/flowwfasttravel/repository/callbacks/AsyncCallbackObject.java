package de.flowwindustries.flowwfasttravel.repository.callbacks;

import java.util.Optional;

public interface AsyncCallbackObject<T> {
    void done(T result);
    void done(Optional<T> optionalResult);
}
