/**
 * Copyright (C) 2014 - 2020 DLSC Software & Consulting GmbH (dlsc.com)
 *
 * This file is part of FlexGanttFX.
 */
package com.flexganttfx.model.exception;

/**
 * An exception type used in the context of {@link com.flexganttfx.model.ActivityRepository}
 * whenever something goes wrong inside the repository.
 *
 * @since 1.0
 */
public class RepositoryException extends RuntimeException {

    private static final long serialVersionUID = 941963346782727997L;

    /**
     * Constructs a new exeption.
     *
     * @param text the error message
     * @since 1.0
     */
    public RepositoryException(String text) {
        super(text);
    }
}
