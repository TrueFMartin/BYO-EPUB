package epub4j.browsersupport;

/**
 * Implemented by classes that want to be notified if the user moves to
 * another location in the book.
 *
 * @author paul
 *
 */
public interface NavigationEventListener {

  /**
   * Called whenever the user navigates to another position in the book.
   *
   * @param navigationEvent
   */
  void navigationPerformed(NavigationEvent navigationEvent);
}