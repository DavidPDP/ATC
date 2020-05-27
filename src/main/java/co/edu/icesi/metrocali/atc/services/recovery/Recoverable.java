package co.edu.icesi.metrocali.atc.services.recovery;

/**
 * Recoverability of a class is enabled by the class implementing the 
 * <code>co.edu.icesi.metrocali.atc.services.recovery.Recoverable</code> 
 * interface. The recoverability interface has no methods or fields and 
 * serves only to identify the semantics of being recoverable.
 * A recoverable entity is one that when initializing the system 
 * its state will be recovered. This applies to system crashes.
 */
public interface Recoverable {
}
