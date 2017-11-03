package com.tylersuehr.emptystaterecycler.loading;
import android.content.Context;

/**
 * Copyright © 2017 Tyler Suehr
 *
 * Factory for returning new stock instances of {@link AbstractContentLoadingState}.
 *
 * @author Tyler Suehr
 * @version 1.0
 */
public final class ContentLoadingStateFactory {
    private ContentLoadingStateFactory() {}

    /**
     * Creates new {@link AbstractContentLoadingState} that renders list mockup
     * content items.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentLoadingState}
     */
    public static AbstractContentLoadingState newListLoadingState(Context c) {
        return new ListContentLoadingState(c);
    }

    /**
     * Creates new {@link AbstractContentLoadingState} that renders single list
     * mockup content items.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentLoadingState}
     */
    public static AbstractContentLoadingState newSingleListLoadingState(Context c) {
        return new SingleListContentLoadingState(c);
    }

    /**
     * Creates new {@link AbstractContentLoadingState} that renders multi-line
     * card mockup content items.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentLoadingState}
     */
    public static AbstractContentLoadingState newCardLoadingState(Context c) {
        return new CardContentLoadingState(c);
    }

    /**
     * Creates new {@link AbstractContentLoadingState} that renders a document
     * -like mockup content item.
     *
     * @param c {@link Context}
     * @return {@link AbstractContentLoadingState}
     */
    public static AbstractContentLoadingState newDocLoadingState(Context c) {
        return new DocContentLoadingState(c);
    }
}