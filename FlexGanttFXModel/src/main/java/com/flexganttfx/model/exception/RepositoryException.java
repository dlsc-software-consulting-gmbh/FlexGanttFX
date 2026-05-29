/**
 * License Notice for FlexGanttFX
 *
 * The FlexGanttFX software library is distributed under a dual licensing model.
 *
 * 1. Commercial Use
 *    Use of FlexGanttFX in proprietary or commercial applications requires the purchase of a commercial license.
 *    The applicable terms and conditions can be found on the product's homepage at <https://www.flexganttfx.com/pages/licensing/>.
 *
 * 2. Open Source Use
 *    For use in open source projects, FlexGanttFX is made available under the **GNU AFFERO GENERAL PUBLIC LICENSE V3**.
 *    The full text of the license is available at:
 *    <https://github.com/dlemmermann/FlexGanttFX/blob/master/LICENSE>
 *
 * By using FlexGanttFX, the licensee accepts and agrees to the applicable licensing terms.
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
