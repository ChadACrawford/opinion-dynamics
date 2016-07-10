package edu.utulsa.masters.opdyn.sim;

/**
 * An interface to help with tracking different types of independent variables easily.
 *
 * However, this was a bad idea. I should have created a Parameters class and tracked/updated all parameters via
 * copies of that.
 */
public interface IndepedentVariable {
    /**
     * Update the independent variable by size V.
     * @param I The independent variable to update.
     * @param V The amount of update that variable by.
     * @return An instance of this type with the updated value.
     */
    public IndepedentVariable updateValue(Independent I, double V);
}
