/**
 * A very small module used for supporting logging and licensing.
 */
module com.flexganttfx.core {

    requires license4j;
    requires transitive java.logging;

    exports com.flexganttfx.core;
}