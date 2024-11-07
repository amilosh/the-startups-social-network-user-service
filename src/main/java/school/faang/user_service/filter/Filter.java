package school.faang.user_service.filter;

import java.util.stream.Stream;

public interface Filter<R, F> {

    boolean isApplicable(F filter);

    Stream<R> apply(Stream<R> goalInvitations, F filter);
}
