package asteroid;

/**
 * Represents the possible type of {@link asteroid.A.TO} the transformation can be applied to
 *
 * @since 0.1.0
 */
public @interface Apply {
    /**
     * Sets the target the AST transformation is applied to
     *
     * @return an instance of {@link asteroid.A.TO}
     */
    A.TO value();
}
