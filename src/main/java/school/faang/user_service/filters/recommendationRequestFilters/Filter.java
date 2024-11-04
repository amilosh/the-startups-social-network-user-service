package school.faang.user_service.filters.recommendationRequestFilters;

import java.util.stream.Stream;

public interface Filter<T, F> {
    boolean isApplicable(T filterDto);

    Stream<F> apply(Stream<F> requests, T filterDto);
}
