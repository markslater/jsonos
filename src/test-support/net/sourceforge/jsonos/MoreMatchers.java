package net.sourceforge.jsonos;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;

public final class MoreMatchers {
    public static <T> Matcher<T> equalsReflectively(final T expected) {
        return new TypeSafeDiagnosingMatcher<T>() {
            @Override
            protected boolean matchesSafely(final T item, final Description mismatchDescription) {
                final boolean result = reflectionEquals(expected, item);
                if (!result) {
                    mismatchDescription.appendValue(item);
                }
                return result;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
